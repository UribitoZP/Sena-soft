<div align="center">

# 🏨 Hotel Santa Ana
### Sistema de Gestión Hotelera

![Java](https://img.shields.io/badge/Java-JDK%2017+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Flutter](https://img.shields.io/badge/Flutter-3.x-02569B?style=for-the-badge&logo=flutter&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-3.45-003B57?style=for-the-badge&logo=sqlite&logoColor=white)
![Dart](https://img.shields.io/badge/Dart-3.x-0175C2?style=for-the-badge&logo=dart&logoColor=white)

**Sistema integral de gestión hotelera compuesto por un aplicativo de escritorio y un dashboard móvil.**

</div>

---

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Arquitectura](#-arquitectura)
- [Módulos](#-módulos)
- [Tecnologías](#-tecnologías)
- [Requisitos](#-requisitos)
- [Instalación y Ejecución](#-instalación-y-ejecución)
- [Credenciales por defecto](#-credenciales-por-defecto)
- [API REST](#-api-rest)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Equipo](#-equipo)

---

## 📖 Descripción

**Hotel Santa Ana** es un sistema de gestión hotelera desarrollado para digitalizar y optimizar la operación de un hotel. Permite gestionar habitaciones, reservas, usuarios y generar reportes en tiempo real desde cualquier dispositivo.

El sistema está compuesto por dos módulos complementarios:

- 🖥️ **Aplicativo de Escritorio** — Consola de administración central desarrollada en Java + Swing. Actúa como servidor REST y gestiona toda la lógica del hotel.
- 📱 **Aplicativo Móvil** — Dashboard móvil desarrollado en Flutter/Dart que permite al personal consultar el estado del hotel desde cualquier dispositivo Android.

---

## 🏗️ Arquitectura

```
📱 App Móvil (Flutter)
        │
        │  HTTP/REST (WiFi local)
        │  Puerto 8080
        ▼
🖥️ App Escritorio (Java Swing)
        │
        │  JDBC
        ▼
🗄️ Base de Datos (SQLite)
```

El aplicativo de escritorio cumple el rol de **servidor y administrador central**. El móvil se conecta a él mediante una API REST en la red local — ambos dispositivos deben estar en la misma red WiFi.

---

## 📦 Módulos

### 🖥️ Aplicativo de Escritorio

| Módulo | Descripción |
|--------|-------------|
| **Tablero Principal** | Dashboard con stats en tiempo real, grid de habitaciones por estado y alertas de checkouts vencidos |
| **Gestión de Habitaciones** | CRUD completo de habitaciones con filtro de búsqueda y cambio de estados |
| **Reservas** | Calendario visual navegable con todas las reservas del hotel |
| **Nueva Reserva** | Formulario modal con validación de disponibilidad por fechas |
| **Notificaciones** | Centro de notificaciones con filtros y tiempo relativo |
| **Historial** | Log de actividades con búsqueda y filtro por rango de fechas |
| **Gestión de Usuarios** | Administración de usuarios y exportación de backup CSV |
| **Dark / Light Mode** | Toggle de tema disponible en toda la aplicación |

### 📱 Aplicativo Móvil

| Vista | Descripción |
|-------|-------------|
| **Inicio** | Ocupación total, ingresos del día, check-ins y reservas activas |
| **Reservas** | Lista filtrable con tabs, búsqueda y timeline de fechas |
| **Habitaciones** | Lista con imagen, tipo, descripción y estado en tiempo real |
| **Reportes** | KPIs, gráfica de barras de ingresos y gráfica de ocupación |

---

## 🛠️ Tecnologías

| Componente | Tecnología | Versión |
|------------|------------|---------|
| Aplicativo de escritorio | Java + Swing | JDK 17+ |
| Base de datos | SQLite | 3.45.1 |
| Servidor REST | com.sun.net.httpserver | JDK built-in |
| Aplicativo móvil | Flutter / Dart | 3.x |
| Manejo de estado | flutter_bloc | 8.x |
| Calendario Swing | JCalendar | 1.4 |

---

## ✅ Requisitos

### Aplicativo de Escritorio
- Java JDK 17 o superior
- Windows 10/11 (recomendado) · macOS · Linux
- 4 GB RAM mínimo

### Aplicativo Móvil
- Flutter SDK 3.x
- Android 8.0 (API 26) o superior
- Misma red WiFi que el servidor

---

## 🚀 Instalación y Ejecución

### 🖥️ Aplicativo de Escritorio

**1. Clona el repositorio**
```bash
git clone https://github.com/UribitoZP/Sena-soft.git
cd Sena-soft/Aplicativo\ Escritorio
```

**2. Ejecuta en Windows**
```bash
run.bat
```

**3. O compila y ejecuta manualmente**
```bash
javac -cp "lib/*" -d out src/com/santaana/**/*.java
java -cp "out;lib/*" com.santaana.main.ApplicationMain
```

> 💡 La base de datos `santaana.db` se crea automáticamente en la raíz al primer inicio.
> El servidor REST inicia automáticamente en el **puerto 8080**.

---

### 📱 Aplicativo Móvil

**1. Navega al directorio**
```bash
cd Sena-soft/Aplicativo\ Movil/santa_ana_dashboard
```

**2. Instala dependencias**
```bash
flutter pub get
```

**3. Ejecuta la app**
```bash
flutter run
```

**4. Conéctate al servidor**

En la pantalla de login, expande **"IP del servidor"** e ingresa la IP del PC donde corre el escritorio.

```
# Obtener IP en Windows
ipconfig → busca "Dirección IPv4"

# Verificar conexión desde el celular
http://IP_DEL_PC:8080/stats
```

> ⚠️ El celular y el PC deben estar conectados a la **misma red WiFi**.

---

## 🔐 Credenciales por defecto

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| `admin` | `1234` | Administrador |
| `recepcion` | `1234` | Recepcionista |

---

## 🌐 API REST

Base URL: `http://{IP_SERVIDOR}:8080`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/auth/login` | Autenticación de usuario |
| `GET` | `/habitaciones` | Lista todas las habitaciones |
| `GET` | `/reservas` | Lista todas las reservas |
| `GET` | `/stats` | Estadísticas globales e ingresos |

### Ejemplo de respuesta `/stats`
```json
{
  "habitaciones": {
    "total": 15,
    "disponibles": 12,
    "ocupadas": 3,
    "limpieza": 0,
    "mantenimiento": 0
  },
  "reservas": {
    "activas": 3,
    "completadas": 0,
    "canceladas": 0
  },
  "ingresos": {
    "hoy": 120000.00,
    "hoyFormato": "$120.0k",
    "mes": 360000.00,
    "mesFormato": "$360.0k",
    "total": 480000.00,
    "totalFormato": "$480.0k"
  }
}
```

---

## 📁 Estructura del Proyecto

```
Sena-soft/
├── Aplicativo Escritorio/
│   ├── src/com/santaana/
│   │   ├── main/          # Punto de entrada
│   │   ├── model/         # Habitacion · Reserva · Usuario · Actividad
│   │   ├── dao/           # HabitacionDAO · ReservaDAO · UsuarioDAO · HistorialDAO
│   │   ├── db/            # DatabaseConnection · SchemaManager · SeedData
│   │   ├── server/        # RestServer · AuthHandler · HabitacionesHandler
│   │   │                  # ReservasHandler · StatsHandler · JsonUtil
│   │   ├── controller/    # LoginController
│   │   ├── view/          # MainFrame · TableroPanel · ReservaPanel
│   │   │                  # GestHabitacionPanel · NotificacionPanel · HistorialPanel
│   │   │                  # NuevaReservaDialog · InfoHabitacionFrame
│   │   └── util/          # ThemeManager · BackupManager
│   ├── lib/               # sqlite-jdbc · jcalendar · slf4j
│   ├── resources/         # Imágenes y assets
│   └── run.bat            # Script de ejecución Windows
│
└── Aplicativo Movil/
    └── santa_ana_dashboard/
        └── lib/
            ├── core/
            │   ├── services/      # ApiService
            │   ├── theme/         # AppTheme
            │   └── constants/     # AppStrings
            └── feature/home/presentation/
                ├── bloc/          # AppBloc · Events · States
                └── views/         # DashboardView · HomeView · ReservasView
                                   # HabitacionesView · ReportesView · LoginView
```

---

## 👥 Equipo

Desarrollado por **Grupo 2** — Formación SENA 2026

[![GitHub](https://img.shields.io/badge/GitHub-UribitoZP-181717?style=for-the-badge&logo=github)](https://github.com/UribitoZP)

---

<div align="center">

**Hotel Santa Ana** · Sistema de Gestión Hotelera · 2026

</div>
