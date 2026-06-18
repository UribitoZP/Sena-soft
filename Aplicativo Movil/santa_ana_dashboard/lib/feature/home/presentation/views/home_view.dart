import 'package:flutter/material.dart';
import 'package:santa_ana_dashboard/core/services/api_service.dart';
import 'package:santa_ana_dashboard/core/theme/app_theme.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/view_seconds/reservas_del_dia.dart';

// ════════════════════════════════════════════════════════════════
//  HOME VIEW
// ════════════════════════════════════════════════════════════════

class HomeView extends StatefulWidget {
  const HomeView({super.key});

  @override
  State<HomeView> createState() => _HomeViewState();
}

class _HomeViewState extends State<HomeView> {
  final _api = ApiService();
  late Future<Map<String, dynamic>> _statsFuture;
  late Future<List<dynamic>> _reservasFuture;
  @override
  void initState() {
    super.initState();
    _load();
  }

  void _load() {
    _statsFuture    = _api.getStats();
    _reservasFuture = _api.getReservas();
  }

  void _refresh() => setState(() => _load());

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.bgColor,
      body: SafeArea(
        child: FutureBuilder<Map<String, dynamic>>(
          future: _statsFuture,
          builder: (context, statsSnap) {
            final stats = statsSnap.data;
            final hab   = stats?['habitaciones'] as Map<String, dynamic>?;
            final res   = stats?['reservas']     as Map<String, dynamic>?;

            return RefreshIndicator(
              color: AppTheme.goldColor,
              onRefresh: () async => _refresh(),
              child: SingleChildScrollView(
                physics: const AlwaysScrollableScrollPhysics(
                    parent: BouncingScrollPhysics()),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // ── Header ──
                    const _DashboardHeader(),
                    const SizedBox(height: 4),
                    const _DateStrip(),
                    const SizedBox(height: 8),

                    // ── Loading / Error / Content ──
                    if (statsSnap.connectionState == ConnectionState.waiting)
                      const Padding(
                        padding: EdgeInsets.all(48),
                        child: Center(
                          child: CircularProgressIndicator(
                              color: AppTheme.goldColor),
                        ),
                      )
                    else if (statsSnap.hasError)
                      Padding(
                        padding: const EdgeInsets.all(24),
                        child: Center(
                          child: Text(
                            'Sin conexión al servidor.\nVerifica la IP.',
                            style: TextStyle(color: AppTheme.errorColor),
                            textAlign: TextAlign.center,
                          ),
                        ),
                      )
                    else ...[
                      // ── Tarjeta ocupación ──
                      _OccupancyCard(
                        total:        (hab?['total']        ?? 0) as int,
                        ocupadas:     (hab?['ocupadas']     ?? 0) as int,
                        disponibles:  (hab?['disponibles']  ?? 0) as int,
                        limpieza:     (hab?['limpieza']     ?? 0) as int,
                        mantenimiento:(hab?['mantenimiento']?? 0) as int,
                      ),
                      const SizedBox(height: 12),

                      // ── Stats row (Ingresos + Check-ins) ──
                      _StatsRow(
                        activas:        (res?['activas']     ?? 0) as int,
                        completadas:    (res?['completadas'] ?? 0) as int,
                        canceladas:     (res?['canceladas']  ?? 0) as int,
                        ingresosHoy:    ((stats?['ingresos'] as Map<String, dynamic>?)?['hoyFormato']  ?? '\$0') as String,
                        ingresosMes:    ((stats?['ingresos'] as Map<String, dynamic>?)?['mesFormato']  ?? '\$0') as String,
                      ),
                      const SizedBox(height: 20),

                      // ── Reservas del día ──
                      FutureBuilder<List<dynamic>>(
                        future: _reservasFuture,
                        builder: (context, resSnap) {
                          if (resSnap.connectionState ==
                              ConnectionState.waiting) {
                            return const Padding(
                              padding: EdgeInsets.symmetric(vertical: 16),
                              child: Center(
                                child: CircularProgressIndicator(
                                    color: AppTheme.goldColor),
                              ),
                            );
                          }
                          final reservas = resSnap.data ?? [];
                          // Solo muestra las activas (máx 3)
                          final hoy = DateTime.now();

                          final reservasHoy = reservas.where((r) {
                            final entrada = DateTime.tryParse(
                              (r['entrada'] ?? '').toString(),
                            );

                            if (entrada == null) return false;

                            return entrada.year == hoy.year &&
                                entrada.month == hoy.month &&
                                entrada.day == hoy.day;
                          }).take(3).toList();

                          return _ReservationsList(reservas: reservasHoy);
                        },
                      ),
                      const SizedBox(height: 16),
                    ],
                  ],
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}

// ════════════════════════════════════════════════════════════════
//  HEADER
// ════════════════════════════════════════════════════════════════

class _DashboardHeader extends StatelessWidget {
  const _DashboardHeader();

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(24, 18, 24, 0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
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
          Container(
            width: 42,
            height: 42,
            decoration: BoxDecoration(
              color: AppTheme.goldDim,
              shape: BoxShape.circle,
              border: Border.all(color: AppTheme.goldColor, width: 1.5),
            ),
            child: const Icon(Icons.person_rounded,
                color: AppTheme.goldColor, size: 20),
          ),
        ],
      ),
    );
  }
}

// ════════════════════════════════════════════════════════════════
//  DATE STRIP
// ════════════════════════════════════════════════════════════════

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
            width: 6, height: 6,
            decoration: const BoxDecoration(
                color: AppTheme.goldColor, shape: BoxShape.circle),
          ),
          const SizedBox(width: 8),
          Text(_formattedDate(),
              style: Theme.of(context)
                  .textTheme
                  .bodySmall
                  ?.copyWith(letterSpacing: 0.8)),
        ],
      ),
    );
  }
}

