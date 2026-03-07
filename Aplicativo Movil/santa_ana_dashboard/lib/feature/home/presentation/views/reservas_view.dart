import 'package:flutter/material.dart';
import 'package:santa_ana_dashboard/core/theme/app_theme.dart';

// ════════════════════════════════════════════════════════════════
//  MODELOS
// ════════════════════════════════════════════════════════════════

enum ReservationStatus { checkIn, checkOut, inStay, reserved }

class ReservationModel {
  final String id, initials, guestName, subtitle, room, time, extra;
  final int totalNights, currentNight;
  final DateTime checkInDate, checkOutDate;
  final ReservationStatus status;
  final Color avatarColor, avatarBorder;

  const ReservationModel({
    required this.id,
    required this.initials,
    required this.guestName,
    required this.subtitle,
    required this.room,
    required this.time,
    required this.extra,
    required this.totalNights,
    required this.currentNight,
    required this.checkInDate,
    required this.checkOutDate,
    required this.status,
    required this.avatarColor,
    required this.avatarBorder,
  });
}

// Datos de ejemplo — reemplazar con datos del BLoC
final _mockReservations = [
  ReservationModel(
    id: '#RES-2841', initials: 'AM', guestName: 'Andrés Martínez',
    subtitle: 'Suite Deluxe', room: '301', time: '10:00 AM',
    extra: '2 huésp.', totalNights: 3, currentNight: 0,
    checkInDate: DateTime(2026, 3, 4), checkOutDate: DateTime(2026, 3, 7),
    status: ReservationStatus.checkIn,
    avatarColor: Color(0x265B8DEE), avatarBorder: Color(0x4D5B8DEE),
  ),
  ReservationModel(
    id: '#RES-2798', initials: 'SR', guestName: 'Sofia Restrepo',
    subtitle: 'Habitación Estándar', room: '215', time: '12:00 PM',
    extra: '\$420k', totalNights: 4, currentNight: 4,
    checkInDate: DateTime(2026, 2, 29), checkOutDate: DateTime(2026, 3, 4),
    status: ReservationStatus.checkOut,
    avatarColor: Color(0x26C9A84C), avatarBorder: Color(0x4DC9A84C),
  ),
  ReservationModel(
    id: '#RES-2801', initials: 'CG', guestName: 'Carlos Gómez',
    subtitle: 'Junior Suite', room: '512', time: 'Día 2/5',
    extra: '3 huésp.', totalNights: 5, currentNight: 2,
    checkInDate: DateTime(2026, 3, 2), checkOutDate: DateTime(2026, 3, 7),
    status: ReservationStatus.inStay,
    avatarColor: Color(0x264CAF82), avatarBorder: Color(0x4D4CAF82),
  ),
  ReservationModel(
    id: '#RES-2855', initials: 'JL', guestName: 'Juan López',
    subtitle: 'Habitación Estándar', room: '418', time: 'Mañana',
    extra: '\$280k', totalNights: 1, currentNight: 0,
    checkInDate: DateTime(2026, 3, 5), checkOutDate: DateTime(2026, 3, 6),
    status: ReservationStatus.reserved,
    avatarColor: Color(0x265B8DEE), avatarBorder: Color(0x4D5B8DEE),
  ),
];

// ════════════════════════════════════════════════════════════════
//  RESERVATIONS VIEW
// ════════════════════════════════════════════════════════════════

class ReservationsView extends StatefulWidget {
  const ReservationsView({super.key});

  @override
  State<ReservationsView> createState() => _ReservationsViewState();
}

class _ReservationsViewState extends State<ReservationsView> {
  int _selectedFilter = 0;
  final _filters = ['Todas', 'Check-in', 'Check-out', 'En estadía', 'Pendientes'];
  final _searchController = TextEditingController();

