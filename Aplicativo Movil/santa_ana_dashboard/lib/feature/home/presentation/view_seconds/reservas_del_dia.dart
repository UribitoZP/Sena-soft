import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:santa_ana_dashboard/core/theme/app_theme.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/view_seconds/bloc/bloc_second_bloc.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/view_seconds/bloc/bloc_second_event.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/view_seconds/bloc/bloc_second_state.dart';

class ReservasDelDiaView extends StatefulWidget {
  const ReservasDelDiaView({super.key});

  @override
  State<ReservasDelDiaView> createState() =>
      _ReservasDelDiaViewState();
}

class _ReservasDelDiaViewState
    extends State<ReservasDelDiaView> {
  final TextEditingController _searchController =
      TextEditingController();

  String _search = '';

  int _selectedFilter = 0;

  final List<String> _filters = [
    'Todas',
    'Activas',
    'Completadas',
    'Canceladas',
  ];

  @override
  void initState() {
    super.initState();

    _searchController.addListener(() {
      setState(() {
        _search =
            _searchController.text.toLowerCase();
      });
    });
  }

  List<dynamic> _applyFilters(
      List<dynamic> reservas) {
    var filtered = reservas;

    // 🔹 FILTRO POR ESTADO
    if (_selectedFilter != 0) {
      final estados = {
        1: 'activa',
        2: 'completada',
        3: 'cancelada',
      };

      filtered = filtered.where((r) {
        final estado =
            r['estado']
                    ?.toString()
                    .toLowerCase() ??
                '';

        return estado ==
            estados[_selectedFilter];
      }).toList();
    }

    // 🔹 FILTRO DE BÚSQUEDA
    if (_search.isNotEmpty) {
      filtered = filtered.where((r) {
        final cliente =
            r['cliente']
                    ?.toString()
                    .toLowerCase() ??
                '';

        final habitacion =
            r['habitacion']
                    ?.toString()
                    .toLowerCase() ??
                '';

        return cliente.contains(_search) ||
            habitacion.contains(_search);
      }).toList();
    }

    return filtered;
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (_) =>
          BlocSecond()..add(LoadReservasDelDia()),
      child: Scaffold(
        backgroundColor: AppTheme.bgColor,
        body: SafeArea(
          child: BlocBuilder<
              BlocSecond,
              BlocSecondState>(
            builder: (context, state) {
              return Column(
                children: [
                  _ReservasHeader(
                    onBack: () =>
                        Navigator.of(context).pop(),
                  ),

                  const SizedBox(height: 14),

                  // 🔹 SEARCH BAR
                  Padding(
                    padding:
                        const EdgeInsets.symmetric(
                            horizontal: 16),
                    child: Container(
                      padding:
                          const EdgeInsets.symmetric(
                        horizontal: 14,
                        vertical: 12,
                      ),
                      decoration: BoxDecoration(
                        color: AppTheme.cardColor,
                        borderRadius:
                            BorderRadius.circular(12),
                        border: Border.all(
                          color:
                              AppTheme.borderColor,
                        ),
                      ),
                      child: Row(
                        children: [
                          const Icon(
                            Icons.search_rounded,
                            color:
                                AppTheme.textMuted,
                            size: 18,
                          ),
                          const SizedBox(width: 10),
                          Expanded(
                            child: TextField(
                              controller:
                                  _searchController,
                              style:
                                  const TextStyle(
                                color: AppTheme
                                    .textColor,
                                fontSize: 13,
                              ),
                              decoration:
                                  const InputDecoration(
                                hintText:
                                    'Buscar cliente o habitación...',
                                hintStyle:
                                    TextStyle(
                                  color: AppTheme
                                      .textMuted,
                                  fontSize: 13,
                                ),
                                border:
                                    InputBorder.none,
                                isDense: true,
                                contentPadding:
                                    EdgeInsets.zero,
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),

                  const SizedBox(height: 14),

                  // 🔹 FILTER BUTTONS
                  SizedBox(
                    height: 46,
                    child: ListView.separated(
                      scrollDirection:
                          Axis.horizontal,
                      padding:
                          const EdgeInsets.symmetric(
                              horizontal: 16),
                      itemCount:
                          _filters.length,
                      separatorBuilder:
                          (_, __) =>
                              const SizedBox(
                                  width: 8),
                      itemBuilder:
                          (context, index) {
                        final isSelected =
                            _selectedFilter ==
                                index;

                        return GestureDetector(
                          onTap: () {
                            setState(() {
                              _selectedFilter =
                                  index;
                            });
                          },
                          child:
                              AnimatedContainer(
                            duration:
                                const Duration(
                                    milliseconds:
                                        200),
                            padding:
                                const EdgeInsets
                                    .symmetric(
                              horizontal: 16,
                              vertical: 10,
                            ),
                            decoration:
                                BoxDecoration(
                              color: isSelected
                                  ? AppTheme
                                      .goldDim
                                  : Colors
                                      .transparent,
                              borderRadius:
                                  BorderRadius
                                      .circular(
                                          20),
                              border: Border.all(
                                color: isSelected
                                    ? AppTheme
                                        .goldColor
                                    : AppTheme
                                        .borderColor,
                              ),
                            ),
                            child: Text(
                              _filters[index],
                              style:
                                  TextStyle(
                                fontSize: 12,
                                fontWeight:
                                    FontWeight
                                        .w600,
                                letterSpacing:
                                    0.3,
                                color: isSelected
                                    ? AppTheme
                                        .goldColor
                                    : AppTheme
                                        .textMuted,
                              ),
                            ),
                          ),
                        );
                      },
                    ),
                  ),

                  const SizedBox(height: 14),

                  // 🔹 LOADING
                  if (state
                      is ReservasLoading) ...[
                    const Expanded(
                        child:
                            _LoadingView()),
                  ]

                  // 🔹 ERROR
                  else if (state
                      is ReservasFailure) ...[
                    Expanded(
                      child: _ErrorView(
                        message:
                            state.message,
                      ),
                    ),
                  ]

                  // 🔹 DATA
                  else if (state
                      is ReservasLoaded) ...[
                    Builder(
                      builder: (_) {
                        final reservasFiltradas =
                            _applyFilters(
                                state.reservas);

                        if (reservasFiltradas
                            .isEmpty) {
                          return const Expanded(
                            child: Center(
                              child: Text(
                                'No se encontraron reservas',
                                style:
                                    TextStyle(
                                  color: AppTheme
                                      .textMuted,
                                ),
                              ),
                            ),
                          );
                        }

                        return Expanded(
                          child:
                              ListView.builder(
                            padding:
                                const EdgeInsets
                                    .symmetric(
                              horizontal: 16,
                            ),
                            itemCount:
                                reservasFiltradas
                                    .length,
                            itemBuilder:
                                (context,
                                    index) {
                              final reserva =
                                  reservasFiltradas[
                                      index];

                              return Container(
                                margin:
                                    const EdgeInsets
                                        .only(
                                  bottom: 12,
                                ),
                                padding:
                                    const EdgeInsets
                                        .all(16),
                                decoration:
                                    BoxDecoration(
                                  color: AppTheme
                                      .cardColor,
                                  borderRadius:
                                      BorderRadius
                                          .circular(
                                              12),
                                  border:
                                      Border.all(
                                    color: AppTheme
                                        .borderColor,
                                  ),
                                ),
                                child: Row(
                                  children: [
                                    // 🔹 ICONO
                                    Container(
                                      width: 42,
                                      height: 42,
                                      decoration:
                                          BoxDecoration(
                                        color:
                                            AppTheme
                                                .goldDim,
                                        shape:
                                            BoxShape
                                                .circle,
                                      ),
                                      child:
                                          const Icon(
                                        Icons.person,
                                        color: AppTheme
                                            .goldColor,
                                      ),
                                    ),

                                    const SizedBox(
                                        width:
                                            12),

                                    // 🔹 INFO
                                    Expanded(
                                      child:
                                          Column(
                                        crossAxisAlignment:
                                            CrossAxisAlignment
                                                .start,
                                        children: [
                                          Text(
                                            reserva['cliente']?.toString() ??
                                                'Sin nombre',
                                            style: Theme.of(
                                                    context)
                                                .textTheme
                                                .bodyLarge,
                                          ),

                                          const SizedBox(
                                              height:
                                                  4),

                                          Text(
                                            'Habitación: ${reserva['habitacion']?.toString() ?? '---'}',
                                            style: Theme.of(
                                                    context)
                                                .textTheme
                                                .bodySmall,
                                          ),
                                        ],
                                      ),
                                    ),

                                    // 🔹 BADGE
                                    Container(
                                      padding:
                                          const EdgeInsets
                                              .symmetric(
                                        horizontal:
                                            10,
                                        vertical: 5,
                                      ),
                                      decoration:
                                          BoxDecoration(
                                        color:
                                            AppTheme
                                                .goldDim,
                                        borderRadius:
                                            BorderRadius
                                                .circular(
                                                    20),
                                        border:
                                            Border
                                                .all(
                                          color: AppTheme
                                              .goldColor
                                              .withOpacity(
                                                  0.4),
                                        ),
                                      ),
                                      child: Text(
                                        reserva['estado']
                                                ?.toString()
                                                .toUpperCase() ??
                                            'RESERVA',
                                        style:
                                            const TextStyle(
                                          color:
                                              AppTheme
                                                  .goldColor,
                                          fontSize:
                                              10,
                                          fontWeight:
                                              FontWeight
                                                  .w700,
                                          letterSpacing:
                                              0.5,
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              );
                            },
                          ),
                        );
                      },
                    ),
                  ]

                  // 🔹 DEFAULT
                  else ...[
                    const Expanded(
                      child: Center(
                        child:
                            Text("Sin datos"),
                      ),
                    ),
                  ]
                ],
              );
            },
          ),
        ),
      ),
    );
  }
}

class _ReservasHeader extends StatelessWidget {
  final VoidCallback onBack;

  const _ReservasHeader({
    required this.onBack,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding:
          const EdgeInsets.fromLTRB(
              16, 18, 24, 0),
      child: Row(
        children: [
          GestureDetector(
            onTap: onBack,
            child: Container(
              width: 38,
              height: 38,
              decoration: BoxDecoration(
                color: AppTheme.cardColor,
                borderRadius:
                    BorderRadius.circular(10),
                border: Border.all(
                  color:
                      AppTheme.borderColor,
                ),
              ),
              child: const Icon(
                Icons
                    .arrow_back_ios_new_rounded,
                color:
                    AppTheme.textMuted,
                size: 16,
              ),
            ),
          ),
          const SizedBox(width: 14),
          Expanded(
            child: Column(
              crossAxisAlignment:
                  CrossAxisAlignment.start,
              children: [
                Text(
                  'RESERVAS',
                  style: Theme.of(context)
                      .textTheme
                      .labelLarge
                      ?.copyWith(
                        color:
                            AppTheme.textMuted,
                        letterSpacing: 1.2,
                      ),
                ),
                const SizedBox(height: 2),
                const Text(
                  'Del Día',
                  style: TextStyle(
                    fontFamily: 'Georgia',
                    fontSize: 20,
                    fontWeight:
                        FontWeight.w700,
                    color:
                        AppTheme.goldColor,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _LoadingView extends StatelessWidget {
  const _LoadingView();

  @override
  Widget build(BuildContext context) {
    return const Center(
      child:
          CircularProgressIndicator(
        color: AppTheme.goldColor,
        strokeWidth: 2,
      ),
    );
  }
}

class _ErrorView extends StatelessWidget {
  final String message;

  const _ErrorView({
    required this.message,
  });

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment:
            MainAxisAlignment.center,
        children: [
          const Icon(
            Icons.error_outline_rounded,
            color:
                AppTheme.errorColor,
            size: 40,
          ),
          const SizedBox(height: 12),
          Text(
            message,
            style: Theme.of(context)
                .textTheme
                .bodyMedium
                ?.copyWith(
                  color:
                      AppTheme.errorColor,
                ),
          ),
          const SizedBox(height: 16),
          TextButton.icon(
            onPressed: () => context
                .read<BlocSecond>()
                .add(
                    LoadReservasDelDia()),
            icon: const Icon(
              Icons.refresh_rounded,
              size: 16,
            ),
            label:
                const Text('Reintentar'),
          ),
        ],
      ),
    );
  }
}