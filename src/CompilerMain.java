import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class CompilerMain {
    public static void main(String[] args) {
        String code = "";
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("Ingrese la ruta del archivo .txt (o 'salir' para terminar): ");
                String filePath = scanner.nextLine();

                if (filePath.equalsIgnoreCase("salir")) {
                    System.out.println("Saliendo de la aplicación.");
                    return;
                }

                if (!filePath.toLowerCase().endsWith(".txt")) {
                    System.out.println("Por favor, ingrese un archivo .txt válido.");
                    continue;
                }

                File file = new File(filePath);
                try (Scanner fileScanner = new Scanner(file)) {
                    StringBuilder codeBuilder = new StringBuilder();
                    while (fileScanner.hasNextLine()) {
                        codeBuilder.append(fileScanner.nextLine()).append("\n");
                    }
                    code = codeBuilder.toString();
                    if (code.trim().isEmpty()) {
                        System.out.println("El archivo está vacío.");
                        continue;
                    }
                    break;
                } catch (FileNotFoundException e) {
                    System.out.println("Archivo no encontrado. Intente de nuevo.");
                }
            }

            printSeparator("Contenido del Archivo");
            System.out.println(code);

            Lexer lexer = new Lexer(code);
            Token[] tokens = lexer.tokenize();

            printSeparator("Análisis Léxico");
            lexer.printErrors();
            lexer.printTokens();

            if (lexer.hasErrors()) {
                System.out.println("\nEl análisis léxico falló. Deteniendo la compilación.");
                return;
            }

            CompilerUtils.CompilerResult result = new CompilerUtils.CompilerResult();

            try {
                printSeparator("Análisis Sintáctico");
                Parser parser = new Parser(tokens);
                parser.parse();
                parser.printParseResults();
                System.out.println("\nAnálisis sintáctico efectuado correctamente");
                result.addParseResult("Análisis sintáctico completado exitosamente");
            } catch (CompilerUtils.CompilerError e) {
                System.err.println("\nError de compilación: " + e.getMessage());
                result.addError(e.getMessage());
            } catch (Exception e) {
                System.err.println("\nError inesperado: " + e.getMessage());
                result.addError("Error inesperado: " + e.getMessage());
            }

            try {
                printSeparator("Análisis Semántico");
                SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
                semanticAnalyzer.analyze(tokens);
                System.out.println("\nAnálisis semántico efectuado correctamente");
                
                printSeparator("Resumen de Compilación");
                printCompilationSummary(result);
            } catch (Exception e) {
                System.err.println("\nError durante el procesamiento: " + e.getMessage());
                result.addError(e.getMessage());
            }
        }
    }

    private static void printSeparator(String title) {
        System.out.println("\n" + "=".repeat(20) + " " + title + " " + "=".repeat(20));
    }

    private static void printCompilationSummary(CompilerUtils.CompilerResult result) {
        if (result.getErrors().isEmpty()) {
            System.out.println("Compilación completada exitosamente");
            System.out.println("\nResultados:");
            for (String parseResult : result.getParseResults()) {
                System.out.println("   " + parseResult);
            }
        } else {
            System.out.println("La compilación falló con errores:");
            for (String error : result.getErrors()) {
                System.out.println("  " + error);
            }
        }
    }
}
