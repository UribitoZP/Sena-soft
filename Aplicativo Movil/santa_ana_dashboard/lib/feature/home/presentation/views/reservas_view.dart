import 'dart:async';
import 'package:flutter/material.dart';
import 'package:santa_ana_dashboard/core/services/api_service.dart';
import 'package:santa_ana_dashboard/core/theme/app_theme.dart';

//  MODELOS

enum ReservationStatus { activa, completada, cancelada }

class ReservationModel {
  final int id;
  final String guestName, doc, room, entrada, salida;
  final ReservationStatus status;

  const ReservationModel({
    required this.id,
    required this.guestName,
    required this.doc,
    required this.room,
    required this.entrada,
    required this.salida,
    required this.status,
  });

  factory ReservationModel.fromJson(Map<String, dynamic> j) {
    final est = j['estado'] as String;
    final status = switch (est) {
      'Completada' => ReservationStatus.completada,
      'Cancelada'  => ReservationStatus.cancelada,
      _            => ReservationStatus.activa,
    };
    return ReservationModel(
      id:        (j['id'] as num).toInt(),
      guestName: j['cliente']   as String,
      doc:       j['doc']       as String,
      room:      j['habitacion'] as String,
      entrada:   j['entrada']   as String,
      salida:    j['salida']    as String,
      status:    status,
    );
  }

  String get initials {
    final parts = guestName.trim().split(' ');
    if (parts.length >= 2) return '${parts[0][0]}${parts[1][0]}'.toUpperCase();
    return guestName.substring(0, guestName.length.clamp(0, 2)).toUpperCase();
  }
}

//  RESERVATIONS VIEW

class ReservationsView extends StatefulWidget {
  const ReservationsView({super.key});

  @override
  State<ReservationsView> createState() => _ReservationsViewState();
}

class _ReservationsViewState extends State<ReservationsView> {
  final _api = ApiService();
  late Future<List<ReservationModel>> _reservasFuture;
  int _selectedFilter = 0;
  final _filters = ['Todas', 'Activas', 'Completadas', 'Canceladas'];
  final _searchController = TextEditingController();
  String _search = '';
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    _loadReservas();
    _searchController.addListener(() {
      setState(() => _search = _searchController.text.toLowerCase());
    });

