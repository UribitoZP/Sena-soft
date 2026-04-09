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
        if (userData['rol'] != 'Administrador') {
          emit(const AppFailure(
              message: 'Acceso denegado.\nEsta app es solo para administradores.'));
          return;
        }
        emit(AppLoginSuccess(data: userData));
      } catch (e) {
        final msg = e.toString().contains('Credenciales')
            ? 'Credenciales incorrectas'
            : 'No se pudo conectar al servidor.\nVerifica la IP y que el escritorio esté abierto.';
        emit(AppFailure(message: msg));
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
