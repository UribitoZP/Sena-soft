import 'dart:async';

import 'package:flutter/material.dart';
import 'package:santa_ana_dashboard/core/services/api_service.dart';
import 'package:santa_ana_dashboard/core/theme/app_theme.dart';

//  MODELO

enum RoomStatus { occupied, available, cleaning, maintenance }

class RoomModel {
  final int id;
  final String number, type;
  final double precio;
  final RoomStatus status;

  const RoomModel({
    required this.id,
    required this.number,
    required this.type,
    required this.precio,
    required this.status,
  });

  factory RoomModel.fromJson(Map<String, dynamic> j) {
    final estado = j['estado'] as String;
    final status = switch (estado) {
      'Ocupada'       => RoomStatus.occupied,
      'Limpieza'      => RoomStatus.cleaning,
      'Mantenimiento' => RoomStatus.maintenance,
      _               => RoomStatus.available,
    };
    return RoomModel(
      id:     (j['id'] as num).toInt(),
      number: j['numero'] as String,
      type:   j['tipo']   as String,
      precio: (j['precio'] as num).toDouble(),
      status: status,
    );
  }
}

//  ROOMS VIEW

class RoomsView extends StatefulWidget {
  const RoomsView({super.key});

  @override
  State<RoomsView> createState() => _RoomsViewState();
}

class _RoomsViewState extends State<RoomsView> {
  final _api = ApiService();
  late Future<List<RoomModel>> _roomsFuture;
  Timer? _timer;
  int _selectedFilter = 0;
  final _filters = ['Todas', 'Ocupadas', 'Disponibles', 'Limpieza', 'Mantenimiento'];

  @override
  void initState() {
    super.initState();
    _loadRooms();

    _timer = Timer.periodic(const Duration(seconds: 10), (_) {
      if (mounted) {
        setState(() => _loadRooms());
      }
    });
  }

  void _loadRooms() {
    _roomsFuture = _api
        .getHabitaciones()
        .then((list) => list.map(RoomModel.fromJson).toList());
  }

