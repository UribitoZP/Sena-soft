import 'package:flutter/material.dart';
import 'package:santa_ana_dashboard/core/constants/app_strings.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/views/failure_view.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/views/loading_view.dart';

class InitialView extends StatelessWidget {
  const InitialView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 32.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                // Logo de la Aplicación
                Hero(
                  tag: 'app_logo',
                  child: ClipRRect(
                    borderRadius: BorderRadius.circular(90),
                    child: Image.asset(
                      'assets/images/app_logo.png',
                      height: 150,
                    ),
                  ),
                ),
                const SizedBox(height: 48),
                Text(
                  AppStrings.initialWelcome,
                  style: Theme.of(context).textTheme.headlineMedium,
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 16),
                Text(
                  AppStrings.initialDescription,
                  style: Theme.of(context).textTheme.bodyLarge,
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 48),
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: () {
                      // Navegar al Login (funcionalidad real esperada)
                      Navigator.push(
                        context,
                        MaterialPageRoute(builder: (context) => const LoadingView()),
                      );
                    },
                    child: const Text(AppStrings.enterApp),
                  ),
                ),

                // --- CÓDIGO TEMPORAL PARA NAVEGACIÓN ---
                const SizedBox(height: 40),
                const Divider(),
                const Text(
                  'Prueba de Estados (Temporal)',
                  style: TextStyle(fontSize: 12, color: Colors.grey),
                ),
                Wrap(
                  spacing: 10,
                  children: [
                    TextButton(
                      onPressed: () => Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => const LoadingView(),
                        ),
                      ),
                      child: const Text('Ver Loading'),
                    ),
                    TextButton(
                      onPressed: () => Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => const FailureView(),
                        ),
                      ),
                      child: const Text('Ver Failure'),
                    ),
                  ],
                ),
                // ----------------------------------------
              ],
            ),
          ),
        ),
      ),
    );
  }
}
