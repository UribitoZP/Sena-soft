# 🏨 Hotel Santa Ana — Sistema de Gestión Hotelera

**HotelSoft** es un sistema integral de administración hotelera desarrollado como proyecto formativo SENA. El sistema permite gestionar habitaciones, reservas, clientes, usuarios y generar reportes operativos, todo respaldado por una base de datos compartida y una API REST embebida.

---

## 📦 Componentes del Proyecto

```
Sena-soft/
├── Aplicativo Escritorio/   # Aplicación de escritorio (Java + Swing)
├── Aplicativo Movil/        # Aplicación móvil (Flutter / Dart)
├── Documentacion/           # Sitio web de documentación
├── setup-servidor.sh        # Script de configuración de servidor MySQL (Debian)
└── .gitignore
```

---

## 🖥️ Aplicativo de Escritorio

- **Lenguaje:** Java 17+ con Swing
- **Base de datos:** SQLite embebida
- **API REST:** Servidor HTTP integrado en el puerto `8080` que expone endpoints para autenticación, habitaciones, reservas y estadísticas
- **Arquitectura:** MVC con paquetes `view`, `controller`, `model`, `dao`, `db`, `server` y `util`

### Funcionalidades principales
- Autenticación por roles (Administrador / Recepcionista)
- Gestión de habitaciones (CRUD + estados: Disponible, Ocupada, Mantenimiento)
- Gestión de reservas (check-in, check-out, tipos de estadía)
- Gestión de clientes y usuarios
- Historial de movimientos
- Notificaciones internas
- Respaldo automático de base de datos

### Ejecutar
```bash
cd "Aplicativo Escritorio"
run.bat
# o manualmente:
javac -d bin -sourcepath src src/com/santaana/main/ApplicationMain.java
java -cp bin com.santaana.main.ApplicationMain
```

---

## 📱 Aplicativo Móvil

- **Framework:** Flutter 3.10.8+ con patrón BLoC
- **Consume la API REST** del aplicativo de escritorio
- **Tema:** Claro / Oscuro con placeholders shimmer

### Funcionalidades
- Inicio de sesión
- Dashboard con métricas en tiempo real
- Visualización de habitaciones y reservas
- Generación de reportes

### Ejecutar
```bash
cd "Aplicativo Movil/santa_ana_dashboard"
flutter pub get
flutter run
```

---

## 🌐 Documentación

Sitio web estático con documentación completa del proyecto:
- Descripción del sistema
- Especificación de requisitos (SRS)
- Diagramas de casos de uso y entidad-relación (ER/MER)
- Galería de capturas del escritorio y móvil
- Glosario y versionado

Abrir `Documentacion/Documentacion_pagina/index.html` en cualquier navegador.

---

## 🗄️ Script de Servidor (MySQL)

`setup-servidor.sh` automatiza la instalación y configuración de MySQL en Debian/Ubuntu:
- Crea las bases de datos `hotel_santa_ana` y `app_node`
- Crea usuarios con permisos específicos
- Habilita conexiones remotas
- Abre el puerto 3306 en el firewall

```bash
chmod +x setup-servidor.sh
sudo bash setup-servidor.sh
```

---

## 🛠️ Tecnologías

| Tecnología | Uso |
|-----------|-----|
| Java 17+ (Swing) | Aplicación de escritorio |
| SQLite | Base de datos local |
| Flutter / Dart | Aplicación móvil |
| BLoC | Estado en móvil |
| HTML5 / CSS3 / JS | Sitio de documentación |
| MySQL | Base de datos en servidor (opcional) |
| Git | Control de versiones |

---

## 👥 Equipo de Trabajo

Proyecto formativo SENA
Equipo de 6 integrantes.