// ════════════════════════════════════════════════════════════════
//  OCCUPANCY CARD
// ════════════════════════════════════════════════════════════════

class _OccupancyCard extends StatelessWidget {
  final int total, ocupadas, disponibles, limpieza, mantenimiento;
  const _OccupancyCard({
    required this.total,
    required this.ocupadas,
    required this.disponibles,
    required this.limpieza,
    required this.mantenimiento,
  });

  @override
  Widget build(BuildContext context) {
    final pct = total == 0 ? 0 : (ocupadas / total * 100).round();

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
            Text('OCUPACIÓN TOTAL',
                style: Theme.of(context).textTheme.labelLarge?.copyWith(
                      color: AppTheme.textMuted, letterSpacing: 1.0)),
            const SizedBox(height: 6),
            RichText(
              text: TextSpan(
                style: const TextStyle(
                  fontFamily: 'Georgia', fontSize: 52,
                  fontWeight: FontWeight.w700, color: AppTheme.textColor, height: 1,
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
            RichText(
              text: TextSpan(
                style: const TextStyle(fontSize: 12, color: AppTheme.textMuted),
                children: [
                  TextSpan(text: '$ocupadas de $total habitaciones · '),
                  const TextSpan(
                    text: '↑ +5% vs ayer',
                    style: TextStyle(
                        color: AppTheme.successColor, fontWeight: FontWeight.w600),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 18),
            _RoomBar(
              occupied: ocupadas, free: disponibles,
              maintenance: mantenimiento, total: total,
            ),
            const SizedBox(height: 10),
            Row(
              children: [
                _LegendItem(color: AppTheme.goldColor,   label: 'Ocupadas ($ocupadas)'),
                const SizedBox(width: 14),
                _LegendItem(color: AppTheme.borderColor, label: 'Libres ($disponibles)'),
                const SizedBox(width: 14),
                _LegendItem(color: AppTheme.errorColor,  label: 'Mant. ($mantenimiento)'),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _RoomBar extends StatelessWidget {
  final int occupied, free, maintenance, total;
  const _RoomBar({
    required this.occupied, required this.free,
    required this.maintenance, required this.total,
  });

  @override
  Widget build(BuildContext context) {
    const segments = 24;
    final occupiedSeg    = total == 0 ? 0 : (occupied    / total * segments).round();
    final maintenanceSeg = total == 0 ? 0 : (maintenance / total * segments).round();
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

class _LegendItem extends StatelessWidget {
  final Color color;
  final String label;
  const _LegendItem({required this.color, required this.label});

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Container(
          width: 7, height: 7,
          decoration: BoxDecoration(color: color, shape: BoxShape.circle),
        ),
        const SizedBox(width: 5),
        Text(label, style: Theme.of(context).textTheme.bodySmall),
      ],
    );
  }
}

// ════════════════════════════════════════════════════════════════
//  STATS ROW — Ingresos + Check-ins conectados a la API
// ════════════════════════════════════════════════════════════════

class _StatsRow extends StatelessWidget {
  final int activas, completadas, canceladas;
  final String ingresosHoy, ingresosMes;
  const _StatsRow({
    required this.activas,
    required this.completadas,
    required this.canceladas,
    required this.ingresosHoy,
    required this.ingresosMes,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Row(
        children: [
          Expanded(child: _IngresosCard(
            ingresosHoy: ingresosHoy,
            ingresosMes: ingresosMes,
          )),
          const SizedBox(width: 10),
          Expanded(child: _CheckinCard(
            activas: activas,
            completadas: completadas,
          )),
        ],
      ),
    );
  }
}

// ── Ingresos Card ─────────────────────────────────────────────

class _IngresosCard extends StatelessWidget {
  final String ingresosHoy, ingresosMes;
  const _IngresosCard({
    required this.ingresosHoy,
    required this.ingresosMes,
  });

  static const List<double> _weekBars = [0.4, 0.55, 0.65, 0.5, 0.75, 1.0];

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(24),
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
                  color: AppTheme.goldColor, size: 17),
              const SizedBox(width: 4),
              Text('INGRESOS HOY',
                  style: Theme.of(context).textTheme.labelLarge?.copyWith(
                        color: AppTheme.textMuted, fontSize: 10)),
            ],
          ),
          const SizedBox(height: 10),

          // Valor real desde la API
          Text(
            ingresosHoy,
            style: Theme.of(context)
                .textTheme
                .headlineMedium
                ?.copyWith(fontSize: 24, height: 1),
          ),
          const SizedBox(height: 4),
          Text(
            'Mes: $ingresosMes',
            style: const TextStyle(fontSize: 11, color: AppTheme.successColor),
          ),

          // Mini bar chart
          const SizedBox(height: 10),
          SizedBox(
            height: 30,
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.end,
              children: _weekBars.asMap().entries.map((e) {
                final isToday = e.key == _weekBars.length - 1;
                return Expanded(
                  child: Container(
                    margin: const EdgeInsets.symmetric(horizontal: 1.5),
                    decoration: BoxDecoration(
                      color: isToday
                          ? AppTheme.goldColor
                          : e.key >= 2
                              ? AppTheme.goldColor.withOpacity(0.35)
                              : AppTheme.borderColor,
                      borderRadius: const BorderRadius.vertical(
                          top: Radius.circular(3)),
                    ),
                    height: 30 * e.value,
                  ),
                );
              }).toList(),
            ),
          ),
          const SizedBox(height: 10),
          Container(
            height: 2,
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                  colors: [AppTheme.goldColor, Colors.transparent]),
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
  final int activas, completadas;
  const _CheckinCard({required this.activas, required this.completadas});

  @override
  Widget build(BuildContext context) {
    final total   = activas + completadas;
    final pctComp = total == 0 ? 0.0 : completadas / total;
    final pctAct  = total == 0 ? 0.0 : activas / total;

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
              Text('CHECK-INS',
                  style: Theme.of(context).textTheme.labelLarge?.copyWith(
                        color: AppTheme.textMuted, fontSize: 10)),
            ],
          ),
          const SizedBox(height: 8),

          // Valor
          Text(
            '$activas',
            style: Theme.of(context)
                .textTheme
                .headlineMedium
                ?.copyWith(fontSize: 24, height: 1),
          ),
          const SizedBox(height: 4),
          Text(
            '$completadas completadas',
            style: const TextStyle(fontSize: 11, color: AppTheme.successColor),
          ),
          const SizedBox(height: 10),

          // Progreso completadas
          _ProgressRow(
            label: 'Completadas',
            value: pctComp,
            done: completadas,
            total: total,
            color: AppTheme.successColor,
          ),
          const SizedBox(height: 8),

          // Progreso activas
          _ProgressRow(
            label: 'Activas',
            value: pctAct,
            done: activas,
            total: total,
            color: AppTheme.goldColor,
          ),
          const SizedBox(height: 10),
          Container(
            height: 2,
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                  colors: [AppTheme.successColor, Colors.transparent]),
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
  final double value;
  final int done, total;
  final Color color;
  const _ProgressRow({
    required this.label, required this.value,
    required this.done, required this.total, required this.color,
  });

  @override
  Widget build(BuildContext context) {
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
            value: value,
            minHeight: 4,
            backgroundColor: AppTheme.borderColor,
            valueColor: AlwaysStoppedAnimation<Color>(color),
          ),
        ),
      ],
    );
  }
}

