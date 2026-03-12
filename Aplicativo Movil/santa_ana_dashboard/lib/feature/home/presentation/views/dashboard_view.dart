import 'package:flutter/material.dart';
import 'package:santa_ana_dashboard/core/theme/app_theme.dart';


//  DASHBOARD VIEW

class DashboardView extends StatelessWidget {
  const DashboardView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.bgColor,
      body: SafeArea(
        child: Column(
          children: [
            // ── Contenido scrolleable ──
            Expanded(
              child: SingleChildScrollView(
                physics: const BouncingScrollPhysics(),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: const [
                    _DashboardHeader(),
                    SizedBox(height: 4),
                    _DateStrip(),
                    SizedBox(height: 8),
                    _OccupancyCard(),
                    SizedBox(height: 12),
                    _StatsRow(),
                    SizedBox(height: 20),
                    _ReservationsList(),
                    SizedBox(height: 16),
                  ],
                ),
              ),
            ),
            // ── Navegación inferior ──
            const _BottomNav(),
          ],
        ),
      ),
    );
  }
}


//  HEADER


class _DashboardHeader extends StatelessWidget {
  const _DashboardHeader();

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(24, 18, 24, 0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Textos
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'BIENVENIDO DE VUELTA',
                  style: Theme.of(context).textTheme.labelLarge?.copyWith(
                    color: AppTheme.textMuted,
                    letterSpacing: 1.2,
                  ),
                ),
                const SizedBox(height: 4),
                RichText(
                  text: const TextSpan(
                    style: TextStyle(
                      fontFamily: 'Georgia',
                      fontSize: 22,
                      fontWeight: FontWeight.w700,
                      color: AppTheme.textColor,
                    ),
                    children: [
                      TextSpan(text: 'Hotel '),
                      TextSpan(
                        text: 'Santa Ana',
                        style: TextStyle(color: AppTheme.goldColor),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
          // Avatar
          Container(
            width: 42,
            height: 42,
            decoration: BoxDecoration(
              color: AppTheme.goldDim,
              shape: BoxShape.circle,
              border: Border.all(color: AppTheme.goldColor, width: 1.5),
            ),
            child: const Icon(
              Icons.person_rounded,
              color: AppTheme.goldColor,
              size: 20,
            ),
          ),
        ],
      ),
    );
  }
}


//  DATE STRIP


class _DateStrip extends StatelessWidget {
  const _DateStrip();

  String _formattedDate() {
    const months = [
      '', 'ENERO', 'FEBRERO', 'MARZO', 'ABRIL', 'MAYO', 'JUNIO',
      'JULIO', 'AGOSTO', 'SEPTIEMBRE', 'OCTUBRE', 'NOVIEMBRE', 'DICIEMBRE',
    ];
    const days = [
      '', 'LUNES', 'MARTES', 'MIÉRCOLES', 'JUEVES', 'VIERNES', 'SÁBADO', 'DOMINGO',
    ];
    final now = DateTime.now();
    return '${days[now.weekday]}, ${now.day} DE ${months[now.month]} ${now.year}';
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 10),
      child: Row(
        children: [
          Container(
            width: 6,
            height: 6,
            decoration: const BoxDecoration(
              color: AppTheme.goldColor,
              shape: BoxShape.circle,
            ),
          ),
          const SizedBox(width: 8),
          Text(
            _formattedDate(),
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
              letterSpacing: 0.8,
            ),
          ),
        ],
      ),
    );
  }
}

//  OCCUPANCY CARD

class _OccupancyCard extends StatelessWidget {
  const _OccupancyCard();

  // Datos de ejemplo — después vendrán del BLoC
  static const int totalRooms      = 120;
  static const int occupiedRooms   = 104;
  static const int maintenanceRooms = 3;

