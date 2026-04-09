import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:santa_ana_dashboard/core/services/api_service.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/bloc/bloc_bloc.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/bloc/bloc_event.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/bloc/bloc_state.dart';
import 'loading_view.dart';
import 'package:santa_ana_dashboard/core/constants/app_strings.dart';

class InitialView extends StatefulWidget {
  const InitialView({super.key});

  @override
  State<InitialView> createState() => _InitialViewState();
}

class _InitialViewState extends State<InitialView> {
  final _usuarioCtrl    = TextEditingController();
  final _passwordCtrl   = TextEditingController();
  final _ipCtrl         = TextEditingController(text: ApiService.serverIp);
  bool _obscurePassword = true;
  bool _showIp          = false;

  @override
  void dispose() {
    _usuarioCtrl.dispose();
    _passwordCtrl.dispose();
    _ipCtrl.dispose();
    super.dispose();
  }

  void _login() {
    // Actualizar IP antes de intentar conectar
    final ip = _ipCtrl.text.trim();
    if (ip.isNotEmpty) ApiService.serverIp = ip;

    context.read<AppBloc>().add(
      LoginRequested(
        username: _usuarioCtrl.text.trim(),
        password: _passwordCtrl.text,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<AppBloc, AppState>(
      builder: (context, state) {

        if (state is AppLoading || state is AppLoginSuccess) {
          return const LoadingView();
        }

        return Scaffold(
          body: SafeArea(
            child: SingleChildScrollView(
              padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 32),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [

                  // LOGO
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

                  const SizedBox(height: 40),

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

                  // Mensaje de error
                  if (state is AppFailure) ...[
                    const SizedBox(height: 20),
                    Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 16, vertical: 12),
                      decoration: BoxDecoration(
                        color: const Color(0x26E05C5C),
                        borderRadius: BorderRadius.circular(12),
                        border: Border.all(color: const Color(0x4DE05C5C)),
                      ),
                      child: Row(
                        children: [
                          const Icon(Icons.error_outline,
                              color: Color(0xFFE05C5C), size: 18),
                          const SizedBox(width: 10),
                          Expanded(
                            child: Text(
                              state.message,
                              style: const TextStyle(
                                color: Color(0xFFE05C5C),
                                fontSize: 13,
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],

                  const SizedBox(height: 28),

                  // INPUT USUARIO
                  TextField(
                    controller: _usuarioCtrl,
                    keyboardType: TextInputType.text,
                    textInputAction: TextInputAction.next,
                    decoration: InputDecoration(
                      labelText: 'Usuario',
                      hintText: 'admin',
                      prefixIcon: const Icon(Icons.person_outline),
                      filled: true,
                      fillColor: Colors.grey.withOpacity(0.08),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(14),
                        borderSide: BorderSide.none,
                      ),
                    ),
                  ),

                  const SizedBox(height: 16),

                  // INPUT CONTRASEÑA
                  TextField(
                    controller: _passwordCtrl,
                    obscureText: _obscurePassword,
                    textInputAction: TextInputAction.done,
                    onSubmitted: (_) => _login(),
                    decoration: InputDecoration(
                      labelText: 'Contraseña',
                      prefixIcon: const Icon(Icons.lock_outline),
                      filled: true,
                      fillColor: Colors.grey.withOpacity(0.08),
                      suffixIcon: IconButton(
                        icon: Icon(_obscurePassword
                            ? Icons.visibility_off
                            : Icons.visibility),
                        onPressed: () => setState(
                            () => _obscurePassword = !_obscurePassword),
                      ),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(14),
                        borderSide: BorderSide.none,
                      ),
                    ),
                  ),

                  const SizedBox(height: 12),

                  // IP del servidor (expandible)
                  GestureDetector(
                    onTap: () => setState(() => _showIp = !_showIp),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          _showIp
                              ? Icons.settings
                              : Icons.settings_outlined,
                          size: 14,
                          color: Colors.grey,
                        ),
                        const SizedBox(width: 6),
                        Text(
                          'IP del servidor (${ApiService.serverIp})',
                          style: const TextStyle(
                              color: Colors.grey, fontSize: 12),
                        ),
                        Icon(
                          _showIp
                              ? Icons.expand_less
                              : Icons.expand_more,
                          size: 14,
                          color: Colors.grey,
                        ),
                      ],
                    ),
                  ),

                  if (_showIp) ...[
                    const SizedBox(height: 10),
                    TextField(
                      controller: _ipCtrl,
                      keyboardType: TextInputType.url,
                      textInputAction: TextInputAction.done,
                      decoration: InputDecoration(
                        labelText: 'IP del servidor',
                        hintText: '192.168.1.x',
                        helperText:
                            'Emulador Android: 10.0.2.2 · Dispositivo físico: IP del PC',
                        prefixIcon: const Icon(Icons.dns_outlined),
                        filled: true,
                        fillColor: Colors.grey.withOpacity(0.08),
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(14),
                          borderSide: BorderSide.none,
                        ),
                      ),
                      onChanged: (v) {
                        if (v.trim().isNotEmpty) ApiService.serverIp = v.trim();
                      },
                    ),
                  ],

                  const SizedBox(height: 32),

                  // BOTÓN LOGIN
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton(
                      onPressed: _login,
                      child: const Text(AppStrings.enterApp),
                    ),
                  ),
                ],
              ),
            ),
          ),
        );
      },
    );
  }
}
