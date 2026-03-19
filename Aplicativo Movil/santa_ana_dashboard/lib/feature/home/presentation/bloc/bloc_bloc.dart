import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/bloc/bloc_event.dart';
import 'bloc_state.dart';

class AppBloc extends Bloc<AppEvent, AppState> {
  AppBloc() : super(AppInitial()) {

    on<LoginRequested>((event, emit) async {
      emit(AppLoading());
      try {
        await Future.delayed(Duration(seconds: 3));
        final userData = {'username': event.username};
        emit(AppLoginSuccess(data: userData));
      } catch (e) {
        emit(AppFailure(message: 'Login failed'));
      }
    });

    on<LoadDashboardData>((event, emit) async {
      emit(AppLoading());
      try {
        await Future.delayed(Duration(seconds: 3));
        final dashboardData = {'stats': 123};
        emit(AppDashboardLoaded(data: dashboardData));
      } catch (e) {
        emit(AppFailure(message: 'Failed to load dashboard'));
      }
    });

    on<LogoutRequested>((event, emit) async {
      emit(AppInitial());
    });
  }
}