import 'package:equatable/equatable.dart';

abstract class AppState extends Equatable {
  const AppState();
  @override
  List<Object> get props => [];
}

class AppInitial extends AppState {}

class AppLoading extends AppState {}

class AppLoginSuccess extends AppState {
  final dynamic data;
  const AppLoginSuccess({this.data});
  @override
  List<Object> get props => [data ?? ''];
}

class AppDashboardLoaded extends AppState {
  final dynamic data;
  const AppDashboardLoaded({this.data});
  @override
  List<Object> get props => [data ?? ''];
}

class AppFailure extends AppState {
  final String message;
  const AppFailure({required this.message});
  @override
  List<Object> get props => [message];
}