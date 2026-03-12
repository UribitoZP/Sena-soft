import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/bloc/bloc_event.dart';
import 'bloc_state.dart';

class AppBloc extends Bloc<AppEvent, AppState> {
  AppBloc() : super(AppInitial()) {
    // Login
    on<LoginRequested>((event, emit) async {
      emit(AppLoading());
      try {
        // Aquí va la lógica de login, puede ser API o mock
        await Future.delayed(Duration(seconds: 2)); // simulando login
        final userData = {'username': event.username}; // ejemplo
        emit(AppSuccess(data: userData));
      } catch (e) {
        emit(AppFailure(message: 'Login failed'));
      }
    });

    // Cargar dashboard
    on<LoadDashboardData>((event, emit) async {
      emit(AppLoading());
      try {
        // Aquí va la lógica para obtener datos del dashboard
        await Future.delayed(Duration(seconds: 2));
        final dashboardData = {'stats': 123}; // ejemplo
        emit(AppSuccess(data: dashboardData));
      } catch (e) {
        emit(AppFailure(message: 'Failed to load dashboard'));
      }
    });

    // Logout
    on<LogoutRequested>((event, emit) async {
      emit(AppInitial());
    });
  }
}