import 'package:flutter/material.dart';
import 'package:santa_ana_dashboard/core/theme/app_theme.dart';

// ════════════════════════════════════════════════════════════════
//  MODELOS
// ════════════════════════════════════════════════════════════════

enum ReservationStatus { checkIn, checkOut, inStay, reserved }

class ReservationModel {
  final String id, initials, guestName, subtitle, room, time, extra;
  final int totalNights, currentNight;
  final DateTime checkInDate, checkOutDate;
  final ReservationStatus status;
  final Color avatarColor, avatarBorder;

  const ReservationModel({
    required this.id,
    required this.initials,
    required this.guestName,
    required this.subtitle,
    required this.room,
    required this.time,
    required this.extra,
    required this.totalNights,
    required this.currentNight,
    required this.checkInDate,
    required this.checkOutDate,
    required this.status,
    required this.avatarColor,
    required this.avatarBorder,
  });
}

// Datos de ejemplo — reemplazar con datos del BLoC
final _mockReservations = [
  ReservationModel(
    id: '#RES-2841', initials: 'AM', guestName: 'Andrés Martínez',
    subtitle: 'Suite Deluxe', room: '301', time: '10:00 AM',
    extra: '2 huésp.', totalNights: 3, currentNight: 0,
    checkInDate: DateTime(2026, 3, 4), checkOutDate: DateTime(2026, 3, 7),
    status: ReservationStatus.checkIn,
    avatarColor: Color(0x265B8DEE), avatarBorder: Color(0x4D5B8DEE),
  ),
  ReservationModel(
    id: '#RES-2798', initials: 'SR', guestName: 'Sofia Restrepo',
    subtitle: 'Habitación Estándar', room: '215', time: '12:00 PM',
    extra: '\$420k', totalNights: 4, currentNight: 4,
    checkInDate: DateTime(2026, 2, 29), checkOutDate: DateTime(2026, 3, 4),
    status: ReservationStatus.checkOut,
    avatarColor: Color(0x26C9A84C), avatarBorder: Color(0x4DC9A84C),
  ),
  ReservationModel(
    id: '#RES-2801', initials: 'CG', guestName: 'Carlos Gómez',
    subtitle: 'Junior Suite', room: '512', time: 'Día 2/5',
    extra: '3 huésp.', totalNights: 5, currentNight: 2,
    checkInDate: DateTime(2026, 3, 2), checkOutDate: DateTime(2026, 3, 7),
    status: ReservationStatus.inStay,
    avatarColor: Color(0x264CAF82), avatarBorder: Color(0x4D4CAF82),
  ),
  ReservationModel(
    id: '#RES-2855', initials: 'JL', guestName: 'Juan López',
    subtitle: 'Habitación Estándar', room: '418', time: 'Mañana',
    extra: '\$280k', totalNights: 1, currentNight: 0,
    checkInDate: DateTime(2026, 3, 5), checkOutDate: DateTime(2026, 3, 6),
    status: ReservationStatus.reserved,
    avatarColor: Color(0x265B8DEE), avatarBorder: Color(0x4D5B8DEE),
  ),
];

