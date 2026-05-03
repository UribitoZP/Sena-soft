import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:santa_ana_dashboard/core/theme/app_theme.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/view_seconds/bloc/bloc_second_bloc.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/view_seconds/bloc/bloc_second_event.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/view_seconds/bloc/bloc_second_state.dart';

class ReservasDelDiaView extends StatelessWidget {
  const ReservasDelDiaView({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (_) => BlocSecond()..add(LoadReservasDelDia()),
      child: Scaffold(
        backgroundColor: AppTheme.bgColor,
        body: SafeArea(
          child: BlocBuilder<BlocSecond, BlocSecondState>(
            builder: (context, state) {
              return Column(
                children: [
                  _ReservasHeader(
                    onBack: () => Navigator.of(context).pop(),
                  ),
                  const SizedBox(height: 12),

                  // 🔹 LOADING
                  if (state is ReservasLoading) ...[
                    const Expanded(child: _LoadingView()),
                  ]

                  // 🔹 ERROR
                  else if (state is ReservasFailure) ...[
                    Expanded(
                      child: _ErrorView(message: state.message),
                    ),
                  ]

                  // 🔹 DATA
                  else if (state is ReservasLoaded) ...[
                    Expanded(
                      child: ListView.builder(
                        padding: const EdgeInsets.symmetric(horizontal: 16),
                        itemCount: state.reservas.length,
                        itemBuilder: (context, index) {
                          final reserva = state.reservas[index];

                          return Container(
                            margin: const EdgeInsets.only(bottom: 12),
                            padding: const EdgeInsets.all(16),
                            decoration: BoxDecoration(
                              color: AppTheme.cardColor,
                              borderRadius: BorderRadius.circular(12),
                              border: Border.all(
                                  color: AppTheme.borderColor),
                            ),
                            child: Row(
                              children: [
                                const Icon(Icons.person,
                                    color: AppTheme.goldColor),
                                const SizedBox(width: 12),
                                Expanded(
                                  child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        reserva['cliente']?.toString() ?? 'Sin nombre',
                                        style: Theme.of(context).textTheme.bodyLarge,
                                      ),
                                      const SizedBox(height: 4),
                                      Text(
                                        'Habitación: ${reserva['habitacion']?.toString() ?? '---'}',
                                        style: Theme.of(context).textTheme.bodySmall,
                                      ),
                                    ],
                                  ),
                                ),
                              ],
                            ),
                          );
                        },
                      ),
                    ),
                  ]

                  // 🔹 DEFAULT (por si acaso)
                  else ...[
                    const Expanded(
                      child: Center(child: Text("Sin datos")),
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

  const _ReservasHeader({required this.onBack});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 18, 24, 0),
      child: Row(
        children: [
          GestureDetector(
            onTap: onBack,
            child: Container(
              width: 38,
              height: 38,
              decoration: BoxDecoration(
                color: AppTheme.cardColor,
                borderRadius: BorderRadius.circular(10),
                border: Border.all(color: AppTheme.borderColor),
              ),
              child: const Icon(
                Icons.arrow_back_ios_new_rounded,
                color: AppTheme.textMuted,
                size: 16,
              ),
            ),
          ),
          const SizedBox(width: 14),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'RESERVAS',
                  style: Theme.of(context).textTheme.labelLarge?.copyWith(
                        color: AppTheme.textMuted,
                        letterSpacing: 1.2,
                      ),
                ),
                const SizedBox(height: 2),
                const Text(
                  'Del Día',
                  style: TextStyle(
                    fontFamily: 'Georgia',
                    fontSize: 20,
                    fontWeight: FontWeight.w700,
                    color: AppTheme.goldColor,
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
      child: CircularProgressIndicator(
        color: AppTheme.goldColor,
        strokeWidth: 2,
      ),
    );
  }
}

class _ErrorView extends StatelessWidget {
  final String message;

  const _ErrorView({required this.message});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.error_outline_rounded,
              color: AppTheme.errorColor, size: 40),
          const SizedBox(height: 12),
          Text(
            message,
            style: Theme.of(context)
                .textTheme
                .bodyMedium
                ?.copyWith(color: AppTheme.errorColor),
          ),
          const SizedBox(height: 16),
          TextButton.icon(
            onPressed: () => context
                .read<BlocSecond>()
                .add(LoadReservasDelDia()),
            icon: const Icon(Icons.refresh_rounded, size: 16),
            label: const Text('Reintentar'),
          ),
        ],
      ),
    );
  }
}