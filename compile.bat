@echo off
echo Creando directorio bin...
mkdir bin 2>nul

echo Compilando archivos Java...
javac -d bin src\*.java

echo Creando manifest...
echo Main-Class: CompilerMain> manifest.txt

echo Creando JAR...
"C:\Program Files\Java\jdk-21\bin\jar" cvfm Compiler.jar manifest.txt -C bin .

echo.
echo Si no hubo errores, el programa fue compilado exitosamente.
echo Para ejecutar el programa use: java -jar Compiler.jar
pause