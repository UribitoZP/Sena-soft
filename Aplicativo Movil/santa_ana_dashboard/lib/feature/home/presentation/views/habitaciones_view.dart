import 'package:flutter/material.dart';
import 'package:santa_ana_dashboard/core/theme/app_theme.dart';

//  MODELO

enum RoomStatus { occupied, available, cleaning, maintenance, reserved }

class RoomModel {
  final String number, type, description, imageUrl;
  final RoomStatus status;

  const RoomModel({
    required this.number,
    required this.type,
    required this.description,
    required this.imageUrl,
    required this.status,
  });
}

// Datos de ejemplo — reemplazar con datos del BLoC
const _mockRooms = [
  RoomModel(
    number: '301', type: 'Suite Deluxe',
    description: 'Amplia suite con vista panorámica, jacuzzi privado y sala de estar de lujo.',
    imageUrl: 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=400&q=80',
    status: RoomStatus.occupied,
  ),
  RoomModel(
    number: '302', type: 'Habitación Estándar',
    description: 'Habitación cómoda y funcional con todas las amenidades para negocios o descanso.',
    imageUrl: 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=400&q=80',
    status: RoomStatus.available,
  ),
  RoomModel(
    number: '304', type: 'Junior Suite',
    description: 'Suite con área de trabajo, mini bar y balcón privado con vista al jardín.',
    imageUrl: 'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=400&q=80',
    status: RoomStatus.cleaning,
  ),
  RoomModel(
    number: '305', type: 'Habitación Estándar',
    description: 'Habitación cálida con cama queen, perfecta para estadías cortas o de negocios.',
    imageUrl: 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=400&q=80',
    status: RoomStatus.reserved,
  ),
  RoomModel(
    number: '307', type: 'Habitación Doble',
    description: 'Habitación con dos camas individuales, ideal para viajeros o compañeros de trabajo.',
    imageUrl: 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=400&q=80',
    status: RoomStatus.maintenance,
  ),
];

//  ROOMS VIEW

class RoomsView extends StatefulWidget {
  const RoomsView({super.key});

  @override
  State<RoomsView> createState() => _RoomsViewState();
}

class _RoomsViewState extends State<RoomsView> {
  int _selectedFilter = 0;
  final _filters = ['Todas', 'Ocupadas', 'Disponibles', 'Limpieza', 'Mantenimiento'];

  List<RoomModel> get _filtered {
    if (_selectedFilter == 0) return _mockRooms;
    final map = {
      1: RoomStatus.occupied,
      2: RoomStatus.available,
      3: RoomStatus.cleaning,
      4: RoomStatus.maintenance,
    };
    return _mockRooms
        .where((r) => r.status == map[_selectedFilter])
        .toList();
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
                  SliverToBoxAdapter(child: _buildHeader()),
                  SliverToBoxAdapter(child: _buildFilterTabs()),
                  SliverToBoxAdapter(child: _buildStatsStrip()),
                  SliverToBoxAdapter(child: _buildSectionTitle()),
                  SliverPadding(
                    padding: const EdgeInsets.symmetric(horizontal: 16),
                    sliver: SliverList(
                      delegate: SliverChildBuilderDelegate(
                        (context, i) => Padding(
                          padding: const EdgeInsets.only(bottom: 10),
                          child: _RoomCard(room: _filtered[i]),
                        ),
                        childCount: _filtered.length,
                      ),
                    ),
                  ),
                  const SliverToBoxAdapter(child: SizedBox(height: 16)),
                ],
              ),
            ),

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
                TextSpan(text: 'Habitaciones '),
                TextSpan(
                  text: '· 120',
                  style: TextStyle(color: AppTheme.goldColor),
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

  Widget _buildStatsStrip() {
    final stats = [
      ('104', 'Ocupadas',  AppTheme.goldColor),
      ('13',  'Libres',    AppTheme.successColor),
      ('4',   'Limpieza',  const Color(0xFFE0924A)),
      ('3',   'Mant.',     AppTheme.errorColor),
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
          Text('Todas las habitaciones',
              style: Theme.of(context).textTheme.titleMedium),
          Text('${_filtered.length} en total',
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
    RoomStatus.reserved    => AppTheme.infoColor,
  };

  (String, Color, Color) get _badgeProps => switch (room.status) {
    RoomStatus.occupied    => ('Ocupada',      AppTheme.goldColor,            const Color(0x26C9A84C)),
    RoomStatus.available   => ('Disponible',   AppTheme.successColor,         const Color(0x264CAF82)),
    RoomStatus.cleaning    => ('Limpieza',     const Color(0xFFE0924A),       const Color(0x26E0924A)),
    RoomStatus.maintenance => ('Mantenimiento',AppTheme.errorColor,           const Color(0x26E05C5C)),
    RoomStatus.reserved    => ('Reservada',    AppTheme.infoColor,            const Color(0x265B8DEE)),
  };

  @override
  Widget build(BuildContext context) {
    final (label, color, bg) = _badgeProps;

    return Container(
      height: 100,
      decoration: BoxDecoration(
        color: AppTheme.cardColor,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: AppTheme.borderColor),
      ),
      clipBehavior: Clip.hardEdge,
      child: Row(
        children: [
          // ── Imagen lateral ──
          SizedBox(
            width: 100,
            child: Stack(
              fit: StackFit.expand,
              children: [
                Image.network(
                  room.imageUrl,
                  fit: BoxFit.cover,
                  color: Colors.black.withOpacity(0.3),
                  colorBlendMode: BlendMode.darken,
                  errorBuilder: (_, __, ___) => Container(
                    color: AppTheme.borderColor,
                    child: const Icon(Icons.bed_rounded,
                        color: AppTheme.textMuted, size: 32),
                  ),
                ),
                // Degradado derecha
                Positioned.fill(
                  child: DecoratedBox(
                    decoration: BoxDecoration(
                      gradient: LinearGradient(
                        begin: Alignment.centerLeft,
                        end: Alignment.centerRight,
                        colors: [
                          Colors.transparent,
                          AppTheme.cardColor.withOpacity(0.85),
                        ],
                        stops: const [0.5, 1.0],
                      ),
                    ),
                  ),
                ),
                // Barra de color estado
                Positioned(
                  left: 0, top: 0, bottom: 0,
                  child: Container(width: 3, color: _statusColor),
                ),
              ],
            ),
          ),

          // ── Contenido ──
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
                              room.number,
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
                          border: Border.all(
                              color: color.withOpacity(0.4)),
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

                  // Descripción
                  Text(
                    room.description,
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                    style: const TextStyle(
                      fontSize: 11,
                      color: AppTheme.textMuted,
                      height: 1.4,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}