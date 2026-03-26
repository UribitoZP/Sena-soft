import 'package:flutter/material.dart';
import 'package:santa_ana_dashboard/core/theme/app_theme.dart';

//  MODELOS

class WeekDayData {
  final String label;
  final double value; // 0.0 - 1.0
  final bool isToday;
  const WeekDayData({required this.label, required this.value, this.isToday = false});
}

class CategorySummary {
  final String icon, name, detail, amount, pct;
  final Color iconBg;
  const CategorySummary({
    required this.icon, required this.name, required this.detail,
    required this.amount, required this.pct, required this.iconBg,
  });
}

// Datos de ejemplo — reemplazar con datos del BLoC
const _weekBars = [
  WeekDayData(label: 'LUN', value: 0.55),
  WeekDayData(label: 'MAR', value: 0.70),
  WeekDayData(label: 'MIÉ', value: 0.45),
  WeekDayData(label: 'JUE', value: 0.85),
  WeekDayData(label: 'HOY', value: 1.00, isToday: true),
  WeekDayData(label: 'SÁB', value: 0.30),
  WeekDayData(label: 'DOM', value: 0.20),
];

const _occupancyPoints = [0.65, 0.72, 0.58, 0.87, 0.84, 0.0, 0.0];

const _categories = [
  CategorySummary(
    icon: '🏨', name: 'Suites', detail: '12 reservas · 100% ocupación',
    amount: '\$42.0k', pct: '47% del total',
    iconBg: Color(0x26C9A84C),
  ),
  CategorySummary(
    icon: '🛏️', name: 'Estándar', detail: '68 reservas · 78% ocupación',
    amount: '\$31.2k', pct: '35% del total',
    iconBg: Color(0x265B8DEE),
  ),
  CategorySummary(
    icon: '🌿', name: 'Junior Suite', detail: '22 reservas · 88% ocupación',
    amount: '\$16.2k', pct: '18% del total',
    iconBg: Color(0x264CAF82),
  ),
];

//  REPORTS VIEW

class ReportsView extends StatefulWidget {
  const ReportsView({super.key});

  @override
  State<ReportsView> createState() => _ReportsViewState();
}

