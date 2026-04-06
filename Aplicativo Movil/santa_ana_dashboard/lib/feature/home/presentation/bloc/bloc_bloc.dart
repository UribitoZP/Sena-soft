import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:santa_ana_dashboard/core/services/api_service.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/bloc/bloc_event.dart';
import 'bloc_state.dart';

class AppBloc extends Bloc<AppEvent, AppState> {
  final _api = ApiService();

  AppBloc() : super(AppInitial()) {

    on<LoginRequested>((event, emit) async {
      emit(AppLoading());
      try {
        final userData = await _api.login(event.username, event.password);
        emit(AppLoginSuccess(data: userData));
      } catch (_) {
        emit(const AppFailure(message: 'Credenciales incorrectas'));
      }
    });

    on<LoadDashboardData>((event, emit) async {
      emit(AppLoading());
      try {
        final stats = await _api.getStats();
        emit(AppDashboardLoaded(data: stats));
      } catch (e) {
        emit(AppFailure(message: e.toString()));
      }
    });

    on<LogoutRequested>((event, emit) async {
      emit(AppInitial());
    });
  }
}
