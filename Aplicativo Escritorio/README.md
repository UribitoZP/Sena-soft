# Santa Ana Hotel Management - Módulo de Autenticación

Este es el módulo de acceso inicial para la aplicación de escritorio del Hotel Santa Ana.

## Características
* **Selección de Rol**: Permite elegir entre Administrador y Recepcionista.
* **Validación**: Comprueba la selección y campos antes de procesar el ingreso.
* **Controlador Separado**: La lógica de negocio está desacoplada de la interfaz visual (`LoginController`).
* **Enrutamiento Dinámico**: Dirige a diferentes vistas (vistas de confirmación simplificadas) según el perfil.
* **Branding**: Incluye el logo oficial y una estética profesional personalizada.

## Requisitos
* Java JDK 17 o superior.

## Cómo Ejecutar
1. Abre una terminal en este directorio.
2. Ejecuta el archivo `run.bat` (en Windows) o compila manualmente:
   ```bash
   javac -d bin -sourcepath src src/com/santaana/main/ApplicationMain.java
   java -cp bin com.santaana.main.ApplicationMain
   ```

## Estructura del Proyecto
* `src/com/santaana/main`: Punto de entrada de la aplicación.
* `src/com/santaana/view`: Clases Swing para la interfaz gráfica.
* `src/com/santaana/controller`: Lógica de control y navegación.
* `resources`: Activos visuales (logo, etc).