// ════════════════════════════════════════════════════════════════
//  RESERVATIONS LIST — Datos reales del API
// ════════════════════════════════════════════════════════════════

class _ReservationsList extends StatelessWidget {
  final List<dynamic> reservas;
  const _ReservationsList({required this.reservas});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text('Reservas del día',
                  style: Theme.of(context).textTheme.titleMedium),
              TextButton(
                onPressed: () => Navigator.push(
                  context,
                  MaterialPageRoute(
                  builder: (_) => const ReservasDelDiaView(),
                ),
              ),
                child: const Text('Ver todas →'),
              ),
            ],
          ),
        ),
        const SizedBox(height: 4),
        if (reservas.isEmpty)
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
            child: Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: AppTheme.cardColor,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: AppTheme.borderColor),
              ),
              child: const Center(
                child: Text('No hay reservas activas hoy',
                    style: TextStyle(color: AppTheme.textMuted, fontSize: 13)),
              ),
            ),
          )
        else
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Column(
              children: reservas
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
  final dynamic data;
  const _ReservationCard({required this.data});

  // Extrae iniciales del nombre del cliente
  String get _initials {
    final nombre = (data['cliente'] ?? data['cliente_nombre'] ?? '??').toString();
    final partes = nombre.trim().split(' ');
    if (partes.length >= 2) {
      return '${partes[0][0]}${partes[1][0]}'.toUpperCase();
    }
    return nombre.substring(0, 2).toUpperCase();
  }

  // Badge según estado de la reserva
  (String, Color, Color) get _badge {
    final estado = (data['estado'] ?? '').toString().toLowerCase();
    return switch (estado) {
      'activa'     => ('ACTIVA',      AppTheme.successColor, const Color(0x264CAF82)),
      'completada' => ('COMPLETADA',  AppTheme.goldColor,    const Color(0x26C9A84C)),
      'cancelada'  => ('CANCELADA',   AppTheme.errorColor,   const Color(0x26E05C5C)),
      _            => ('RESERVA',     AppTheme.infoColor,    const Color(0x265B8DEE)),
    };
  }

  @override
  Widget build(BuildContext context) {
    final (label, color, bg) = _badge;
    final habitacion = data['habitacion']?.toString() ?? '---';
    final cliente    = (data['cliente'] ?? 'Sin nombre').toString();
    final entrada    = (data['entrada'] ?? '').toString();
    final salida     = (data['salida']  ?? '').toString();

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
      decoration: BoxDecoration(
        color: AppTheme.cardColor,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: AppTheme.borderColor),
      ),
      child: Row(
        children: [
          // Avatar con iniciales
          Container(
            width: 40, height: 40,
            decoration: BoxDecoration(
              color: AppTheme.goldDim,
              shape: BoxShape.circle,
              border: Border.all(
                  color: AppTheme.goldColor.withOpacity(0.4)),
            ),
            child: Center(
              child: Text(_initials,
                  style: const TextStyle(
                    fontFamily: 'Georgia', fontSize: 13,
                    fontWeight: FontWeight.w700, color: AppTheme.textColor,
                  )),
            ),
          ),
          const SizedBox(width: 12),

          // Info
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(cliente,
                    style: Theme.of(context)
                        .textTheme
                        .titleMedium
                        ?.copyWith(fontSize: 13)),
                const SizedBox(height: 2),
                Text(
                  'Entrada: $entrada · Salida: $salida',
                  style: Theme.of(context)
                      .textTheme
                      .bodySmall
                      ?.copyWith(fontSize: 11),
                ),
              ],
            ),
          ),

          // Número de habitación + badge
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(
                habitacion,
                style: const TextStyle(
                  fontFamily: 'Georgia', fontSize: 17,
                  fontWeight: FontWeight.w700, color: AppTheme.goldColor,
                ),
              ),
              const SizedBox(height: 3),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 7, vertical: 2),
                decoration: BoxDecoration(
                  color: bg,
                  borderRadius: BorderRadius.circular(20),
                  border: Border.all(color: color.withOpacity(0.4)),
                ),
                child: Text(label,
                    style: TextStyle(
                      fontSize: 8, fontWeight: FontWeight.w700,
                      color: color, letterSpacing: 0.5,
                    )),
              ),
            ],
          ),
        ],
      ),
    );
  }
}