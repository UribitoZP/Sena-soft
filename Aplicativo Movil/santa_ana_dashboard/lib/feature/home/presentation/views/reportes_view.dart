import 'package:flutter/material.dart';
import 'package:santa_ana_dashboard/core/services/api_service.dart';
import 'package:santa_ana_dashboard/core/theme/app_theme.dart';

// ════════════════════════════════════════════════════════════════
//  REPORTS VIEW — Conectada a /reportes
// ════════════════════════════════════════════════════════════════

class ReportesView extends StatefulWidget {
  const ReportesView({super.key});

  @override
  State<ReportesView> createState() => _ReportesViewState();
}

class _ReportesViewState extends State<ReportesView> {
  final _api = ApiService();
  late Future<Map<String, dynamic>> _future;
  int _selectedPeriod = 0;
  final _periods = ['Ingresos/Mes', 'Reservas/Mes'];

  @override
  void initState() {
    super.initState();
    _future = _api.getReportes();
  }

  void _refresh() => setState(() => _future = _api.getReportes());

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.bgColor,
      body: SafeArea(
        child: FutureBuilder<Map<String, dynamic>>(
          future: _future,
          builder: (context, snap) {
            if (snap.connectionState == ConnectionState.waiting) {
              return const Center(
                child: CircularProgressIndicator(color: AppTheme.goldColor),
              );
            }
            if (snap.hasError || snap.data == null) {
              return Center(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Icon(Icons.wifi_off_rounded,
                        color: AppTheme.errorColor, size: 48),
                    const SizedBox(height: 12),
                    const Text('Sin conexión al servidor',
                        style: TextStyle(
                            color: AppTheme.errorColor, fontSize: 14)),
                    const SizedBox(height: 16),
                    ElevatedButton(
                      onPressed: _refresh,
                      child: const Text('Reintentar'),
                    ),
                  ],
                ),
              );
            }

            final data  = snap.data!;
            final kpis  = data['kpis']  as Map<String, dynamic>? ?? {};
            final ingresosMes  = (data['ingresosPorMes']  as List<dynamic>?) ?? [];
            final reservasMes  = (data['reservasPorMes']  as List<dynamic>?) ?? [];
            final topHabs      = (data['topHabitaciones'] as List<dynamic>?) ?? [];

            return RefreshIndicator(
              color: AppTheme.goldColor,
              onRefresh: () async => _refresh(),
              child: CustomScrollView(
                physics: const AlwaysScrollableScrollPhysics(
                    parent: BouncingScrollPhysics()),
                slivers: [
                  SliverToBoxAdapter(child: _buildHeader()),
                  SliverToBoxAdapter(child: _buildKpiGrid(kpis)),
                  SliverToBoxAdapter(child: _buildChartToggle()),
                  SliverToBoxAdapter(
                    child: _selectedPeriod == 0
                        ? _buildBarChart(
                            'Ingresos por mes',
                            ingresosMes,
                            isMoneda: true,
                          )
                        : _buildBarChart(
                            'Reservas por mes',
                            reservasMes,
                            isMoneda: false,
                          ),
                  ),
                  SliverToBoxAdapter(
                      child: _buildTopHabs(topHabs)),
                  const SliverToBoxAdapter(child: SizedBox(height: 16)),
                ],
              ),
            );
          },
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
                fontFamily: 'Georgia', fontSize: 22,
                fontWeight: FontWeight.w700, color: AppTheme.textColor,
              ),
              children: [
                TextSpan(text: 'Reportes '),
                TextSpan(
                  text: '· Contabilidad',
                  style: TextStyle(color: AppTheme.goldColor),
                ),
              ],
            ),
          ),
          GestureDetector(
            onTap: _refresh,
            child: Container(
              width: 38, height: 38,
              decoration: BoxDecoration(
                color: AppTheme.cardColor,
                borderRadius: BorderRadius.circular(10),
                border: Border.all(color: AppTheme.borderColor),
              ),
              child: const Icon(Icons.refresh_rounded,
                  color: AppTheme.goldColor, size: 18),
            ),
          ),
        ],
      ),
    );
  }

  // ── KPI Grid ────────────────────────────────────────────────

  Widget _buildKpiGrid(Map<String, dynamic> kpis) {
    final ingresosConf  = kpis['ingresosConfirmadosFormato']  ?? '\$0';
    final anticipos     = kpis['anticiposTotalesFormato']     ?? '\$0';
    final totalRes      = kpis['totalReservas']               ?? 0;
    final activas       = kpis['activas']                     ?? 0;
    final completadas   = kpis['completadas']                 ?? 0;
    final canceladas    = kpis['canceladas']                  ?? 0;
    final tasaCanc      = (kpis['tasaCancelacion'] as num?)?.toDouble() ?? 0.0;

    final items = [
      ('💰', 'INGRESOS CONF.',  '$ingresosConf',    'De reservas completadas', AppTheme.successColor),
      ('🏦', 'ANTICIPOS',       '$anticipos',        'Suma de todos los anticipos', AppTheme.infoColor),
      ('🛎️', 'TOTAL RESERVAS',  '$totalRes',         '$activas activas · $completadas comp.', AppTheme.goldColor),
      ('❌', 'CANCELACIONES',   '$canceladas',       '${tasaCanc.toStringAsFixed(1)}% tasa cancelación', AppTheme.errorColor),
    ];

    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 16, 16, 0),
      child: GridView.count(
        crossAxisCount: 2,
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        crossAxisSpacing: 10,
        mainAxisSpacing: 10,
        childAspectRatio: 1.55,
        children: items.map((k) {
          final (icon, label, value, sub, color) = k;
          return Container(
            padding: const EdgeInsets.all(14),
            decoration: BoxDecoration(
              color: AppTheme.cardColor,
              borderRadius: BorderRadius.circular(16),
              border: Border.all(color: AppTheme.borderColor),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('$icon $label',
                    style: const TextStyle(
                        fontSize: 9, color: AppTheme.textMuted, letterSpacing: 0.5)),
                Text(value,
                    style: TextStyle(
                      fontFamily: 'Georgia', fontSize: 20,
                      fontWeight: FontWeight.w700, color: color, height: 1,
                    )),
                Text(sub,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: const TextStyle(fontSize: 9, color: AppTheme.textMuted)),
                Container(
                  height: 2,
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                        colors: [color, Colors.transparent]),
                    borderRadius: BorderRadius.circular(1),
                  ),
                ),
              ],
            ),
          );
        }).toList(),
      ),
    );
  }

  // ── Chart toggle ─────────────────────────────────────────────

  Widget _buildChartToggle() {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 16, 16, 0),
      child: Row(
        children: _periods.asMap().entries.map((e) {
          final isActive = e.key == _selectedPeriod;
          return Padding(
            padding: EdgeInsets.only(right: e.key == 0 ? 8 : 0),
            child: GestureDetector(
              onTap: () => setState(() => _selectedPeriod = e.key),
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
                child: Text(e.value,
                    style: TextStyle(
                      fontSize: 12, fontWeight: FontWeight.w600,
                      color: isActive ? AppTheme.goldColor : AppTheme.textMuted,
                    )),
              ),
            ),
          );
        }).toList(),
      ),
    );
  }

  // ── Bar chart ────────────────────────────────────────────────

  Widget _buildBarChart(String titulo, List<dynamic> data,
      {required bool isMoneda}) {
    if (data.isEmpty) return _emptyCard(titulo);

    final valores = data
        .map((e) => (e['valor'] as num).toDouble())
        .toList();
    final maxVal = valores.reduce((a, b) => a > b ? a : b);

    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 12, 16, 0),
      child: Container(
        padding: const EdgeInsets.all(18),
        decoration: BoxDecoration(
          color: AppTheme.cardColor,
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: AppTheme.borderColor),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(titulo,
                style: Theme.of(context).textTheme.titleMedium),
            const SizedBox(height: 4),
            Text('Últimos 6 meses',
                style: const TextStyle(
                    fontSize: 11, color: AppTheme.textMuted)),
            const SizedBox(height: 16),
            SizedBox(
              height: 100,
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: data.asMap().entries.map((e) {
                  final item   = e.value as Map<String, dynamic>;
                  final val    = (item['valor'] as num).toDouble();
                  final mes    = (item['mes'] as String).length >= 7
                      ? (item['mes'] as String).substring(5)
                      : item['mes'] as String;
                  final isLast = e.key == data.length - 1;
                  final ratio  = maxVal == 0 ? 0.0 : val / maxVal;

                  return Expanded(
                    child: Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 3),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.end,
                        children: [
                          // Valor encima
                          if (val > 0)
                            Text(
                              isMoneda
                                  ? _abreviar(val)
                                  : val.toInt().toString(),
                              style: TextStyle(
                                fontSize: 8,
                                color: isLast
                                    ? AppTheme.goldColor
                                    : AppTheme.textMuted,
                              ),
                            ),
                          const SizedBox(height: 2),
                          // Barra
                          AnimatedContainer(
                            duration: const Duration(milliseconds: 600),
                            width: double.infinity,
                            height: (70 * ratio).clamp(4.0, 70.0),
                            decoration: BoxDecoration(
                              color: isLast
                                  ? AppTheme.goldColor
                                  : AppTheme.goldColor.withOpacity(0.4),
                              borderRadius: const BorderRadius.vertical(
                                  top: Radius.circular(4)),
                            ),
                          ),
                          const SizedBox(height: 6),
                          // Mes
                          Text(mes,
                              style: TextStyle(
                                fontSize: 9,
                                color: isLast
                                    ? AppTheme.goldColor
                                    : AppTheme.textMuted,
                                fontWeight: isLast
                                    ? FontWeight.w700
                                    : FontWeight.w400,
                              )),
                        ],
                      ),
                    ),
                  );
                }).toList(),
              ),
            ),
          ],
        ),
      ),
    );
  }

  // ── Top habitaciones ─────────────────────────────────────────

  Widget _buildTopHabs(List<dynamic> data) {
    if (data.isEmpty) return _emptyCard('Top habitaciones');

    final maxVal = data
        .map((e) => (e['reservas'] as num).toDouble())
        .reduce((a, b) => a > b ? a : b);

    final colors = [
      AppTheme.goldColor,
      AppTheme.successColor,
      AppTheme.infoColor,
      AppTheme.errorColor,
      const Color(0xFFA855F7),
    ];

    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 12, 16, 0),
      child: Container(
        padding: const EdgeInsets.all(18),
        decoration: BoxDecoration(
          color: AppTheme.cardColor,
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: AppTheme.borderColor),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Top habitaciones más reservadas',
                style: Theme.of(context).textTheme.titleMedium),
            const SizedBox(height: 16),
            ...data.asMap().entries.map((e) {
              final item  = e.value as Map<String, dynamic>;
              final hab   = item['habitacion'] as String;
              final res   = (item['reservas'] as num).toInt();
              final ratio = maxVal == 0 ? 0.0 : res / maxVal;
              final color = colors[e.key % colors.length];

              return Padding(
                padding: const EdgeInsets.only(bottom: 12),
                child: Column(
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(hab,
                            style: const TextStyle(
                                fontSize: 12,
                                color: AppTheme.textColor,
                                fontWeight: FontWeight.w600)),
                        Text('$res reservas',
                            style: TextStyle(
                                fontSize: 11, color: color,
                                fontWeight: FontWeight.w600)),
                      ],
                    ),
                    const SizedBox(height: 5),
                    ClipRRect(
                      borderRadius: BorderRadius.circular(3),
                      child: LinearProgressIndicator(
                        value: ratio,
                        minHeight: 5,
                        backgroundColor: AppTheme.borderColor,
                        valueColor: AlwaysStoppedAnimation<Color>(color),
                      ),
                    ),
                  ],
                ),
              );
            }),
          ],
        ),
      ),
    );
  }

  // ── Helpers ──────────────────────────────────────────────────

  Widget _emptyCard(String titulo) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 12, 16, 0),
      child: Container(
        height: 100,
        decoration: BoxDecoration(
          color: AppTheme.cardColor,
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: AppTheme.borderColor),
        ),
        child: Center(
          child: Text('Sin datos para $titulo',
              style: const TextStyle(
                  color: AppTheme.textMuted, fontSize: 13)),
        ),
      ),
    );
  }

  String _abreviar(double val) {
    if (val >= 1_000_000) return '\$${(val / 1_000_000).toStringAsFixed(1)}M';
    if (val >= 1_000)     return '\$${(val / 1_000).toStringAsFixed(0)}k';
    return '\$${val.toInt()}';
  }
}