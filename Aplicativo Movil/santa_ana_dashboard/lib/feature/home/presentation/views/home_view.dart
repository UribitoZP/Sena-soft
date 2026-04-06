import 'package:flutter/material.dart';
import 'package:santa_ana_dashboard/core/services/api_service.dart';
import 'package:santa_ana_dashboard/core/theme/app_theme.dart';


//  DASHBOARD VIEW

class HomeView extends StatefulWidget {
  const HomeView({super.key});

  @override
  State<HomeView> createState() => _HomeViewState();
}

class _HomeViewState extends State<HomeView> {
  final _api = ApiService();
  late Future<Map<String, dynamic>> _statsFuture;

  @override
  void initState() {
    super.initState();
    _statsFuture = _api.getStats();
  }

  void _refresh() {
    setState(() {
      _statsFuture = _api.getStats();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.bgColor,
      body: SafeArea(
        child: Column(
          children: [
            Expanded(
              child: FutureBuilder<Map<String, dynamic>>(
                future: _statsFuture,
                builder: (context, snap) {
                  final stats = snap.data;
                  final hab   = stats?['habitaciones'] as Map<String, dynamic>?;
                  final res   = stats?['reservas']     as Map<String, dynamic>?;

                  return RefreshIndicator(
                    onRefresh: () async => _refresh(),
                    child: SingleChildScrollView(
                      physics: const AlwaysScrollableScrollPhysics(
                          parent: BouncingScrollPhysics()),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const _DashboardHeader(),
                          const SizedBox(height: 4),
                          const _DateStrip(),
                          const SizedBox(height: 8),
                          if (snap.connectionState == ConnectionState.waiting)
                            const Padding(
                              padding: EdgeInsets.all(32),
                              child: Center(child: CircularProgressIndicator(color: AppTheme.goldColor)),
                            )
                          else if (snap.hasError)
                            Padding(
                              padding: const EdgeInsets.all(24),
                              child: Text(
                                'Sin conexión al servidor.\nVerifica la IP en ApiService.',
                                style: TextStyle(color: AppTheme.errorColor),
                                textAlign: TextAlign.center,
                              ),
                            )
                          else ...[
                            _OccupancyCard(
                              total:       (hab?['total']       ?? 0) as int,
                              ocupadas:    (hab?['ocupadas']    ?? 0) as int,
                              disponibles: (hab?['disponibles'] ?? 0) as int,
                              limpieza:    (hab?['limpieza']    ?? 0) as int,
                              mantenimiento:(hab?['mantenimiento'] ?? 0) as int,
                            ),
                            const SizedBox(height: 12),
                            _ReservasStatsRow(
                              activas:     (res?['activas']     ?? 0) as int,
                              completadas: (res?['completadas'] ?? 0) as int,
                              canceladas:  (res?['canceladas']  ?? 0) as int,
                            ),
                          ],
                          const SizedBox(height: 16),
                        ],
                      ),
                    ),
                  );
                },
              ),
            ),
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
    final totalRooms      = total;
    final occupiedRooms   = ocupadas;
    final maintenanceRooms = mantenimiento;
    final freeRooms = disponibles;
    final pct = totalRooms == 0 ? 0 : (occupiedRooms / totalRooms * 100).round();

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


//  RESERVAS STATS ROW


class _ReservasStatsRow extends StatelessWidget {
  final int activas, completadas, canceladas;
  const _ReservasStatsRow({
    required this.activas,
    required this.completadas,
    required this.canceladas,
  });

  @override
  Widget build(BuildContext context) {
    final items = [
      ('$activas',     'Activas',     AppTheme.goldColor),
      ('$completadas', 'Completadas', AppTheme.successColor),
      ('$canceladas',  'Canceladas',  AppTheme.errorColor),
    ];
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.only(bottom: 10),
            child: Text('Reservas',
                style: Theme.of(context).textTheme.titleMedium),
          ),
          Row(
            children: items.asMap().entries.map((e) {
              final (val, label, color) = e.value;
              return Expanded(
                child: Container(
                  margin: EdgeInsets.only(left: e.key == 0 ? 0 : 8),
                  padding: const EdgeInsets.symmetric(vertical: 14),
                  decoration: BoxDecoration(
                    color: AppTheme.cardColor,
                    borderRadius: BorderRadius.circular(14),
                    border: Border.all(color: AppTheme.borderColor),
                  ),
                  child: Column(
                    children: [
                      Text(
                        val,
                        style: TextStyle(
                          fontFamily: 'Georgia',
                          fontSize: 24,
                          fontWeight: FontWeight.w700,
                          color: color,
                          height: 1,
                        ),
                      ),
                      const SizedBox(height: 4),
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
        ],
      ),
    );
  }
}


