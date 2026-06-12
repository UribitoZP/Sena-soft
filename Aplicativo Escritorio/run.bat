@echo off
setlocal

set "SRC_DIR=src"
set "BIN_DIR=bin"
set "LIB_DIR=lib"
set "RESOURCES_DIR=resources"
set "MAIN_CLASS=com.santaana.main.ApplicationMain"
set "CLASSPATH=.;..\%LIB_DIR%\jcalendar-1.4.jar;..\%LIB_DIR%\sqlite-jdbc-3.45.1.0.jar;..\%LIB_DIR%\slf4j-api-1.7.36.jar;..\%LIB_DIR%\slf4j-nop-1.7.36.jar;..\%LIB_DIR%\itextpdf-5.5.13.3.jar"

echo [+] Creando directorio de binarios...
if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"

echo [+] Compilando proyecto...
javac -cp ".;%LIB_DIR%\jcalendar-1.4.jar;%LIB_DIR%\sqlite-jdbc-3.45.1.0.jar;%LIB_DIR%\slf4j-api-1.7.36.jar;%LIB_DIR%\slf4j-nop-1.7.36.jar;%LIB_DIR%\itextpdf-5.5.13.3.jar" ^
    -d "%BIN_DIR%" ^
    -sourcepath "%SRC_DIR%" ^
    "%SRC_DIR%\com\santaana\main\ApplicationMain.java" ^
    "%SRC_DIR%\com\santaana\controller\LoginController.java" ^
    "%SRC_DIR%\com\santaana\db\DatabaseConnection.java" ^
    "%SRC_DIR%\com\santaana\db\DatabaseException.java" ^
    "%SRC_DIR%\com\santaana\db\SchemaManager.java" ^
    "%SRC_DIR%\com\santaana\db\SeedData.java" ^
    "%SRC_DIR%\com\santaana\model\Usuario.java" ^
    "%SRC_DIR%\com\santaana\model\Habitacion.java" ^
    "%SRC_DIR%\com\santaana\model\Reserva.java" ^
    "%SRC_DIR%\com\santaana\model\Actividad.java" ^
    "%SRC_DIR%\com\santaana\model\Cliente.java" ^
    "%SRC_DIR%\com\santaana\model\Producto.java" ^
    "%SRC_DIR%\com\santaana\dao\UsuarioDAO.java" ^
    "%SRC_DIR%\com\santaana\dao\HabitacionDAO.java" ^
    "%SRC_DIR%\com\santaana\dao\ReservaDAO.java" ^
    "%SRC_DIR%\com\santaana\dao\HistorialDAO.java" ^
    "%SRC_DIR%\com\santaana\dao\ClienteDAO.java" ^
    "%SRC_DIR%\com\santaana\util\ThemeManager.java" ^
    "%SRC_DIR%\com\santaana\util\PasswordUtil.java" ^
    "%SRC_DIR%\com\santaana\util\DateUtil.java" ^
    "%SRC_DIR%\com\santaana\util\ErrorUtil.java" ^
    "%SRC_DIR%\com\santaana\view\LoginFrame.java" ^
    "%SRC_DIR%\com\santaana\view\MainFrame.java" ^
    "%SRC_DIR%\com\santaana\view\TableroPanel.java" ^
    "%SRC_DIR%\com\santaana\view\TableroFrame.java" ^
    "%SRC_DIR%\com\santaana\view\ReservaPanel.java" ^
    "%SRC_DIR%\com\santaana\view\ReservaFrame.java" ^
    "%SRC_DIR%\com\santaana\view\GestHabitacionPanel.java" ^
    "%SRC_DIR%\com\santaana\view\GestHabitacion.java" ^
    "%SRC_DIR%\com\santaana\view\NotificacionPanel.java" ^
    "%SRC_DIR%\com\santaana\view\NotificacionFrame.java" ^
    "%SRC_DIR%\com\santaana\view\NuevaReservaDialog.java" ^
    "%SRC_DIR%\com\santaana\view\UsuarioDialog.java" ^
    "%SRC_DIR%\com\santaana\view\GestionUsuarioPanel.java" ^
    "%SRC_DIR%\com\santaana\view\InfoHabitacionFrame.java" ^
    "%SRC_DIR%\com\santaana\view\HistorialPanel.java" ^
    "%SRC_DIR%\com\santaana\server\JsonUtil.java" ^
    "%SRC_DIR%\com\santaana\server\TokenManager.java" ^
    "%SRC_DIR%\com\santaana\server\AuthHandler.java" ^
    "%SRC_DIR%\com\santaana\server\HabitacionesHandler.java" ^
    "%SRC_DIR%\com\santaana\server\ReservasHandler.java" ^
    "%SRC_DIR%\com\santaana\server\StatsHandler.java" ^
    "%SRC_DIR%\com\santaana\server\ReportesHandler.java" ^
    "%SRC_DIR%\com\santaana\server\RestServer.java"

if %errorlevel% neq 0 (
    echo [!] Error de compilacion.
    pause
    exit /b %errorlevel%
)

echo [+] Copiando recursos...
xcopy /s /e /y "%RESOURCES_DIR%" "%BIN_DIR%\resources\" >nul 2>&1
xcopy /s /e /y "%SRC_DIR%\main\resources\resources\*" "%BIN_DIR%\resources\" >nul 2>&1

echo [+] Iniciando aplicacion...
cd "%BIN_DIR%"
java -cp "%CLASSPATH%" "%MAIN_CLASS%"

endlocal
