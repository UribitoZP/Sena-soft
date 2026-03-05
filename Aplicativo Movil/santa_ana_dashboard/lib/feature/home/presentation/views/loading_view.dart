import 'package:flutter/material.dart';
import 'package:santa_ana_dashboard/core/constants/app_strings.dart';
import 'package:santa_ana_dashboard/core/widgets/shimmer_placeholder.dart';

class LoadingView extends StatelessWidget {
  const LoadingView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 32),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Skeleton Header
              const ShimmerPlaceholder(width: 200, height: 32, borderRadius: 8),
              const SizedBox(height: 12),
              const ShimmerPlaceholder(
                width: double.infinity,
                height: 16,
                borderRadius: 4,
              ),
              const SizedBox(height: 8),
              const ShimmerPlaceholder(width: 250, height: 16, borderRadius: 4),

              const SizedBox(height: 48),

              // Skeleton lista
              Expanded(
                child: ListView.builder(
                  itemCount: 6,
                  itemBuilder: (context, index) => _buildSkeletonItem(),
                ),
              ),

              // Texto de carga
              Center(
                child: Padding(
                  padding: const EdgeInsets.only(top: 16),
                  child: Text(
                    AppStrings.loadingMessage,
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: Colors.grey,
                      letterSpacing: 1.1,
                    ),
                  ),
                ),
              ),

              // --- CÓDIGO TEMPORAL PARA NAVEGACIÓN ---
              const SizedBox(height: 16),
              const Divider(),
              Center(
                child: TextButton(
                  onPressed: () => Navigator.pop(context),
                  child: const Text('Volver (Temporal)'),
                ),
              ),
              // ----------------------------------------
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildSkeletonItem() {
    return const Padding(
      padding: EdgeInsets.only(bottom: 24),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          ShimmerPlaceholder(width: 56, height: 56, borderRadius: 12),
          SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                ShimmerPlaceholder(width: 150, height: 14, borderRadius: 4),
                SizedBox(height: 8),
                ShimmerPlaceholder(width: 100, height: 10, borderRadius: 4),
              ],
            ),
          ),
          ShimmerPlaceholder(width: 40, height: 14, borderRadius: 4),
        ],
      ),
    );
  }
}