  @override
  Widget build(BuildContext context) {
    final freeRooms = totalRooms - occupiedRooms - maintenanceRooms;
    final pct = (occupiedRooms / totalRooms * 100).round();

    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Container(
        padding: const EdgeInsets.all(22),
        decoration: BoxDecoration(
          gradient: const LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [Color(0xFF1A1F2E), Color(0xFF131720), Color(0xFF0F1219)],
            stops: [0.0, 0.6, 1.0],
          ),
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: AppTheme.borderColor),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Label
            Text(
              'OCUPACIÓN TOTAL',
              style: Theme.of(context).textTheme.labelLarge?.copyWith(
                color: AppTheme.textMuted,
                letterSpacing: 1.0,
              ),
            ),
            const SizedBox(height: 6),

            // Número grande
            RichText(
              text: TextSpan(
                style: const TextStyle(
                  fontFamily: 'Georgia',
                  fontSize: 52,
                  fontWeight: FontWeight.w700,
                  color: AppTheme.textColor,
                  height: 1,
                ),
                children: [
                  TextSpan(text: '$pct'),
                  const TextSpan(
                    text: '%',
                    style: TextStyle(fontSize: 28, color: AppTheme.goldColor),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 4),

            // Sub-línea
            RichText(
              text: TextSpan(
                style: const TextStyle(
                  fontSize: 12,
                  color: AppTheme.textMuted,
                ),
                children: [
                  TextSpan(text: '$occupiedRooms de $totalRooms habitaciones · '),
                  const TextSpan(
                    text: '↑ +5% vs ayer',
                    style: TextStyle(
                      color: AppTheme.successColor,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 18),

            // Barra de habitaciones
            _RoomBar(
              occupied:    occupiedRooms,
              free:        freeRooms,
              maintenance: maintenanceRooms,
              total:       totalRooms,
            ),
            const SizedBox(height: 10),

            // Leyenda
            Row(
              children: [
                _LegendItem(
                  color: AppTheme.goldColor,
                  label: 'Ocupadas ($occupiedRooms)',
                ),
                const SizedBox(width: 14),
                _LegendItem(
                  color: AppTheme.borderColor,
                  label: 'Libres ($freeRooms)',
                ),
                const SizedBox(width: 14),
                _LegendItem(
                  color: AppTheme.errorColor,
                  label: 'Mant. ($maintenanceRooms)',
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

// ── Room Bar

class _RoomBar extends StatelessWidget {
  final int occupied, free, maintenance, total;
  const _RoomBar({
    required this.occupied,
    required this.free,
    required this.maintenance,
    required this.total,
  });

  @override
  Widget build(BuildContext context) {
    const segments = 24;
    final occupiedSeg    = (occupied    / total * segments).round();
    final maintenanceSeg = (maintenance / total * segments).round();
    final freeSeg        = segments - occupiedSeg - maintenanceSeg;

    return Row(
      children: [
        for (int i = 0; i < segments; i++)
          Expanded(
            child: Container(
              height: 6,
              margin: const EdgeInsets.symmetric(horizontal: 1.5),
              decoration: BoxDecoration(
                color: i < occupiedSeg
                    ? AppTheme.goldColor
                    : i < occupiedSeg + freeSeg
                        ? AppTheme.borderColor
                        : AppTheme.errorColor.withOpacity(0.7),
                borderRadius: BorderRadius.circular(3),
              ),
            ),
          ),
      ],
    );
  }
}

// ── Legend Item ───────────────────────────────────────────────

class _LegendItem extends StatelessWidget {
  final Color color;
  final String label;
  const _LegendItem({required this.color, required this.label});

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Container(
          width: 7,
          height: 7,
          decoration: BoxDecoration(color: color, shape: BoxShape.circle),
        ),
        const SizedBox(width: 5),
        Text(label, style: Theme.of(context).textTheme.bodySmall),
      ],
    );
  }
}


//  STATS ROW  (Ingresos + Check-ins)


class _StatsRow extends StatelessWidget {
  const _StatsRow();

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Row(
        children: const [
          Expanded(child: _IncomeCard()),
          SizedBox(width: 10),
          Expanded(child: _CheckinCard()),
        ],
      ),
    );
  }
}

// ── Income Card ───────────────────────────────────────────────

class _IncomeCard extends StatelessWidget {
  const _IncomeCard();

  static const List<double> weekData = [0.4, 0.55, 0.65, 0.5, 0.75, 0.9, 1.0];

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardColor,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: AppTheme.borderColor),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Label
          Row(
            children: [
              const Icon(Icons.attach_money_rounded,
                  color: AppTheme.goldColor, size: 14),
              const SizedBox(width: 4),
              Text(
                'INGRESOS HOY',
                style: Theme.of(context).textTheme.labelLarge?.copyWith(
                  color: AppTheme.textMuted,
                  fontSize: 10,
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),

          // Valor
          Text(
            '\$14.2k',
            style: Theme.of(context).textTheme.headlineMedium?.copyWith(
              fontSize: 24,
              height: 1,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            '↑ 12% vs ayer',
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
              color: AppTheme.successColor,
            ),
          ),

          // Mini bar chart
          const SizedBox(height: 10),
          SizedBox(
            height: 30,
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.end,
              children: weekData.asMap().entries.map((e) {
                final isToday = e.key == weekData.length - 1;
                return Expanded(
                  child: Container(
                    margin: const EdgeInsets.symmetric(horizontal: 1.5),
                    decoration: BoxDecoration(
                      color: isToday
                          ? AppTheme.goldColor
                          : e.key >= 2
                              ? AppTheme.goldColor.withOpacity(0.35)
                              : AppTheme.borderColor,
                      borderRadius:
                          const BorderRadius.vertical(top: Radius.circular(3)),
                    ),
                    height: 30 * e.value,
                  ),
                );
              }).toList(),
            ),
          ),

          // Barra inferior dorada
          const SizedBox(height: 10),
          Container(
            height: 2,
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                colors: [AppTheme.goldColor, Colors.transparent],
              ),
              borderRadius: BorderRadius.circular(1),
            ),
          ),
        ],
      ),
    );
  }
}

// ── Check-in Card ─────────────────────────────────────────────

class _CheckinCard extends StatelessWidget {
  const _CheckinCard();

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardColor,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: AppTheme.borderColor),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Label
          Row(
            children: [
              const Icon(Icons.key_rounded,
                  color: AppTheme.successColor, size: 14),
              const SizedBox(width: 4),
              Text(
                'CHECK-INS HOY',
                style: Theme.of(context).textTheme.labelLarge?.copyWith(
                  color: AppTheme.textMuted,
                  fontSize: 10,
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),

          // Valor
          Text(
            '18',
            style: Theme.of(context).textTheme.headlineMedium?.copyWith(
              fontSize: 24,
              height: 1,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            '↑ 3 pendientes',
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
              color: AppTheme.successColor,
            ),
          ),
          const SizedBox(height: 10),

          // Progreso check-ins
          _ProgressRow(label: 'Check-ins', done: 15, total: 18,
              color: AppTheme.successColor),
          const SizedBox(height: 8),

          // Progreso check-outs
          _ProgressRow(label: 'Check-outs', done: 9, total: 12,
              color: AppTheme.errorColor),

          // Barra inferior verde
          const SizedBox(height: 10),
          Container(
            height: 2,
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                colors: [AppTheme.successColor, Colors.transparent],
              ),
              borderRadius: BorderRadius.circular(1),
            ),
          ),
        ],
      ),
    );
  }
}

