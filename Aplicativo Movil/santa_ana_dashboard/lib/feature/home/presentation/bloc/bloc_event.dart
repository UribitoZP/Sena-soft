import 'package:equatable/equatable.dart';

abstract class AppEvent extends Equatable {
  const AppEvent();
  @override
  List<Object> get props => [];
}

// ── Auth ──────────────────────────────────────────────────────

class LoginRequested extends AppEvent {
  final String username;
  final String password;

  const LoginRequested({required this.username, required this.password});

  @override
  List<Object> get props => [username, password];
}

class LogoutRequested extends AppEvent {}

// ── Dashboard ─────────────────────────────────────────────────

class LoadDashboardData extends AppEvent {}


// ── Reservas del día ──────────────────────────────────────────

/// Carga (o recarga) las reservas del día
class LoadReservasDelDia extends AppEvent {}

/// Cambia el filtro activo (all, checkIn, checkOut, reserved)
class FilterReservasChanged extends AppEvent {
  final String filter; // 'all' | 'checkIn' | 'checkOut' | 'reserved'
  const FilterReservasChanged({required this.filter});

  @override
  List<Object> get props => [filter];
}

/// Actualiza el texto de búsqueda
class SearchReservasChanged extends AppEvent {
  final String query;
  const SearchReservasChanged({required this.query});

  @override
  List<Object> get props => [query];
}