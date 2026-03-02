import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/bloc/bloc_bloc.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/bloc/bloc_state.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/views/failure_view.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/views/loading_view.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/views/login_view.dart';



class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<HomeBloc, HomeState>(
      builder: (context, state) {

        if (state is EstadoLoading) {
          return LoadingView();
        }

        if (state is EstadoLogin) {
          return Login();
        }

        if (state is EstadoFailure) {
          return FailureView();
        }

        return const Center(child: Text("Vista inicial"));
      },
    );
  }
}