    _timer = Timer.periodic(const Duration(seconds: 10), (_) {
      if (mounted) {
        setState(() {
          _loadReservas();
        });
      }
    });
  }

  void _loadReservas() {
    _reservasFuture = _api
        .getReservas()
        .then((list) => list.map(ReservationModel.fromJson).toList());
  }

  List<ReservationModel> _applyFilter(List<ReservationModel> all) {
    var result = all;
    if (_selectedFilter > 0) {
      final map = {
        1: ReservationStatus.activa,
        2: ReservationStatus.completada,
        3: ReservationStatus.cancelada,
      };
      result = result.where((r) => r.status == map[_selectedFilter]).toList();
    }
    if (_search.isNotEmpty) {
      result = result
          .where((r) =>
              r.guestName.toLowerCase().contains(_search) ||
              r.room.contains(_search) ||
              r.doc.contains(_search))
          .toList();
    }
    return result;
  }

  @override
  void dispose() {
    _timer?.cancel();
    _searchController.dispose();
    super.dispose();
}

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.bgColor,
      body: SafeArea(
        child: FutureBuilder<List<ReservationModel>>(
          future: _reservasFuture,
          builder: (context, snap) {
            if (snap.connectionState == ConnectionState.waiting) {
              return const Center(
                  child: CircularProgressIndicator(color: AppTheme.goldColor));
            }
            if (snap.hasError) {
              return Center(
                child: Padding(
                  padding: const EdgeInsets.all(24),
                  child: Text(
                    'Sin conexión al servidor.\n${snap.error}',
                    style: const TextStyle(color: AppTheme.errorColor),
                    textAlign: TextAlign.center,
                  ),
                ),
              );
            }
            final allReservas = snap.data ?? [];
            final filtered = _applyFilter(allReservas);
            final activas     = allReservas.where((r) => r.status == ReservationStatus.activa).length;
            final completadas = allReservas.where((r) => r.status == ReservationStatus.completada).length;
            final canceladas  = allReservas.where((r) => r.status == ReservationStatus.cancelada).length;

            return RefreshIndicator(
              onRefresh: () async => setState(() => _loadReservas()),
              child: CustomScrollView(
                physics: const AlwaysScrollableScrollPhysics(
                    parent: BouncingScrollPhysics()),
                slivers: [
                  SliverToBoxAdapter(child: _buildHeader()),
                  SliverToBoxAdapter(child: _buildFilterTabs()),
                  SliverToBoxAdapter(child: _buildSearchBar()),
                  SliverToBoxAdapter(child: _buildStatsStrip(
                    activas: activas,
                    completadas: completadas,
                    canceladas: canceladas,
                  )),
                  SliverToBoxAdapter(child: _buildSectionTitle(filtered.length)),
                  SliverPadding(
                    padding: const EdgeInsets.symmetric(horizontal: 16),
                    sliver: SliverList(
                      delegate: SliverChildBuilderDelegate(
                        (context, i) => Padding(
                          padding: const EdgeInsets.only(bottom: 10),
                          child: _ReservationCard(data: filtered[i]),
                        ),
                        childCount: filtered.length,
                      ),
                    ),
                  ),
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

  Widget _buildStatsStrip({
    required int activas,
    required int completadas,
    required int canceladas,
  }) {
    final stats = [
      ('${activas + completadas + canceladas}', 'Total',      AppTheme.goldColor),
      ('$activas',     'Activas',     AppTheme.successColor),
      ('$completadas', 'Completadas', AppTheme.infoColor),
      ('$canceladas',  'Canceladas',  AppTheme.errorColor),
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

  Widget _buildSectionTitle(int count) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(24, 18, 24, 10),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text('Reservas', style: Theme.of(context).textTheme.titleMedium),
          Text('$count en total', style: Theme.of(context).textTheme.bodySmall),
        ],
      ),
    );
  }
}

//  RESERVATION CARD

class _ReservationCard extends StatelessWidget {
  final ReservationModel data;
  const _ReservationCard({required this.data});

  (Color, String, Color, Color) get _statusProps => switch (data.status) {
    ReservationStatus.activa     => (AppTheme.goldColor,    'ACTIVA',     AppTheme.goldColor,    const Color(0x26C9A84C)),
    ReservationStatus.completada => (AppTheme.successColor, 'COMPLETADA', AppTheme.successColor, const Color(0x264CAF82)),
    ReservationStatus.cancelada  => (AppTheme.errorColor,   'CANCELADA',  AppTheme.errorColor,   const Color(0x26E05C5C)),
  };

  @override
  Widget build(BuildContext context) {
    final (accentColor, label, badgeColor, badgeBg) = _statusProps;

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
            Container(width: 3, color: accentColor),
            Expanded(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // ── Top: avatar + nombre + badge ──
                    Row(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        // Avatar
                        Container(
                          width: 40, height: 40,
                          decoration: BoxDecoration(
                            color: accentColor.withOpacity(0.15),
                            shape: BoxShape.circle,
                            border: Border.all(color: accentColor.withOpacity(0.4)),
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
                              Text('CC ${data.doc}',
                                  style: Theme.of(context)
                                      .textTheme
                                      .bodySmall
                                      ?.copyWith(fontSize: 11)),
                            ],
                          ),
                        ),
                        const SizedBox(width: 8),
                        // Badge estado
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                          decoration: BoxDecoration(
                            color: badgeBg,
                            borderRadius: BorderRadius.circular(20),
                            border: Border.all(color: badgeColor.withOpacity(0.4)),
                          ),
                          child: Text(
                            label,
                            style: TextStyle(
                              fontSize: 8,
                              fontWeight: FontWeight.w700,
                              color: badgeColor,
                              letterSpacing: 0.5,
                            ),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 12),

                    // ── Chips: habitación, entrada, salida ──
                    Wrap(
                      spacing: 8,
                      runSpacing: 6,
                      children: [
                        _Chip(icon: Icons.bed_rounded,          label: 'Hab. ${data.room}'),
                        _Chip(icon: Icons.login_rounded,        label: data.entrada),
                        _Chip(icon: Icons.logout_rounded,       label: data.salida),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _Chip extends StatelessWidget {
  final IconData icon;
  final String label;
  const _Chip({required this.icon, required this.label});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.04),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: AppTheme.borderColor),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 12, color: AppTheme.textMuted),
          const SizedBox(width: 5),
          Text(
            label,
            style: const TextStyle(
              fontSize: 11,
              color: AppTheme.textColor,
              fontWeight: FontWeight.w500,
            ),
          ),
        ],
      ),
    );
  }
}