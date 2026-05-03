import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:santa_ana_dashboard/core/services/api_service.dart';
import 'bloc_second_event.dart';
import 'bloc_second_state.dart';

class BlocSecond extends Bloc<BlocSecondEvent, BlocSecondState> {
  final _api = ApiService();

  BlocSecond() : super(BlocSecondInitial()) {

    on<LoadReservasDelDia>((event, emit) async {
      emit(ReservasLoading());

      try {
        final reservas = await _api.getReservas();

        final today = DateTime.now();

        final reservasHoy = reservas.where((reserva) {
          final fechaString = reserva['fecha'];

          final fecha = DateTime.parse(fechaString);

          return fecha.year == today.year &&
                 fecha.month == today.month &&
                 fecha.day == today.day;
        }).toList();

        emit(ReservasLoaded(reservas: reservasHoy));

      } catch (e) {
        emit(ReservasFailure(message: 'Error cargando reservas'));
      }
    });

  }
}