class _ProgressRow extends StatelessWidget {
  final String label;
  final int done, total;
  final Color color;
  const _ProgressRow({
    required this.label,
    required this.done,
    required this.total,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    final pct = done / total;
    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(label,
                style: Theme.of(context)
                    .textTheme
                    .bodySmall
                    ?.copyWith(fontSize: 10)),
            Text('$done/$total',
                style: Theme.of(context)
                    .textTheme
                    .bodySmall
                    ?.copyWith(color: color, fontSize: 10)),
          ],
        ),
        const SizedBox(height: 4),
        ClipRRect(
          borderRadius: BorderRadius.circular(2),
          child: LinearProgressIndicator(
            value: pct,
            minHeight: 4,
            backgroundColor: AppTheme.borderColor,
            valueColor: AlwaysStoppedAnimation<Color>(color),
          ),
        ),
      ],
    );
  }
}


//  RESERVATIONS LIST


enum ReservationStatus { checkIn, checkOut, reserved }

class _ReservationData {
  final String initials, name, detail, room;
  final ReservationStatus status;
  final Color avatarColor, avatarBorder;

  const _ReservationData({
    required this.initials,
    required this.name,
    required this.detail,
    required this.room,
    required this.status,
    required this.avatarColor,
    required this.avatarBorder,
  });
}

const _mockReservations = [
  _ReservationData(
    initials: 'AM', name: 'Andrés Martínez',
    detail: '2 noches · Suite Deluxe · 10:00 AM',
    room: '301', status: ReservationStatus.checkIn,
    avatarColor: Color(0x265B8DEE), avatarBorder: Color(0x4D5B8DEE),
  ),
  _ReservationData(
    initials: 'SR', name: 'Sofia Restrepo',
    detail: '4 noches · Habitación Estándar',
    room: '215', status: ReservationStatus.checkOut,
    avatarColor: Color(0x26C9A84C), avatarBorder: Color(0x4DC9A84C),
  ),
  _ReservationData(
    initials: 'JL', name: 'Juan López',
    detail: '1 noche · Junior Suite · Mañana',
    room: '418', status: ReservationStatus.reserved,
    avatarColor: Color(0x264CAF82), avatarBorder: Color(0x4D4CAF82),
  ),
];

