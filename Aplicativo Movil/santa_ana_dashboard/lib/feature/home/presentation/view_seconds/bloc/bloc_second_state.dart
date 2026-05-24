abstract class BlocSecondState {}

class BlocSecondInitial extends BlocSecondState {}

class ReservasLoading extends BlocSecondState {}

class ReservasLoaded extends BlocSecondState {
  final List<dynamic> reservas;

  ReservasLoaded({required this.reservas});
}

class ReservasFailure extends BlocSecondState {
  final String message;

  ReservasFailure({required this.message});
}