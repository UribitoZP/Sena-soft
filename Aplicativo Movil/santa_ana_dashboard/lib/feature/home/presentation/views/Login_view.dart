import 'package:flutter/material.dart';
import 'package:santa_ana_dashboard/core/constants/app_strings.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/views/dashboard_view.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/views/failure_view.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/views/loading_view.dart';

class InitialView extends StatefulWidget {
  const InitialView({super.key});

  @override
  State<InitialView> createState() => _InitialViewState();
}

class _InitialViewState extends State<InitialView>
    with SingleTickerProviderStateMixin {

  final TextEditingController emailController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();

  bool obscurePassword = true;

  late AnimationController _controller;

  @override
  void initState() {
    super.initState();

    _controller = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 2),
    );

    _controller.forward(); // inicia la animación
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 32),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [

                /// LOGO CON ANIMACIÓN
                Hero(
                  tag: 'app_logo',
                  child: RotationTransition(
                    turns: Tween(begin: 0.0, end: 1.0).animate(
                      CurvedAnimation(
                        parent: _controller,
                        curve: Curves.easeOut,
                      ),
                    ),
                    child: ClipRRect(
                      borderRadius: BorderRadius.circular(90),
                      child: Image.asset(
                        'assets/images/app_logo.png',
                        height: 150,
                      ),
                    ),
                  ),
                ),

                const SizedBox(height: 40),

                /// TEXTO BIENVENIDA
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

                const SizedBox(height: 40),

                /// INPUT CORREO
                TextField(
                  controller: emailController,
                  keyboardType: TextInputType.emailAddress,
                  decoration: InputDecoration(
                    labelText: 'Correo electrónico',
                    hintText: 'ejemplo@email.com',
                    prefixIcon: const Icon(Icons.email_outlined),
                    filled: true,
                    fillColor: Colors.grey.withOpacity(0.08),
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(14),
                      borderSide: BorderSide.none,
                    ),
                  ),
                ),

                const SizedBox(height: 20),

                /// INPUT CONTRASEÑA
                TextField(
                  controller: passwordController,
                  obscureText: obscurePassword,
                  decoration: InputDecoration(
                    labelText: 'Contraseña',
                    prefixIcon: const Icon(Icons.lock_outline),
                    filled: true,
                    fillColor: Colors.grey.withOpacity(0.08),
                    suffixIcon: IconButton(
                      icon: Icon(
                        obscurePassword
                            ? Icons.visibility_off
                            : Icons.visibility,
                      ),
                      onPressed: () {
                        setState(() {
                          obscurePassword = !obscurePassword;
                        });
                      },
                    ),
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(14),
                      borderSide: BorderSide.none,
                    ),
                  ),
                ),

                const SizedBox(height: 40),

                /// BOTÓN ENTRAR
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: () {

                      String email = emailController.text;
                      String password = passwordController.text;

                      print(email);
                      print(password);

                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => const LoadingView(),
                        ),
                      );
                    },
                    child: const Text(AppStrings.enterApp),
                  ),
                ),

                /// --- TEMPORAL ---
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
                    TextButton(
                      onPressed: () => Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => const DashboardView(),
                        ),
                      ),
                      child: const Text('Ver dashboard'),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}