class _ReservationsList extends StatelessWidget {
  const _ReservationsList();

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // Header de sección
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text('Reservas del día',
                  style: Theme.of(context).textTheme.titleMedium),
              TextButton(
                onPressed: () {},
                child: const Text('Ver todas →'),
              ),
            ],
          ),
        ),
        const SizedBox(height: 4),

        // Cards
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: Column(
            children: _mockReservations
                .map((r) => Padding(
                      padding: const EdgeInsets.only(bottom: 8),
                      child: _ReservationCard(data: r),
                    ))
                .toList(),
          ),
        ),
      ],
    );
  }
}

class _ReservationCard extends StatelessWidget {
  final _ReservationData data;
  const _ReservationCard({super.key, required this.data});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
      decoration: BoxDecoration(
        color: AppTheme.cardColor,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: AppTheme.borderColor),
      ),
      child: Row(
        children: [
          // Avatar
          Container(
            width: 40,
            height: 40,
            decoration: BoxDecoration(
              color: data.avatarColor,
              shape: BoxShape.circle,
              border: Border.all(color: data.avatarBorder, width: 1),
            ),
            child: Center(
              child: Text(
                data.initials,
                style: const TextStyle(
                  fontFamily: 'Georgia',
                  fontSize: 13,
                  fontWeight: FontWeight.w700,
                  color: AppTheme.textColor,
                ),
              ),
            ),
          ),
          const SizedBox(width: 12),

          // Info
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(data.name,
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      fontSize: 13,
                    )),
                const SizedBox(height: 2),
                Text(data.detail,
                    style: Theme.of(context)
                        .textTheme
                        .bodySmall
                        ?.copyWith(fontSize: 11)),
              ],
            ),
          ),

          // Room + badge
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(
                data.room,
                style: const TextStyle(
                  fontFamily: 'Georgia',
                  fontSize: 17,
                  fontWeight: FontWeight.w700,
                  color: AppTheme.goldColor,
                ),
              ),
              const SizedBox(height: 3),
              _StatusBadge(status: data.status),
            ],
          ),
        ],
      ),
    );
  }
}

class _StatusBadge extends StatelessWidget {
  final ReservationStatus status;
  const _StatusBadge({required this.status});

  @override
  Widget build(BuildContext context) {
    final (label, color, bg) = switch (status) {
      ReservationStatus.checkIn  => ('CHECK-IN',  AppTheme.successColor, const Color(0x264CAF82)),
      ReservationStatus.checkOut => ('CHECK-OUT', AppTheme.errorColor,   const Color(0x26E05C5C)),
      ReservationStatus.reserved => ('RESERVA',   AppTheme.infoColor,    const Color(0x265B8DEE)),
    };

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 7, vertical: 2),
      decoration: BoxDecoration(
        color: bg,
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: color.withOpacity(0.4)),
      ),
      child: Text(
        label,
        style: TextStyle(
          fontSize: 8,
          fontWeight: FontWeight.w700,
          color: color,
          letterSpacing: 0.5,
        ),
      ),
    );
  }
}


//  BOTTOM NAV


class _BottomNav extends StatefulWidget {
  const _BottomNav();

  @override
  State<_BottomNav> createState() => _BottomNavState();
}

class _BottomNavState extends State<_BottomNav> {
  int _selected = 0;

  static const _items = [
    (icon: Icons.home_rounded,             label: 'INICIO'),
    (icon: Icons.calendar_month_rounded,   label: 'RESERVAS'),
    (icon: Icons.bed_rounded,              label: 'HABITACIONES'),
    (icon: Icons.bar_chart_rounded,        label: 'REPORTES'),
  ];

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.fromLTRB(16, 0, 16, 12),
      padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 8),
      decoration: BoxDecoration(
        color: AppTheme.cardColor,
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: AppTheme.borderColor),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: _items.asMap().entries.map((e) {
          final isActive = e.key == _selected;
          return GestureDetector(
            onTap: () => setState(() => _selected = e.key),
            child: AnimatedContainer(
              duration: const Duration(milliseconds: 200),
              padding:
                  const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
              decoration: BoxDecoration(
                color: isActive ? AppTheme.goldDim : Colors.transparent,
                borderRadius: BorderRadius.circular(12),
              ),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(
                    e.value.icon,
                    size: 20,
                    color: isActive ? AppTheme.goldColor : AppTheme.textMuted,
                  ),
                  const SizedBox(height: 4),
                  Text(
                    e.value.label,
                    style: TextStyle(
                      fontSize: 9,
                      fontWeight: FontWeight.w600,
                      letterSpacing: 0.5,
                      color: isActive ? AppTheme.goldColor : AppTheme.textMuted,
                    ),
                  ),
                ],
              ),
            ),
          );
        }).toList(),
      ),
    );
  }
}