class _ReportsViewState extends State<ReportsView> {
  int _selectedPeriod = 1;
  final _periods = ['Hoy', 'Semana', 'Mes', 'Año'];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.bgColor,
      body: SafeArea(
        child: CustomScrollView(
          physics: const BouncingScrollPhysics(),
          slivers: [
            SliverToBoxAdapter(child: _buildHeader()),
            SliverToBoxAdapter(child: _buildPeriodSelector()),
            SliverToBoxAdapter(child: _buildKpiGrid()),
            SliverToBoxAdapter(child: _buildBarChart()),
            SliverToBoxAdapter(child: _buildLineChart()),
            SliverToBoxAdapter(child: _buildSummaryTitle()),
            SliverPadding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              sliver: SliverList(
                delegate: SliverChildBuilderDelegate(
                  (context, i) => Padding(
                    padding: const EdgeInsets.only(bottom: 10),
                    child: _CategoryRow(data: _categories[i]),
                  ),
                  childCount: _categories.length,
                ),
              ),
            ),
            const SliverToBoxAdapter(child: SizedBox(height: 16)),
          ],
        ),
      ),
    );
  }

  // ── Header ──────────────────────────────────────────────────

  Widget _buildHeader() {
    return Padding(
      padding: const EdgeInsets.fromLTRB(24, 18, 24, 0),
      child: RichText(
        text: const TextSpan(
          style: TextStyle(
            fontFamily: 'Georgia', fontSize: 22,
            fontWeight: FontWeight.w700, color: AppTheme.textColor,
          ),
          children: [
            TextSpan(text: 'Reportes '),
            TextSpan(text: '· Marzo',
                style: TextStyle(color: AppTheme.goldColor)),
          ],
        ),
      ),
    );
  }

  // ── Period selector ──────────────────────────────────────────

  Widget _buildPeriodSelector() {
    return SizedBox(
      height: 52,
      child: ListView.separated(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.fromLTRB(24, 14, 24, 0),
        itemCount: _periods.length,
        separatorBuilder: (_, __) => const SizedBox(width: 8),
        itemBuilder: (context, i) {
          final isActive = i == _selectedPeriod;
          return GestureDetector(
            onTap: () => setState(() => _selectedPeriod = i),
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
                _periods[i],
                style: TextStyle(
                  fontSize: 12, fontWeight: FontWeight.w600,
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

  // ── KPI grid ────────────────────────────────────────────────

  Widget _buildKpiGrid() {
    final kpis = [
      ('💰', 'INGRESOS',      '\$89.4k', '↑ 14% vs semana ant.', true,  AppTheme.goldColor),
      ('📈', 'OCUPACIÓN',     '84%',     '↑ 5% vs semana ant.',  true,  AppTheme.successColor),
      ('🛎️', 'RESERVAS',      '142',     '↑ 8 nuevas',           true,  AppTheme.infoColor),
      ('❌', 'CANCELACIONES', '7',       '↓ 3 vs semana ant.',   false, AppTheme.errorColor),
    ];

    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 14, 16, 0),
      child: GridView.count(
        crossAxisCount: 2,
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        crossAxisSpacing: 10,
        mainAxisSpacing: 10,
        childAspectRatio: 1.6,
        children: kpis.map((k) {
          final (icon, label, value, change, isUp, color) = k;
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
                      fontSize: 10, color: AppTheme.textMuted,
                      letterSpacing: 0.5,
                    )),
                Text(value,
                    style: TextStyle(
                      fontFamily: 'Georgia', fontSize: 22,
                      fontWeight: FontWeight.w700, color: AppTheme.textColor,
                      height: 1,
                    )),
                Text(change,
                    style: TextStyle(
                      fontSize: 10,
                      color: isUp ? AppTheme.successColor : AppTheme.errorColor,
                    )),
                // Barra inferior de color
                Container(
                  height: 2,
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      colors: [color, Colors.transparent],
                    ),
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

  // ── Bar chart ────────────────────────────────────────────────

  Widget _buildBarChart() {
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
            // Header
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text('Ingresos por día',
                        style: Theme.of(context).textTheme.titleMedium),
                    const SizedBox(height: 2),
                    const Text('Esta semana',
                        style: TextStyle(fontSize: 11, color: AppTheme.textMuted)),
                  ],
                ),
                RichText(
                  text: const TextSpan(
                    children: [
                      TextSpan(
                        text: '\$89.4k',
                        style: TextStyle(
                          fontFamily: 'Georgia', fontSize: 18,
                          fontWeight: FontWeight.w700, color: AppTheme.goldColor,
                        ),
                      ),
                      TextSpan(
                        text: ' total',
                        style: TextStyle(fontSize: 11, color: AppTheme.textMuted),
                      ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            // Barras
            SizedBox(
              height: 80,
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: _weekBars.map((d) {
                  return Expanded(
                    child: Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 3),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.end,
                        children: [
                          Expanded(
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.end,
                              children: [
                                AnimatedContainer(
                                  duration: const Duration(milliseconds: 600),
                                  width: double.infinity,
                                  height: 60 * d.value,
                                  decoration: BoxDecoration(
                                    color: d.isToday
                                        ? AppTheme.goldColor
                                        : d.value > 0
                                            ? AppTheme.goldColor.withOpacity(0.45)
                                            : AppTheme.borderColor,
                                    borderRadius: const BorderRadius.vertical(
                                        top: Radius.circular(4)),
                                  ),
                                ),
                              ],
                            ),
                          ),
                          const SizedBox(height: 6),
                          Text(
                            d.label,
                            style: TextStyle(
                              fontSize: 9,
                              color: d.isToday
                                  ? AppTheme.goldColor
                                  : AppTheme.textMuted,
                              fontWeight: d.isToday
                                  ? FontWeight.w700
                                  : FontWeight.w400,
                            ),
                          ),
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

  // ── Line chart ───────────────────────────────────────────────

  Widget _buildLineChart() {
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
            Text('Ocupación semanal',
                style: Theme.of(context).textTheme.titleMedium),
            const SizedBox(height: 2),
            const Text('Promedio: 84% esta semana',
                style: TextStyle(fontSize: 11, color: AppTheme.textMuted)),
            const SizedBox(height: 16),
            SizedBox(
              height: 80,
              child: CustomPaint(
                size: const Size(double.infinity, 80),
                painter: _LineChartPainter(points: _occupancyPoints),
              ),
            ),
            const SizedBox(height: 8),
            // Labels
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: ['LUN', 'MAR', 'MIÉ', 'JUE', 'HOY', 'SÁB', 'DOM']
                  .map((d) => Text(
                        d,
                        style: TextStyle(
                          fontSize: 9,
                          color: d == 'HOY'
                              ? AppTheme.goldColor
                              : AppTheme.textMuted,
                          fontWeight: d == 'HOY'
                              ? FontWeight.w700
                              : FontWeight.w400,
                        ),
                      ))
                  .toList(),
            ),
          ],
        ),
      ),
    );
  }

  // ── Summary title ────────────────────────────────────────────

  Widget _buildSummaryTitle() {
    return Padding(
      padding: const EdgeInsets.fromLTRB(24, 18, 24, 10),
      child: Text('Resumen por categoría',
          style: Theme.of(context).textTheme.titleMedium),
    );
  }
}

//  LINE CHART PAINTER

class _LineChartPainter extends CustomPainter {
  final List<double> points;
  const _LineChartPainter({required this.points});

  @override
  void paint(Canvas canvas, Size size) {
    // Solo pinta los puntos con valor > 0
    final activePoints = points.where((p) => p > 0).toList();
    if (activePoints.length < 2) return;

    final spacing = size.width / (points.length - 1);
    final activeOffsets = <Offset>[];

    for (int i = 0; i < points.length; i++) {
      if (points[i] > 0) {
        activeOffsets.add(Offset(
          i * spacing,
          size.height - (points[i] * size.height * 0.85) - 8,
        ));
      }
    }

    // Path de la línea
    final path = Path()..moveTo(activeOffsets.first.dx, activeOffsets.first.dy);
    for (int i = 1; i < activeOffsets.length; i++) {
      final prev = activeOffsets[i - 1];
      final curr = activeOffsets[i];
      final cp1 = Offset((prev.dx + curr.dx) / 2, prev.dy);
      final cp2 = Offset((prev.dx + curr.dx) / 2, curr.dy);
      path.cubicTo(cp1.dx, cp1.dy, cp2.dx, cp2.dy, curr.dx, curr.dy);
    }

    // Area rellena
    final areaPath = Path.from(path)
      ..lineTo(activeOffsets.last.dx, size.height)
      ..lineTo(activeOffsets.first.dx, size.height)
      ..close();

    canvas.drawPath(
      areaPath,
      Paint()
        ..shader = LinearGradient(
          begin: Alignment.topCenter,
          end: Alignment.bottomCenter,
          colors: [
            AppTheme.goldColor.withOpacity(0.25),
            AppTheme.goldColor.withOpacity(0.0),
          ],
        ).createShader(Rect.fromLTWH(0, 0, size.width, size.height)),
    );

    // Línea
    canvas.drawPath(
      path,
      Paint()
        ..color = AppTheme.goldColor
        ..strokeWidth = 2
        ..style = PaintingStyle.stroke
        ..strokeCap = StrokeCap.round,
    );

    // Puntos
    for (int i = 0; i < activeOffsets.length; i++) {
      final isLast = i == activeOffsets.length - 1;
      canvas.drawCircle(
        activeOffsets[i],
        isLast ? 4 : 3,
        Paint()..color = AppTheme.goldColor,
      );
      if (isLast) {
        canvas.drawCircle(
          activeOffsets[i],
          4,
          Paint()
            ..color = AppTheme.bgColor
            ..style = PaintingStyle.stroke
            ..strokeWidth = 2,
        );
      }
    }
  }

  @override
  bool shouldRepaint(_LineChartPainter old) => old.points != points;
}

//  CATEGORY ROW

class _CategoryRow extends StatelessWidget {
  final CategorySummary data;
  const _CategoryRow({required this.data});

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
          // Ícono
          Container(
            width: 38, height: 38,
            decoration: BoxDecoration(
              color: data.iconBg,
              borderRadius: BorderRadius.circular(10),
            ),
            child: Center(child: Text(data.icon, style: const TextStyle(fontSize: 18))),
          ),
          const SizedBox(width: 12),
          // Info
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(data.name,
                    style: Theme.of(context)
                        .textTheme
                        .titleMedium
                        ?.copyWith(fontSize: 13)),
                const SizedBox(height: 2),
                Text(data.detail,
                    style: const TextStyle(
                        fontSize: 11, color: AppTheme.textMuted)),
              ],
            ),
          ),
          // Valores
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(data.amount,
                  style: const TextStyle(
                    fontFamily: 'Georgia', fontSize: 16,
                    fontWeight: FontWeight.w700, color: AppTheme.goldColor,
                  )),
              const SizedBox(height: 2),
              Text(data.pct,
                  style: const TextStyle(
                      fontSize: 10, color: AppTheme.successColor)),
            ],
          ),
        ],
      ),
    );
  }
}