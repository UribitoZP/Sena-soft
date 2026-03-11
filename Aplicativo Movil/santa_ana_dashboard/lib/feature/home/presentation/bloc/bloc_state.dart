import 'package:equatable/equatable.dart';

abstract class AppState extends Equatable {
  const AppState();
  @override
  List<Object> get props => [];
}

// Estado inicial
class AppInitial extends AppState {}

// Estado de carga
class AppLoading extends AppState {}

// Estado de éxito (login o carga de datos)
class AppSuccess extends AppState {
  final dynamic data; // puede ser user info o dashboard data

  const AppSuccess({this.data});

  @override
  List<Object> get props => [data ?? ''];
}

// Estado de fallo
class AppFailure extends AppState {
  final String message;

  const AppFailure({required this.message});

  @override
  List<Object> get props => [message];
}