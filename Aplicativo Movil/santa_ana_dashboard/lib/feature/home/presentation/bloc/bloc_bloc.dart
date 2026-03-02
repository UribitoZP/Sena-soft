import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/bloc/bloc_event.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/bloc/bloc_state.dart';

class HomeBloc extends Bloc<HomeEvent, HomeState> {

  HomeBloc() : super(EstadoLogin()) {

    on<VistaLoading>((event, emit) async {

      emit(EstadoLoading());

      await Future.delayed(const Duration(seconds: 2));

      emit(EstadoInicial());
    });

    on<VistaLogin>((event, emit) {
      emit(EstadoLogin());
    });

    on<VistaFailure>((event, emit) {
      emit(EstadoFailure());
    });

  }
}