  List<ReservationModel> get _filtered {
    if (_selectedFilter == 0) return _mockReservations;
    final map = {
      1: ReservationStatus.checkIn,
      2: ReservationStatus.checkOut,
      3: ReservationStatus.inStay,
      4: ReservationStatus.reserved,
    };
    return _mockReservations
        .where((r) => r.status == map[_selectedFilter])
        .toList();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.bgColor,
      body: SafeArea(
        child: Column(
          children: [
            Expanded(
              child: CustomScrollView(
                physics: const BouncingScrollPhysics(),
                slivers: [
                  // Header
                  SliverToBoxAdapter(child: _buildHeader()),
                  // Filtros
                  SliverToBoxAdapter(child: _buildFilterTabs()),
                  // Búsqueda
                  SliverToBoxAdapter(child: _buildSearchBar()),
                  // Stats
                  SliverToBoxAdapter(child: _buildStatsStrip()),
                  // Título sección
                  SliverToBoxAdapter(child: _buildSectionTitle()),
                  // Lista
                  SliverPadding(
                    padding: const EdgeInsets.symmetric(horizontal: 16),
                    sliver: SliverList(
                      delegate: SliverChildBuilderDelegate(
                        (context, i) => Padding(
                          padding: const EdgeInsets.only(bottom: 10),
                          child: _ReservationCard(data: _filtered[i]),
                        ),
                        childCount: _filtered.length,
                      ),
                    ),
                  ),
                  const SliverToBoxAdapter(child: SizedBox(height: 16)),
                ],
              ),
            ),
            _buildBottomNav(),
          ],
        ),
      ),
    );
  }

  // ── Header ──────────────────────────────────────────────────

  Widget _buildHeader() {
    return Padding(
      padding: const EdgeInsets.fromLTRB(24, 18, 24, 0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          RichText(
            text: const TextSpan(
              style: TextStyle(
                fontFamily: 'Georgia',
                fontSize: 22,
                fontWeight: FontWeight.w700,
                color: AppTheme.textColor,
              ),
              children: [
                TextSpan(text: 'Reservas '),
                TextSpan(
                  text: 'del día',
                  style: TextStyle(color: AppTheme.goldColor),
                ),
              ],
            ),
          ),
          // Botón añadir
          GestureDetector(
            onTap: () {},
            child: Container(
              width: 38, height: 38,
              decoration: BoxDecoration(
                color: AppTheme.cardColor,
                borderRadius: BorderRadius.circular(10),
                border: Border.all(color: AppTheme.borderColor),
              ),
              child: const Icon(Icons.add_rounded,
                  color: AppTheme.goldColor, size: 20),
            ),
          ),
        ],
      ),
    );
  }

  // ── Filter tabs ──────────────────────────────────────────────

  Widget _buildFilterTabs() {
    return SizedBox(
      height: 52,
      child: ListView.separated(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.fromLTRB(24, 14, 24, 0),
        itemCount: _filters.length,
        separatorBuilder: (_, __) => const SizedBox(width: 8),
        itemBuilder: (context, i) {
          final isActive = i == _selectedFilter;
          return GestureDetector(
            onTap: () => setState(() => _selectedFilter = i),
            child: AnimatedContainer(
              duration: const Duration(milliseconds: 200),
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 7),
              decoration: BoxDecoration(
                color: isActive ? AppTheme.goldDim : Colors.transparent,
                borderRadius: BorderRadius.circular(20),
                border: Border.all(
                  color: isActive ? AppTheme.goldColor : AppTheme.borderColor,
                ),
              ),
              child: Text(
                _filters[i],
                style: TextStyle(
                  fontSize: 12,
                  fontWeight: FontWeight.w600,
                  letterSpacing: 0.3,
                  color: isActive ? AppTheme.goldColor : AppTheme.textMuted,
                ),
              ),
            ),
          );
        },
      ),
    );
  }

  // ── Search bar ───────────────────────────────────────────────

  Widget _buildSearchBar() {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 14, 16, 0),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 11),
        decoration: BoxDecoration(
          color: AppTheme.cardColor,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: AppTheme.borderColor),
        ),
        child: Row(
          children: [
            const Icon(Icons.search_rounded,
                color: AppTheme.textMuted, size: 18),
            const SizedBox(width: 10),
            Expanded(
              child: TextField(
                controller: _searchController,
                style: const TextStyle(
                    color: AppTheme.textColor, fontSize: 13),
                decoration: const InputDecoration(
                  hintText: 'Buscar huésped o habitación...',
                  hintStyle:
                      TextStyle(color: AppTheme.textMuted, fontSize: 13),
                  border: InputBorder.none,
                  isDense: true,
                  contentPadding: EdgeInsets.zero,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  // ── Stats strip ──────────────────────────────────────────────

  Widget _buildStatsStrip() {
    final stats = [
      ('24', 'Total',      AppTheme.goldColor),
      ('8',  'Check-in',   AppTheme.successColor),
      ('5',  'Check-out',  AppTheme.errorColor),
      ('11', 'En estadía', AppTheme.infoColor),
    ];
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 14, 16, 0),
      child: Row(
        children: stats.asMap().entries.map((e) {
          final (val, label, color) = e.value;
          return Expanded(
            child: Container(
              margin: EdgeInsets.only(left: e.key == 0 ? 0 : 8),
              padding: const EdgeInsets.symmetric(vertical: 10),
              decoration: BoxDecoration(
                color: AppTheme.cardColor,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: AppTheme.borderColor),
              ),
              child: Column(
                children: [
                  Text(
                    val,
                    style: TextStyle(
                      fontFamily: 'Georgia',
                      fontSize: 20,
                      fontWeight: FontWeight.w700,
                      color: color,
                      height: 1,
                    ),
                  ),
                  const SizedBox(height: 3),
                  Text(
                    label.toUpperCase(),
                    style: const TextStyle(
                      fontSize: 9,
                      color: AppTheme.textMuted,
                      letterSpacing: 0.5,
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

  // ── Section title ────────────────────────────────────────────

  Widget _buildSectionTitle() {
    return Padding(
      padding: const EdgeInsets.fromLTRB(24, 18, 24, 10),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text('Hoy · Miércoles 4 Mar',
              style: Theme.of(context).textTheme.titleMedium),
          Text('${_filtered.length} reservas',
              style: Theme.of(context).textTheme.bodySmall),
        ],
      ),
    );
  }

  // ── Bottom nav ───────────────────────────────────────────────

  Widget _buildBottomNav() {
    const items = [
      (Icons.home_rounded,           'INICIO'),
      (Icons.calendar_month_rounded, 'RESERVAS'),
      (Icons.bed_rounded,            'HABITACIONES'),
      (Icons.bar_chart_rounded,      'REPORTES'),
    ];
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
        children: items.asMap().entries.map((e) {
          final isActive = e.key == 1; // Reservas activo
          return GestureDetector(
            onTap: () {},
            child: AnimatedContainer(
              duration: const Duration(milliseconds: 200),
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
              decoration: BoxDecoration(
                color: isActive ? AppTheme.goldDim : Colors.transparent,
                borderRadius: BorderRadius.circular(12),
              ),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(e.value.$1, size: 20,
                      color: isActive
                          ? AppTheme.goldColor
                          : AppTheme.textMuted),
                  const SizedBox(height: 4),
                  Text(
                    e.value.$2,
                    style: TextStyle(
                      fontSize: 9,
                      fontWeight: FontWeight.w600,
                      letterSpacing: 0.5,
                      color: isActive
                          ? AppTheme.goldColor
                          : AppTheme.textMuted,
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

// ════════════════════════════════════════════════════════════════
//  RESERVATION CARD
// ════════════════════════════════════════════════════════════════

class _ReservationCard extends StatelessWidget {
  final ReservationModel data;
  const _ReservationCard({required this.data});

  // Colores según estado
  Color get _accentColor => switch (data.status) {
    ReservationStatus.checkIn  => AppTheme.successColor,
    ReservationStatus.checkOut => AppTheme.errorColor,
    ReservationStatus.inStay   => AppTheme.goldColor,
    ReservationStatus.reserved => AppTheme.infoColor,
  };

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: AppTheme.cardColor,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: AppTheme.borderColor),
      ),
      clipBehavior: Clip.hardEdge,
      child: IntrinsicHeight(
        child: Row(
          children: [
            // Barra lateral de color
            Container(width: 3, color: _accentColor),
            // Contenido
            Expanded(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    _buildTop(context),
                    const SizedBox(height: 12),
                    _buildChips(context),
                    const SizedBox(height: 12),
                    _buildTimeline(),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  // ── Top row (avatar + nombre + badge) ───────────────────────

  Widget _buildTop(BuildContext context) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Avatar
        Container(
          width: 40, height: 40,
          decoration: BoxDecoration(
            color: data.avatarColor,
            shape: BoxShape.circle,
            border: Border.all(color: data.avatarBorder),
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
        const SizedBox(width: 10),
        // Info
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(data.guestName,
                  style: Theme.of(context)
                      .textTheme
                      .titleMedium
                      ?.copyWith(fontSize: 13)),
              const SizedBox(height: 2),
              Text('${data.id} · ${data.subtitle}',
                  style: Theme.of(context)
                      .textTheme
                      .bodySmall
                      ?.copyWith(fontSize: 11)),
            ],
          ),
        ),
        const SizedBox(width: 8),
        // Badge
        _StatusBadge(status: data.status),
      ],
    );
  }

  // ── Detail chips ─────────────────────────────────────────────

  Widget _buildChips(BuildContext context) {
    final chips = [
      (Icons.bed_rounded,    'Hab. ${data.room}'),
      (Icons.access_time_rounded, data.time),
      (Icons.person_rounded, data.extra),
    ];
    return Row(
      children: chips.map((c) {
        return Padding(
          padding: const EdgeInsets.only(right: 8),
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
            decoration: BoxDecoration(
              color: Colors.white.withOpacity(0.04),
              borderRadius: BorderRadius.circular(8),
              border: Border.all(color: AppTheme.borderColor),
            ),
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Icon(c.$1, size: 12, color: AppTheme.textMuted),
                const SizedBox(width: 5),
                Text(
                  c.$2,
                  style: const TextStyle(
                    fontSize: 11,
                    color: AppTheme.textColor,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ],
            ),
          ),
        );
      }).toList(),
    );
  }

  // ── Timeline ─────────────────────────────────────────────────

  Widget _buildTimeline() {
    final isCheckOut = data.status == ReservationStatus.checkOut;
    final isInStay   = data.status == ReservationStatus.inStay;
    final isReserved = data.status == ReservationStatus.reserved;

    final startColor = isCheckOut
        ? AppTheme.textMuted
        : isReserved
            ? AppTheme.infoColor
            : AppTheme.goldColor;

    final endColor = isCheckOut
        ? AppTheme.errorColor
        : isInStay
            ? AppTheme.textMuted
            : isReserved
                ? AppTheme.borderColor
                : AppTheme.textMuted;

    final lineStart = isCheckOut
        ? AppTheme.borderColor
        : isReserved
            ? AppTheme.infoColor
            : AppTheme.goldColor;

    return Row(
      children: [
        // Punto inicio
        Container(
          width: 6, height: 6,
          decoration: BoxDecoration(
            color: startColor,
            shape: BoxShape.circle,
          ),
        ),
        const SizedBox(width: 4),
        // Fecha inicio
        _TlDate(
          day: data.checkInDate.day.toString(),
          month: _monthShort(data.checkInDate.month),
          color: isCheckOut ? AppTheme.textMuted : AppTheme.textColor,
        ),
        // Línea con label
        Expanded(
          child: Stack(
            alignment: Alignment.center,
            children: [
              Container(
                height: 1,
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    colors: [lineStart, AppTheme.borderColor],
                  ),
                ),
              ),
              Container(
                color: AppTheme.cardColor,
                padding: const EdgeInsets.symmetric(horizontal: 4),
                child: Text(
                  '${data.totalNights} noches',
                  style: const TextStyle(
                    fontSize: 9,
                    color: AppTheme.goldColor,
                    letterSpacing: 0.3,
                  ),
                ),
              ),
            ],
          ),
        ),
        // Fecha fin
        _TlDate(
          day: data.checkOutDate.day.toString(),
          month: _monthShort(data.checkOutDate.month),
          color: isCheckOut
              ? AppTheme.errorColor
              : isInStay
                  ? AppTheme.textColor
                  : AppTheme.textMuted,
        ),
        const SizedBox(width: 4),
        // Punto fin
        Container(
          width: 6, height: 6,
          decoration: BoxDecoration(
            color: endColor,
            shape: BoxShape.circle,
          ),
        ),
      ],
    );
  }

  String _monthShort(int month) {
    const months = [
      '', 'Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun',
      'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic',
    ];
    return months[month];
  }
}

// ── Timeline date widget ──────────────────────────────────────

class _TlDate extends StatelessWidget {
  final String day, month;
  final Color color;
  const _TlDate({
    required this.day,
    required this.month,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text(
          day,
          style: TextStyle(
            fontFamily: 'Georgia',
            fontSize: 18,
            fontWeight: FontWeight.w700,
            color: color,
            height: 1,
          ),
        ),
        Text(
          month.toUpperCase(),
          style: const TextStyle(
            fontSize: 8,
            color: AppTheme.textMuted,
            letterSpacing: 0.5,
          ),
        ),
      ],
    );
  }
}

// ── Status badge ──────────────────────────────────────────────

class _StatusBadge extends StatelessWidget {
  final ReservationStatus status;
  const _StatusBadge({required this.status});

  @override
  Widget build(BuildContext context) {
    final (label, color, bg) = switch (status) {
      ReservationStatus.checkIn  => ('CHECK-IN',   AppTheme.successColor, const Color(0x264CAF82)),
      ReservationStatus.checkOut => ('CHECK-OUT',  AppTheme.errorColor,   const Color(0x26E05C5C)),
      ReservationStatus.inStay   => ('EN ESTADÍA', AppTheme.goldColor,    const Color(0x26C9A84C)),
      ReservationStatus.reserved => ('RESERVA',    AppTheme.infoColor,    const Color(0x265B8DEE)),
    };

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
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