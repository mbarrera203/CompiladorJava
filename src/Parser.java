import java.util.ArrayList;
import java.util.List;

public class Parser {
    private Token[] tokens;
    private int pos;
    private List<String> parseResults = new ArrayList<>();

    public Parser(Token[] tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    public void parse() {
        try {
            while (pos < tokens.length && tokens[pos] != null) {
                parseStatement();
            }
        } catch (CompilerUtils.CompilerError e) {
            addParseResult("Error de parsing: " + e.getMessage());
        }
    }

    private void parseStatement() {
        Token current = tokens[pos];
        if (current == null) return;

        switch (current.getType()) {
            case "KEYWORD":
                switch (current.getValue()) {
                    case "long":
                    case "double":
                        parseVariableDeclaration();
                        addParseResult("Declaración de variable válida");
                        break;
                    case "if":
                        parseIfStatement();
                        addParseResult("Estructura if-then-else válida");
                        break;
                    case "while":
                        parseWhileStatement();
                        addParseResult("Estructura while válida");
                        break;
                    case "write":
                        parseWriteStatement();
                        addParseResult("Instrucción write válida");
                        break;
                    default:
                        error("Palabra clave no reconocida: " + current.getValue());
                }
                break;
            case "IDENTIFIER":
                parseAssignment();
                addParseResult("Asignación válida encontrada");
                break;
            default:
                error("Sentencia no reconocida: " + current.getType());
        }
    }

    private void parseWriteStatement() {
        expect("KEYWORD", "write");
        expect("IDENTIFIER");
        expect("SYMBOL", ";");
    }

    private void parseAssignment() {
        expect("IDENTIFIER");
        if (pos < tokens.length && tokens[pos] != null && 
            tokens[pos].getType().equals("OPERATOR")) {
            
            // Verificar el operador de asignación
            if (!tokens[pos].getValue().equals("=")) {
                error("Se esperaba operador de asignación '='");
            }
            
            pos++;
            parseExpression();
            expect("SYMBOL", ";");
        } else {
            error("Se esperaba operador de asignación '='");
        }
    }

    private void parseExpression() {
        if (pos >= tokens.length || tokens[pos] == null) {
            error("Expresión incompleta");
        }

        if (tokens[pos].getType().equals("NUMBER") || tokens[pos].getType().equals("IDENTIFIER")) {
            pos++;
            if (pos < tokens.length && tokens[pos] != null && 
                tokens[pos].getType().equals("OPERATOR") && 
                (tokens[pos].getValue().equals("+") || 
                 tokens[pos].getValue().equals("-") || 
                 tokens[pos].getValue().equals("*") || 
                 tokens[pos].getValue().equals("/"))) {
                pos++;
                if (pos < tokens.length && (tokens[pos].getType().equals("NUMBER") || 
                    tokens[pos].getType().equals("IDENTIFIER"))) {
                    pos++;
                } else {
                    error("Se esperaba un número o identificador después del operador");
                }
            }
        } else {
            error("Se esperaba un número o identificador");
        }
    }

    private void parseIfStatement() {
        expect("KEYWORD", "if");
        parseCondition();
        expect("KEYWORD", "then");
        expect("SYMBOL", "{");
        
        while (pos < tokens.length && tokens[pos] != null && !tokens[pos].getValue().equals("}")) {
            parseStatement();
        }
        
        expect("SYMBOL", "}");

        if (pos < tokens.length && tokens[pos] != null && tokens[pos].getValue().equals("else")) {
            pos++;
            expect("SYMBOL", "{");
            while (pos < tokens.length && tokens[pos] != null && !tokens[pos].getValue().equals("}")) {
                parseStatement();
            }
            expect("SYMBOL", "}");
        }
    }

    private void parseCondition() {
        expect("IDENTIFIER");
        if (pos >= tokens.length || !tokens[pos].getType().equals("OPERATOR")) {
            error("Se esperaba un operador de comparación");
        }
        
        String operator = tokens[pos].getValue();
        if (operator.equals(">") || operator.equals("<") || operator.equals(">=") || 
            operator.equals("<=") || operator.equals("==") || operator.equals("!=") ||
            operator.equals("<>")) {
            pos++;
        } else {
            error("Operador de comparación no válido: " + operator);
        }
        
        if (pos >= tokens.length || 
            (!tokens[pos].getType().equals("NUMBER") && !tokens[pos].getType().equals("IDENTIFIER"))) {
            error("Se esperaba un número o identificador en la condición");
        }
        pos++;
    }

    private void parseWhileStatement() {
        expect("KEYWORD", "while");
        parseCondition();
        expect("SYMBOL", "{");
        
        while (pos < tokens.length && tokens[pos] != null && !tokens[pos].getValue().equals("}")) {
            parseStatement();
        }
        
        expect("SYMBOL", "}");
    }

    private void parseVariableDeclaration() {
        // Consumir el tipo (long o double)
        pos++;
        
        // Esperar un identificador
        expect("IDENTIFIER");
        
        // Si hay un =, parsear la inicialización
        if (pos < tokens.length && tokens[pos] != null && 
            tokens[pos].getType().equals("OPERATOR") && 
            tokens[pos].getValue().equals("=")) {
            pos++;
            parseExpression();
        }
        
        expect("SYMBOL", ";");
    }

    private void expect(String type) {
        if (pos >= tokens.length || tokens[pos] == null) {
            error("Final inesperado del archivo");
        }
        if (!tokens[pos].getType().equals(type)) {
            error("Se esperaba " + type + ", se encontró " + tokens[pos].getType());
        }
        pos++;
    }

    private void expect(String type, String value) {
        if (pos >= tokens.length || tokens[pos] == null) {
            error("Final inesperado del archivo");
        }
        if (!tokens[pos].getType().equals(type) || !tokens[pos].getValue().equals(value)) {
            error("Se esperaba " + type + ":" + value + 
                  ", se encontró " + tokens[pos].getType() + ":" + tokens[pos].getValue());
        }
        pos++;
    }

    private void error(String message) {
        parseResults.add("Error: " + message);
        throw new CompilerUtils.CompilerError(message, 0, 0);
    }

    private void addParseResult(String result) {
        parseResults.add(result);
    }

    public void printParseResults() {
        for (String result : parseResults) {
            System.out.println(result);
        }
    }
}
