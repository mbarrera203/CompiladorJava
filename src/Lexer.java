import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class Lexer {
    private String code;
    private int pos;
    private Token[] tokens;
    private int tokenCount;
    private int line = 1;
    private int column = 1;
    private List<String> errors = new ArrayList<>();

    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(CompilerUtils.RESERVED_KEYWORDS));

    public Lexer(String code) {
        this.code = code;
        this.pos = 0;
        this.tokens = new Token[100]; // Limite de tokens
        this.tokenCount = 0;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public Token[] tokenize() {
        while (pos < code.length()) {
            char currentChar = code.charAt(pos);

            if (currentChar == '/') {
                if (pos + 1 < code.length()) {
                    if (code.charAt(pos + 1) == '/') {
                        // Comentario de una línea
                        StringBuilder comment = new StringBuilder();
                        pos += 2; // Saltar //
                        while (pos < code.length() && code.charAt(pos) != '\n') {
                            comment.append(code.charAt(pos));
                            pos++;
                        }
                        addToken(new Token("COMMENT", comment.toString().trim(), line, column));
                        continue;
                    } else if (code.charAt(pos + 1) == '*') {
                        // Comentario multilínea
                        StringBuilder comment = new StringBuilder();
                        pos += 2; // Saltar /*
                        int startLine = line;
                        int startColumn = column;
                        while (pos + 1 < code.length() && 
                               !(code.charAt(pos) == '*' && code.charAt(pos + 1) == '/')) {
                            comment.append(code.charAt(pos));
                            if (code.charAt(pos) == '\n') {
                                line++;
                                column = 1;
                            }
                            pos++;
                        }
                        if (pos + 1 < code.length()) {
                            pos += 2; // Saltar */
                        }
                        addToken(new Token("MULTILINE_COMMENT", comment.toString().trim(), startLine, startColumn));
                        continue;
                    }
                }
            }

            if (currentChar == '\n') {
                line++;
                column = 1;
                pos++;
            } else if (isWhitespace(currentChar)) {
                pos++;
                column++;
            } else if (currentChar == '_') {
                String identifier = readWord();
                if (identifier.startsWith("__")) {
                    addError("Identificador no puede comenzar con doble guión bajo: " + identifier);
                } else if (identifier.contains("@") || identifier.contains("#") || 
                          identifier.contains("$") || identifier.contains("%")) {
                    addError("Identificador contiene caracteres inválidos: " + identifier);
                } else if (CompilerUtils.isValidIdentifier(identifier)) {
                    addToken(new Token("IDENTIFIER", identifier, line, column));
                } else {
                    addError("Identificador inválido: " + identifier);
                }
                column += identifier.length();
            } else if (isLetter(currentChar)) {
                String word = readWord();
                if (KEYWORDS.contains(word)) {
                    addToken(new Token("KEYWORD", word, line, column));
                } else {
                    addError("Identificador debe comenzar con guión bajo: " + word);
                }
                column += word.length();
            } else if (isDigit(currentChar)) {
                String number = readNumber();
                addToken(new Token("NUMBER", number, line, column));
                column += number.length();
            } else if (isOperatorStart(currentChar)) {
                String operator = readOperator();
                addToken(new Token("OPERATOR", operator, line, column));
                column += operator.length();
            } else if (isSymbol(currentChar)) {
                addToken(new Token("SYMBOL", String.valueOf(currentChar), line, column));
                column++;
                pos++;
            } else {
                addError("Carácter no reconocido: " + currentChar);
                pos++;
                column++;
            }
        }
        return tokens;
    }

    private String readWord() {
        StringBuilder word = new StringBuilder();
        if (pos < code.length() && code.charAt(pos) == '_') {
            word.append(code.charAt(pos));
            pos++;
        }
        while (pos < code.length() && isLetterOrDigit(code.charAt(pos))) {
            word.append(code.charAt(pos));
            pos++;
        }
        return word.toString();
    }

    private String readNumber() {
        StringBuilder number = new StringBuilder();
        while (pos < code.length() && isDigit(code.charAt(pos))) {
            number.append(code.charAt(pos));
            pos++;
        }
        return number.toString();
    }

    private void addToken(Token token) {
        tokens[tokenCount] = token;
        tokenCount++;
    }

    private boolean isWhitespace(char c) {
        return c == ' ' || c == '\n' || c == '\t';
    }

    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isLetterOrDigit(char c) {
        return isLetter(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isOperatorStart(char c) {
        return c == '>' || c == '<' || c == '=' || c == '!' || c == '+' || c == '-' || c == '*' || c == '/';
    }

    private String readOperator() {
        char firstChar = code.charAt(pos);
        pos++;

        // Para operadores de dos caracteres
        if (pos < code.length()) {
            char secondChar = code.charAt(pos);
            if ((firstChar == '>' || firstChar == '<' || firstChar == '=' || firstChar == '!') && 
                secondChar == '=' ||  // Para >=, <=, ==, !=
                (firstChar == '<' && secondChar == '>')) {  // Para <>
                pos++;
                return String.valueOf(firstChar) + secondChar;
            }
        }

        return String.valueOf(firstChar);
    }

    public void printTokens() {
        if (!errors.isEmpty()) {
            System.out.println("\nErrores encontrados:");
            for (String error : errors) {
                System.out.println(error + "\n");
            }
        } else {
            System.out.println("\nAnálisis léxico efectuado correctamente:\n");
            
            // Imprimir todos los tokens, incluyendo los comentarios
            System.out.println("\nTokens encontrados:");
            for (int i = 0; i < tokenCount; i++) {
                Token token = tokens[i];
                System.out.printf("Token %d: Tipo=%s, Valor='%s'\n", 
                    i + 1, token.getType(), token.getValue());
            }
        }
    }

    private boolean isSymbol(char c) {
        return Arrays.asList(CompilerUtils.SYMBOLS).contains(String.valueOf(c));
    }

    private void addError(String message) {
        errors.add(String.format("Error léxico (línea %d, columna %d): %s", line, column, message));
    }

    public void printErrors() {
        if (!errors.isEmpty()) {
            System.out.println("\nErrores encontrados:");
            for (String error : errors) {
                System.out.println(error + "\n");
            }
        } else {
            System.out.println("\nAnálisis léxico efectuado correctamente:\n");
        }
    }
}
