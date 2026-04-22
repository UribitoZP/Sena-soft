#!/bin/bash
# =============================================================================
#  setup-servidor.sh
#  Configura automáticamente MySQL en Debian para:
#    - Aplicativo Escritorio: Hotel Santa Ana (Java)
#    - Aplicativo en desarrollo (Node.js / Express)
#
#  Uso:
#    chmod +x setup-servidor.sh
#    sudo bash setup-servidor.sh
# =============================================================================

set -e

# ── Colores ───────────────────────────────────────────────────────────────────
VERDE='\033[0;32m'
AMARILLO='\033[1;33m'
ROJO='\033[0;31m'
AZUL='\033[0;34m'
BLANCO='\033[1;37m'
NC='\033[0m'

ok()   { echo -e "${VERDE}  [OK]${NC} $1"; }
info() { echo -e "${AZUL}  [->]${NC} $1"; }
warn() { echo -e "${AMARILLO}  [!]${NC} $1"; }
err()  { echo -e "${ROJO}  [ERROR]${NC} $1"; exit 1; }

# ── Verificar que se ejecuta como root ───────────────────────────────────────
if [ "$EUID" -ne 0 ]; then
  err "Debes ejecutar este script como root. Usa: sudo bash setup-servidor.sh"
fi

# ── Bienvenida ────────────────────────────────────────────────────────────────
clear
echo -e "${BLANCO}"
echo "  ╔══════════════════════════════════════════════════════╗"
echo "  ║       CONFIGURACIÓN DE SERVIDOR  -  SENA-SOFT        ║"
echo "  ║          MySQL para Hotel Santa Ana + Node App        ║"
echo "  ╚══════════════════════════════════════════════════════╝"
echo -e "${NC}"
echo ""

# ── Pedir contraseñas ─────────────────────────────────────────────────────────
info "Vamos a configurar las contraseñas. No se mostrarán mientras escribes."
echo ""

read -s -p "  Contraseña para el administrador de MySQL (root): " MYSQL_ROOT_PASS
echo ""
read -s -p "  Repite la contraseña: " MYSQL_ROOT_PASS2
echo ""
if [ "$MYSQL_ROOT_PASS" != "$MYSQL_ROOT_PASS2" ]; then
  err "Las contraseñas no coinciden. Vuelve a ejecutar el script."
fi

echo ""
read -s -p "  Contraseña para el usuario de la app del Hotel (hotel_user): " HOTEL_DB_PASS
echo ""
read -s -p "  Repite la contraseña: " HOTEL_DB_PASS2
echo ""
if [ "$HOTEL_DB_PASS" != "$HOTEL_DB_PASS2" ]; then
  err "Las contraseñas no coinciden. Vuelve a ejecutar el script."
fi

echo ""
read -s -p "  Contraseña para el usuario de la app Node.js (node_user): " NODE_DB_PASS
echo ""
read -s -p "  Repite la contraseña: " NODE_DB_PASS2
echo ""
if [ "$NODE_DB_PASS" != "$NODE_DB_PASS2" ]; then
  err "Las contraseñas no coinciden. Vuelve a ejecutar el script."
fi

echo ""
echo "  ─────────────────────────────────────────────────────"
echo ""

# ── Actualizar el sistema ─────────────────────────────────────────────────────
info "Actualizando lista de paquetes..."
apt-get update -qq
ok "Sistema actualizado."

# ── Instalar MySQL ────────────────────────────────────────────────────────────
info "Instalando MySQL Server..."
DEBIAN_FRONTEND=noninteractive apt-get install -y mysql-server > /dev/null 2>&1
ok "MySQL instalado correctamente."

# ── Iniciar y habilitar MySQL ─────────────────────────────────────────────────
info "Iniciando el servicio MySQL..."
systemctl enable mysql  > /dev/null 2>&1
systemctl start  mysql
ok "MySQL en ejecución."

# ── Asegurar la instalación y crear usuarios/bases de datos ──────────────────
info "Configurando MySQL (usuarios, bases de datos y permisos)..."

mysql --user=root <<EOF

-- Contraseña del administrador
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '${MYSQL_ROOT_PASS}';

-- Eliminar usuarios anónimos
DELETE FROM mysql.user WHERE User='';

-- Eliminar base de datos de prueba
DROP DATABASE IF EXISTS test;
DELETE FROM mysql.db WHERE Db='test' OR Db='test\\_%';

-- ────────────────────────────────────────
--  BASE DE DATOS: Hotel Santa Ana (Java)
-- ────────────────────────────────────────
CREATE DATABASE IF NOT EXISTS hotel_santa_ana
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'hotel_user'@'%'
  IDENTIFIED WITH mysql_native_password BY '${HOTEL_DB_PASS}';

GRANT ALL PRIVILEGES ON hotel_santa_ana.* TO 'hotel_user'@'%';

-- Tablas iniciales del hotel
USE hotel_santa_ana;

