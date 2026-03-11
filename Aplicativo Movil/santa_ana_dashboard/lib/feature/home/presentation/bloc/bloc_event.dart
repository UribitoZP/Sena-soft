import 'package:equatable/equatable.dart';

abstract class AppEvent extends Equatable {
  const AppEvent();
  @override
  List<Object> get props => [];
}

// Evento para iniciar sesión
class LoginRequested extends AppEvent {
  final String username;
  final String password;

  const LoginRequested({required this.username, required this.password});

  @override
  List<Object> get props => [username, password];
}

// Evento para cargar datos del dashboard
class LoadDashboardData extends AppEvent {}

// Evento para cerrar sesión
class LogoutRequested extends AppEvent {}