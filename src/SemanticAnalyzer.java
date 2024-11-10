import java.util.ArrayList;
import java.util.List;

class SemanticAnalyzer {
    private List<String> semanticErrors = new ArrayList<>();

    public void analyze(Token[] tokens) {
        boolean hasErrors = false;
        for (Token token : tokens) {
            if (token != null) {
                if (token.getType().equals("IDENTIFIER")) {
                    if (token.getValue().startsWith("__")) {
                        addError(token, "Identificador no puede comenzar con doble guión bajo");
                        hasErrors = true;
                    }
                    if (token.getValue().matches(".*[@#$%].*")) {
                        addError(token, "Identificador contiene caracteres inválidos");
                        hasErrors = true;
                    }
                }
            }
        }
        
        if (!hasErrors) {
            System.out.println("No se encontraron errores semánticos");
        } else {
            System.out.println("Se encontraron errores semánticos:");
            for (String error : semanticErrors) {
                System.out.println(" " + error);
            }
        }
    }

    private void addError(Token token, String message) {
        semanticErrors.add(String.format("Error semántico (línea %d, columna %d): %s: %s",
            token.getLine(), token.getColumn(), message, token.getValue()));
        throw new CompilerUtils.CompilerError(message, token.getLine(), token.getColumn());
    }
}
