# ISSUE: Problemas en la Base de Datos — CORREGIDOS

Todos los fixes aplicados en la rama `fix/base-de-datos-esquema-indices-fks`.

---

## ✅ Correcciones Aplicadas

### 🔴 Críticos

### 1. Bug en `listarReservasActivas()` — Filtro con mayúsculas
**Fix:** `ReservaDAO.java:43` — `'ACTIVA'` → `'Activa'`

### 2. SQLite no valida Foreign Keys
**Fix:** `DatabaseConnection.java:47` — Se agregó `PRAGMA foreign_keys = ON` al abrir la conexión.

### 🟡 Moderados

### 3. Sin índices en columnas de búsqueda frecuente
**Fix:** Se crearon 7 índices en migración v10:
| Índice | Tabla | Columna |
|--------|-------|---------|
| `idx_reservas_fecha_entrada` | reservas | fecha_entrada |
| `idx_reservas_id_habitacion` | reservas | id_habitacion |
| `idx_reservas_estado` | reservas | estado |
| `idx_historial_fecha_hora` | historial | fecha_hora |
| `idx_historial_tipo` | historial | tipo |
| `idx_reserva_clientes_id_reserva` | reserva_clientes | id_reserva |
| `idx_reserva_productos_id_reserva` | reserva_productos | id_reserva |

### 4. Sin `ON DELETE CASCADE` en tablas puente
**Fix:** Se agregó `ON DELETE CASCADE` en `reserva_clientes` y `reserva_productos` al recrearlas en v10.

### 5. Modelo `Reserva.java` no refleja la FK real de la BD *(Informativo)*
No se modificó. El modelo sigue usando `clienteNombre`/`clienteDoc` con JOINs. Es una deuda técnica de código, no de BD.

### 6. `hora_salida` con DEFAULT '12:00' aunque `fecha_salida` sea NULL *(Informativo)*
No se modificó. La lógica de negocio de "Indefinido" maneja esto correctamente desde la aplicación.

### 7. `reservas.id_cliente` sin `NOT NULL` en migración para BD existentes
**Fix:** La migración v9 ya había recreado la tabla con `NOT NULL`. La v10 refuerza con índices.

### 🟡 Menores

### 8. `reserva_clientes.tipo_persona` sin CHECK
**Fix:** Migración v10 recrea la tabla con `CHECK(tipo_persona IN ('Titular','Acompanante'))`.

### 9. `habitaciones.tipo` sin CHECK
**Fix:** Migración v10 recrea la tabla con `CHECK(tipo IN ('Simple','Doble','Suite'))`.

### 10. `usuarios.telefono` y `usuarios.correo` con `DEFAULT ''` en vez de NULL *(Pendiente)*
No se modificó por ser un cambio que requiere ajustes en la capa de UI/validación.

### 11. Falta `UNIQUE(id_reserva, id_cliente)` en `reserva_clientes`
**Fix:** Migración v10 recrea la tabla con `UNIQUE(id_reserva, id_cliente)`.

### 12. Fechas sin validación de formato ISO
**Fix:** Migración v11 agrega `CHECK(fecha_entrada IS date(fecha_entrada))` y `CHECK(fecha_salida IS date(fecha_salida))` en `reservas`. Usa el operador `IS` de SQLite que valida correctamente incluso con NULLs.

### 13. `historial.id_usuario` con `DEFAULT 0` en vez de NULL
**Fix:** Migración v11 recrea `historial` con `DEFAULT NULL`. Además `HistorialDAO` cambió el parámetro `int idUsuario` a `Integer` para pasar `NULL` en vez de `0` cuando no hay usuario.

### 14. Migraciones fallarían con `PRAGMA foreign_keys = ON`
**Fix:** Se agregó `PRAGMA foreign_keys = OFF` al inicio de `SchemaManager.inicializar()` y `ON` al final, para que los `DROP TABLE` en migraciones existentes (v6d, v9) no fallen.

### 💡 Recomendaciones adicionales

### `ReservaDAO.crear()` sin transacción
**Fix:** Se agregó `conn.setAutoCommit(false)` + `conn.commit()` / `conn.rollback()` para que la inserción de la reserva y su relación con cliente sean atómicas.