  List<RoomModel> _applyFilter(List<RoomModel> rooms) {
    if (_selectedFilter == 0) return rooms;
    final map = {
      1: RoomStatus.occupied,
      2: RoomStatus.available,
      3: RoomStatus.cleaning,
      4: RoomStatus.maintenance,
    };
    return rooms.where((r) => r.status == map[_selectedFilter]).toList();
  }
  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.bgColor,
      body: SafeArea(
        child: FutureBuilder<List<RoomModel>>(
          future: _roomsFuture,
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
            final allRooms = snap.data ?? [];
            final filtered = _applyFilter(allRooms);

            return RefreshIndicator(
              onRefresh: () async {
                setState(() => _loadRooms());
              },
              child: CustomScrollView(
                physics: const AlwaysScrollableScrollPhysics(
                    parent: BouncingScrollPhysics()),
                slivers: [
                  SliverToBoxAdapter(child: _buildHeader(allRooms.length)),
                  SliverToBoxAdapter(child: _buildFilterTabs()),
                  SliverToBoxAdapter(child: _buildStatsStrip(allRooms)),
                  SliverToBoxAdapter(child: _buildSectionTitle(filtered.length)),
                  SliverPadding(
                    padding: const EdgeInsets.symmetric(horizontal: 16),
                    sliver: SliverList(
                      delegate: SliverChildBuilderDelegate(
                        (context, i) => Padding(
                          padding: const EdgeInsets.only(bottom: 10),
                          child: _RoomCard(room: filtered[i]),
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

  Widget _buildHeader(int total) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(24, 18, 24, 0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          RichText(
            text: TextSpan(
              style: const TextStyle(
                fontFamily: 'Georgia',
                fontSize: 22,
                fontWeight: FontWeight.w700,
                color: AppTheme.textColor,
              ),
              children: [
                const TextSpan(text: 'Habitaciones '),
                TextSpan(
                  text: '· $total',
                  style: const TextStyle(color: AppTheme.goldColor),
                ),
              ],
            ),
          ),
          GestureDetector(
            onTap: () {},
            child: Container(
              width: 38, height: 38,
              decoration: BoxDecoration(
                color: AppTheme.cardColor,
                borderRadius: BorderRadius.circular(10),
                border: Border.all(color: AppTheme.borderColor),
              ),
              child: const Icon(Icons.search_rounded,
                  color: AppTheme.textMuted, size: 18),
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

  // ── Stats strip ──────────────────────────────────────────────

  Widget _buildStatsStrip(List<RoomModel> rooms) {
    final ocupadas     = rooms.where((r) => r.status == RoomStatus.occupied).length;
    final disponibles  = rooms.where((r) => r.status == RoomStatus.available).length;
    final limpieza     = rooms.where((r) => r.status == RoomStatus.cleaning).length;
    final mantenimiento = rooms.where((r) => r.status == RoomStatus.maintenance).length;
    final stats = [
      ('$ocupadas',      'Ocupadas',  AppTheme.goldColor),
      ('$disponibles',   'Libres',    AppTheme.successColor),
      ('$limpieza',      'Limpieza',  const Color(0xFFE0924A)),
      ('$mantenimiento', 'Mant.',     AppTheme.errorColor),
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
          Text('Todas las habitaciones',
              style: Theme.of(context).textTheme.titleMedium),
          Text('$count en total',
              style: Theme.of(context).textTheme.bodySmall),
        ],
      ),
    );
  }

}

//  ROOM CARD

class _RoomCard extends StatelessWidget {
  final RoomModel room;
  const _RoomCard({required this.room});

  Color get _statusColor => switch (room.status) {
    RoomStatus.occupied    => AppTheme.goldColor,
    RoomStatus.available   => AppTheme.successColor,
    RoomStatus.cleaning    => const Color(0xFFE0924A),
    RoomStatus.maintenance => AppTheme.errorColor,
  };

  (String, Color, Color) get _badgeProps => switch (room.status) {
    RoomStatus.occupied    => ('Ocupada',       AppTheme.goldColor,      const Color(0x26C9A84C)),
    RoomStatus.available   => ('Disponible',    AppTheme.successColor,   const Color(0x264CAF82)),
    RoomStatus.cleaning    => ('Limpieza',      const Color(0xFFE0924A), const Color(0x26E0924A)),
    RoomStatus.maintenance => ('Mantenimiento', AppTheme.errorColor,     const Color(0x26E05C5C)),
  };

  @override
  Widget build(BuildContext context) {
    final (label, color, bg) = _badgeProps;

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
            // Barra lateral de color estado
            Container(width: 3, color: _statusColor),

            // Ícono
            Container(
              width: 70,
              color: _statusColor.withOpacity(0.07),
              child: Icon(Icons.bed_rounded, color: _statusColor, size: 32),
            ),

            // Contenido
            Expanded(
              child: Padding(
                padding: const EdgeInsets.fromLTRB(14, 14, 14, 12),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    // Número + badge
                    Row(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                'Hab. ${room.number}',
                                style: const TextStyle(
                                  fontFamily: 'Georgia',
                                  fontSize: 20,
                                  fontWeight: FontWeight.w700,
                                  color: AppTheme.textColor,
                                  height: 1,
                                ),
                              ),
                              const SizedBox(height: 2),
                              Text(
                                room.type,
                                style: const TextStyle(
                                  fontSize: 11,
                                  color: AppTheme.textMuted,
                                ),
                              ),
                            ],
                          ),
                        ),
                        // Badge
                        Container(
                          padding: const EdgeInsets.symmetric(
                              horizontal: 8, vertical: 3),
                          decoration: BoxDecoration(
                            color: bg,
                            borderRadius: BorderRadius.circular(20),
                            border: Border.all(color: color.withOpacity(0.4)),
                          ),
                          child: Text(
                            label,
                            style: TextStyle(
                              fontSize: 9,
                              fontWeight: FontWeight.w700,
                              color: color,
                              letterSpacing: 0.4,
                            ),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 8),

                    // Precio
                    Text(
                      '\$${room.precio.toStringAsFixed(0)} / noche',
                      style: const TextStyle(
                        fontSize: 12,
                        color: AppTheme.goldColor,
                        fontWeight: FontWeight.w600,
                      ),
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