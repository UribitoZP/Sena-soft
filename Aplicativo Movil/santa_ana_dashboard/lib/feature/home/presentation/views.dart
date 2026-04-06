import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/views/Login_view.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/views/dashboard_view.dart';
import 'bloc/bloc_bloc.dart';
import 'bloc/bloc_state.dart';
import 'views/home_view.dart';
import 'views/failure_view.dart';

class Views extends StatelessWidget {
  const Views({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocListener<AppBloc, AppState>(
      listener: (context, state) {
        if (state is AppLoginSuccess) {
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(builder: (_) => const DashboardView()),
          );
        }
        // AppFailure se muestra directamente en InitialView
      },
      child: const InitialView(),
    );
  }
}