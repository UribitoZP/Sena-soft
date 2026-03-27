import 'package:flutter/material.dart';
import 'package:santa_ana_dashboard/core/theme/app_theme.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/views/home_view.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/views/habitaciones_view.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/views/reservas_view.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/views/reportes_view.dart';


//  DASHBOARD VIEW — Nav centralizado con IndexedStack


class DashboardView extends StatefulWidget {
  const DashboardView({super.key});

  @override
  State<DashboardView> createState() => _DashboardViewState();
}

class _DashboardViewState extends State<DashboardView> {
  int _currentIndex = 0;

  // Agrega aquí cada vista nueva que crees
  final List<Widget> _views = [
    const HomeView(),
    const ReservationsView(),
    const RoomsView(),
    const ReportsView()
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.bgColor,
      // IndexedStack mantiene todas las vistas vivas
      // y solo muestra la del índice actual
      body: IndexedStack(
        index: _currentIndex,
        children: _views,
      ),
      bottomNavigationBar: _BottomNav(
        currentIndex: _currentIndex,
        onTap: (i) => setState(() => _currentIndex = i),
      ),
    );
  }
}


//  BOTTOM NAV — Compartido entre todas las vistas


class _BottomNav extends StatelessWidget {
  final int currentIndex;
  final ValueChanged<int> onTap;

  const _BottomNav({required this.currentIndex, required this.onTap});

  static const _items = [
    (icon: Icons.home_rounded,           label: 'INICIO'),
    (icon: Icons.calendar_month_rounded, label: 'RESERVAS'),
    (icon: Icons.bed_rounded,            label: 'HABITACIONES'),
    (icon: Icons.bar_chart_rounded,      label: 'REPORTES'),
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
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.3),
            blurRadius: 20,
            offset: const Offset(0, -4),
          ),
        ],
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: _items.asMap().entries.map((e) {
          final isActive = e.key == currentIndex;
          return GestureDetector(
            onTap: () => onTap(e.key),
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