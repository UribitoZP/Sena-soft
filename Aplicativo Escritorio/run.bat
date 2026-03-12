@echo off
setlocal

set "SRC_DIR=src"
set "BIN_DIR=bin"
set "LIB_DIR=lib"
set "RESOURCES_DIR=resources"
set "MAIN_CLASS=com.santaana.main.ApplicationMain"

echo [+] Creando directorio de binarios...
if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"

echo [+] Compilando proyecto...
javac -cp ".;%LIB_DIR%\jcalendar-1.4.jar" -d "%BIN_DIR%" -sourcepath "%SRC_DIR%" ^
    "%SRC_DIR%\com\santaana\main\ApplicationMain.java" ^
    "%SRC_DIR%\com\santaana\view\LoginFrame.java" ^
    "%SRC_DIR%\com\santaana\view\HomeFrame.java" ^
    "%SRC_DIR%\com\santaana\controller\LoginController.java"

if %errorlevel% neq 0 (
    echo [!] Error de compilacion.
    pause
    exit /b %errorlevel%
)

echo [+] Copiando recursos...
xcopy /s /e /y "%RESOURCES_DIR%" "%BIN_DIR%\resources\" >nul 2>&1

echo [+] Iniciando aplicacion...
cd "%BIN_DIR%"
java -cp ".;..\%LIB_DIR%\jcalendar-1.4.jar" "%MAIN_CLASS%"

endlocal
