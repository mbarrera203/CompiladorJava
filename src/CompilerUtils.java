import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompilerUtils {
    // Especificaciones del compilador
    public static final String[] RESERVED_KEYWORDS = {"long", "double", "if", "then", "else", "while", "break", "read", "write"};
    public static final String[] OPERATORS = {"+", "-", "*", "/", ">", "<", ">=", "<=", "==", "!=", "<>"};
    public static final String[] SYMBOLS = {"{", "}", ";", "(", ")"};

    // Clase interna para manejar errores
    public static class CompilerError extends RuntimeException {
        private int line;
        private int column;
        
        public CompilerError(String message, int line, int column) {
            super(String.format("Error en línea %d, columna %d: %s", line, column, message));
            this.line = line;
            this.column = column;
        }

        public int getLine() { return line; }
        public int getColumn() { return column; }
    }

    // Clase interna para manejar resultados
    public static class CompilerResult {
        private List<Token> tokens = new ArrayList<>();
        private List<String> parseResults = new ArrayList<>();
        private List<String> errors = new ArrayList<>();

        public void addToken(Token token) { tokens.add(token); }
        public void addParseResult(String result) { parseResults.add(result); }
        public void addError(String error) { errors.add(error); }

        public List<Token> getTokens() { return tokens; }
        public List<String> getParseResults() { return parseResults; }
        public List<String> getErrors() { return errors; }
    }

    // Métodos de validación
    public static boolean isReservedKeyword(String word) {
        return Arrays.asList(RESERVED_KEYWORDS).contains(word);
    }

    public static boolean isOperator(String op) {
        return Arrays.asList(OPERATORS).contains(op);
    }

    public static boolean isSymbol(String symbol) {
        return Arrays.asList(SYMBOLS).contains(symbol);
    }

    public static boolean isValidIdentifier(String identifier) {
        if (identifier == null || identifier.isEmpty()) return false;
        if (!identifier.startsWith("_")) return false;
        if (identifier.startsWith("__")) return false;
        if (identifier.length() < 2) return false;
        
        String afterUnderscore = identifier.substring(1);
        return afterUnderscore.matches("[a-zA-Z][a-zA-Z0-9]*");
    }
} 