import 'package:flutter/material.dart';
import 'package:santa_ana_dashboard/core/constants/app_strings.dart';

class FailureView extends StatelessWidget {
  final String? message;
  final VoidCallback? onRetry;

  const FailureView({super.key, this.message, this.onRetry});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Container(
                padding: const EdgeInsets.all(24),
                decoration: BoxDecoration(
                  color: Colors.red.withOpacity(0.1),
                  shape: BoxShape.circle,
                ),
                child: const Icon(
                  Icons.error_outline_rounded,
                  color: Colors.red,
                  size: 64,
                ),
              ),
              const SizedBox(height: 24),
              Text(
                message ?? AppStrings.failureGeneric,
                textAlign: TextAlign.center,
                style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                  color: Theme.of(context).colorScheme.error,
                  fontSize: 22,
                ),
              ),
              const SizedBox(height: 12),
              Text(
                AppStrings.failureData,
                textAlign: TextAlign.center,
                style: Theme.of(context).textTheme.bodyLarge,
              ),
              const SizedBox(height: 32),
              if (onRetry != null)
                ElevatedButton.icon(
                  onPressed: onRetry,
                  icon: const Icon(Icons.refresh_rounded),
                  label: const Text(AppStrings.retry),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.redAccent,
                    padding: const EdgeInsets.symmetric(
                      horizontal: 32,
                      vertical: 12,
                    ),
                  ),
                ),

              // --- CÓDIGO TEMPORAL PARA NAVEGACIÓN ---
              const SizedBox(height: 24),
              TextButton(
                onPressed: () => Navigator.pop(context),
                child: const Text('Volver (Temporal)'),
              ),
              // ----------------------------------------
            ],
          ),
        ),
      ),
    );
  }
}