CREATE TABLE IF NOT EXISTS habitaciones (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  numero      VARCHAR(10) NOT NULL UNIQUE,
  tipo        ENUM('Individual','Doble','Suite') NOT NULL DEFAULT 'Individual',
  precio_noche DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  estado      ENUM('Disponible','Ocupada','Mantenimiento') NOT NULL DEFAULT 'Disponible',
  descripcion TEXT,
  creado_en   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS huespedes (
  id              INT AUTO_INCREMENT PRIMARY KEY,
  identificacion  VARCHAR(20) NOT NULL UNIQUE,
  nombre_completo VARCHAR(120) NOT NULL,
  correo          VARCHAR(100),
  telefono        VARCHAR(20),
  creado_en       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS reservas (
  id              INT AUTO_INCREMENT PRIMARY KEY,
  id_huesped      INT NOT NULL,
  id_habitacion   INT NOT NULL,
  fecha_entrada   DATE NOT NULL,
  hora_entrada    TIME,
  fecha_salida    DATE NOT NULL,
  hora_salida     TIME,
  tipo_estadia    ENUM('Por horas','Media noche','Noche completa','Día completo') NOT NULL,
  estado          ENUM('Pendiente','Confirmada','Check-in','Check-out','Cancelada') NOT NULL DEFAULT 'Pendiente',
  metodo_pago     ENUM('Efectivo','Transferencia','Tarjeta') NOT NULL DEFAULT 'Efectivo',
  anticipo        DECIMAL(10,2) DEFAULT 0.00,
  total           DECIMAL(10,2) DEFAULT 0.00,
  creado_en       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_huesped)    REFERENCES huespedes(id),
  FOREIGN KEY (id_habitacion) REFERENCES habitaciones(id)
);

CREATE TABLE IF NOT EXISTS usuarios (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  nombre       VARCHAR(100) NOT NULL,
  correo       VARCHAR(100) NOT NULL UNIQUE,
  contrasena   VARCHAR(255) NOT NULL,
  rol          ENUM('Administrador','Recepcionista') NOT NULL DEFAULT 'Recepcionista',
  activo       TINYINT(1) DEFAULT 1,
  creado_en    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Habitaciones de ejemplo
INSERT IGNORE INTO habitaciones (numero, tipo, precio_noche, estado) VALUES
  ('101','Individual', 70000, 'Disponible'),
  ('102','Doble',      120000,'Disponible'),
  ('103','Suite',      200000,'Disponible'),
  ('104','Individual', 70000, 'Disponible'),
  ('105','Doble',      120000,'Disponible');

-- ─────────────────────────────────────────
--  BASE DE DATOS: App Node.js / Express
-- ─────────────────────────────────────────
CREATE DATABASE IF NOT EXISTS app_node
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'node_user'@'%'
  IDENTIFIED WITH mysql_native_password BY '${NODE_DB_PASS}';

GRANT ALL PRIVILEGES ON app_node.* TO 'node_user'@'%';

-- Aplicar cambios
FLUSH PRIVILEGES;

EOF

ok "Bases de datos y usuarios creados."

# ── Permitir conexiones remotas ───────────────────────────────────────────────
info "Habilitando conexiones remotas a MySQL..."
MYSQL_CONF=$(find /etc/mysql -name "mysqld.cnf" 2>/dev/null | head -1)
if [ -z "$MYSQL_CONF" ]; then
  MYSQL_CONF="/etc/mysql/mysql.conf.d/mysqld.cnf"
fi

# Cambiar bind-address de 127.0.0.1 a 0.0.0.0
sed -i 's/^bind-address\s*=.*/bind-address = 0.0.0.0/' "$MYSQL_CONF"
ok "MySQL ahora acepta conexiones de la red local."

# ── Reiniciar MySQL para aplicar cambios ──────────────────────────────────────
info "Reiniciando MySQL..."
systemctl restart mysql
ok "MySQL reiniciado."

# ── Configurar el firewall (ufw) si está disponible ──────────────────────────
if command -v ufw &> /dev/null; then
  info "Configurando firewall (UFW)..."
  ufw allow 3306/tcp > /dev/null 2>&1
  ok "Puerto 3306 (MySQL) abierto en el firewall."
else
  warn "UFW no está instalado. Si usas otro firewall, abre el puerto 3306 manualmente."
fi

# ── Obtener la IP del servidor ────────────────────────────────────────────────
IP_SERVIDOR=$(hostname -I | awk '{print $1}')

# ── Resumen final ─────────────────────────────────────────────────────────────
echo ""
echo -e "${VERDE}"
echo "  ╔══════════════════════════════════════════════════════╗"
echo "  ║              CONFIGURACIÓN COMPLETADA                ║"
echo "  ╚══════════════════════════════════════════════════════╝"
echo -e "${NC}"
echo ""
echo -e "  ${BLANCO}IP de este servidor:${NC}  ${AMARILLO}${IP_SERVIDOR}${NC}"
echo ""
echo -e "  ${BLANCO}─── App Hotel Santa Ana (Java) ──────────────────────${NC}"
echo -e "  Host:      ${IP_SERVIDOR}"
echo -e "  Puerto:    3306"
echo -e "  Base de datos: hotel_santa_ana"
echo -e "  Usuario:   hotel_user"
echo -e "  Contraseña: (la que escribiste)"
echo ""
echo -e "  ${BLANCO}─── App Node.js / Express ───────────────────────────${NC}"
echo -e "  Host:      ${IP_SERVIDOR}"
echo -e "  Puerto:    3306"
echo -e "  Base de datos: app_node"
echo -e "  Usuario:   node_user"
echo -e "  Contraseña: (la que escribiste)"
echo ""
echo -e "  ${BLANCO}─── Administrador MySQL ─────────────────────────────${NC}"
echo -e "  Usuario:   root"
echo -e "  Contraseña: (la que escribiste)"
echo ""
echo -e "  ${AMARILLO}Guarda bien estas contraseñas, no se pueden recuperar.${NC}"
echo ""
echo -e "  Para conectarte desde otra máquina puedes usar:"
echo -e "  ${AZUL}mysql -h ${IP_SERVIDOR} -u hotel_user -p hotel_santa_ana${NC}"
echo ""
