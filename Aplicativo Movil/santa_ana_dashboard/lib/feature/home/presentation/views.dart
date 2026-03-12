import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/views/Login_view.dart';
import 'bloc/bloc_bloc.dart';
import 'bloc/bloc_state.dart';
import 'views/dashboard_view.dart';
import 'views/failure_view.dart';

class Views extends StatelessWidget {
  const Views({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocListener<AppBloc, AppState>(
      listener: (context, state) {
        if (state is AppSuccess) {
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(builder: (_) => const DashboardView()),
          );
        } else if (state is AppFailure) {
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(
              builder: (_) => FailureView(message: state.message),
            ),
          );
        }
      },
      child: const InitialView(), // InitialView ahora maneja su propio loading overlay
    );
  }
}