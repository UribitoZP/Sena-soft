import 'package:equatable/equatable.dart';

abstract class AppState extends Equatable {
  const AppState();
  @override
  List<Object> get props => [];
}

// ── Generales ─────────────────────────────────────────────────

class AppInitial extends AppState {}

class AppLoading extends AppState {}

class AppFailure extends AppState {
  final String message;
  const AppFailure({required this.message});
  @override
  List<Object> get props => [message];
}

// ── Auth ──────────────────────────────────────────────────────

class AppLoginSuccess extends AppState {
  final dynamic data;
  const AppLoginSuccess({this.data});
  @override
  List<Object> get props => [data ?? ''];
}

// ── Dashboard ─────────────────────────────────────────────────

class AppDashboardLoaded extends AppState {
  final dynamic data;
  const AppDashboardLoaded({this.data});
  @override
  List<Object> get props => [data ?? ''];
}

// ── Reservas del día ──────────────────────────────────────────

class ReservasLoading extends AppState {}

class ReservasLoaded extends AppState {
  /// Lista completa sin filtrar
  final List<ReservationItem> reservations;

  /// Lista ya filtrada y buscada — la que muestra la UI
  final List<ReservationItem> filtered;

  final String activeFilter; // 'all' | 'checkIn' | 'checkOut' | 'reserved'
  final String searchQuery;

  const ReservasLoaded({
    required this.reservations,
    required this.filtered,
    this.activeFilter = 'all',
    this.searchQuery = '',
  });

  ReservasLoaded copyWith({
    List<ReservationItem>? reservations,
    List<ReservationItem>? filtered,
    String? activeFilter,
    String? searchQuery,
  }) {
    return ReservasLoaded(
      reservations:  reservations  ?? this.reservations,
      filtered:      filtered      ?? this.filtered,
      activeFilter:  activeFilter  ?? this.activeFilter,
      searchQuery:   searchQuery   ?? this.searchQuery,
    );
  }

  @override
  List<Object> get props => [filtered, activeFilter, searchQuery];
}

class ReservasFailure extends AppState {
  final String message;
  const ReservasFailure({required this.message});
  @override
  List<Object> get props => [message];
}

// ── Modelo de datos ───────────────────────────────────────────

enum ReservationStatus { checkIn, checkOut, reserved }

class ReservationItem extends Equatable {
  final String initials;
  final String name;
  final String detail;
  final String room;
  final String time;
  final ReservationStatus status;

  const ReservationItem({
    required this.initials,
    required this.name,
    required this.detail,
    required this.room,
    required this.time,
    required this.status,
  });

  @override
  List<Object> get props => [name, room];
}