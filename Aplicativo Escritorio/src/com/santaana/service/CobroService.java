package com.santaana.service;

import com.santaana.dao.HabitacionDAO;
import com.santaana.dao.ReservaDAO;
import com.santaana.model.Habitacion;
import com.santaana.model.Reserva;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class CobroService {

    private static final int HORA_CORTE = 12;
    private static final int BLOQUE_MINUTOS = 180;
    private static final int TOLERANCIA_MINUTOS = 20;

    // ─────────────────────────────────────────────────────────────────────
    //  Punto de entrada
    // ─────────────────────────────────────────────────────────────────────
    public static void finalizarReserva(int idReserva) {
        ReservaDAO reservaDAO = new ReservaDAO();
        HabitacionDAO habitacionDAO = new HabitacionDAO();

        Reserva reserva = reservaDAO.buscarPorId(idReserva);
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva con ID " + idReserva + " no encontrada");
        }
        if (!"Activa".equals(reserva.getEstado())) {
            throw new IllegalStateException(
                "La reserva no esta activa (estado: " + reserva.getEstado() + ")");
        }

        Habitacion habitacion = habitacionDAO.buscarPorId(reserva.getIdHabitacion());
        if (habitacion == null) {
            throw new IllegalArgumentException("Habitacion no encontrada para la reserva");
        }

        LocalDateTime entrada = parseDateTime(reserva.getFechaEntrada(), reserva.getHoraEntrada());

        boolean esIndefinido = "Indefinido".equals(reserva.getTipoEstadia());
        String fechaSalidaStr = reserva.getFechaSalida();
        String horaSalidaStr = reserva.getHoraSalida();
        LocalDateTime salida;
        if (esIndefinido) {
            salida = LocalDateTime.now();
        } else if (fechaSalidaStr == null || fechaSalidaStr.isEmpty()) {
            salida = LocalDateTime.now();
        } else {
            salida = parseDateTime(fechaSalidaStr, horaSalidaStr);
        }

        if (!salida.isAfter(entrada)) {
            throw new IllegalStateException("La fecha de salida debe ser posterior a la de entrada");
        }

        double total = calcularTotal(entrada, salida, habitacion);
        reservaDAO.finalizar(idReserva, total);
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Algoritmo de segmentacion en tres tramos
    // ─────────────────────────────────────────────────────────────────────
    public static double calcularTotal(LocalDateTime entrada, LocalDateTime salida,
                                 Habitacion habitacion) {
        double precioNoche = habitacion.getPrecio();
        double precioBloque = habitacion.getPrecioBloque();
        LocalDate diaEntrada = entrada.toLocalDate();
        LocalDate diaSalida  = salida.toLocalDate();

        // ── 1. CASO BASE: estadia corta que NO CRUZA ningun mediodia ──────
        boolean mismoDia  = diaEntrada.equals(diaSalida);
        boolean antesDelMediodia = entrada.getHour() < HORA_CORTE
                                && salida.getHour()  < HORA_CORTE;
        boolean despuesDelMediodia = entrada.getHour() >= HORA_CORTE
                                  && salida.getHour()  >= HORA_CORTE;

        if (mismoDia && (antesDelMediodia || despuesDelMediodia)) {
            long minutos = Duration.between(entrada, salida).toMinutes();
            int bloques = calcularBloques(minutos);
            return Math.min(bloques * precioBloque, precioNoche);
        }

        double total = 0;

        // ── 2. TRAMO A: Entrada Anticipada (antes de las 12:00) ──────────
        if (entrada.getHour() < HORA_CORTE) {
            LocalDateTime mediodiaEntrada = diaEntrada.atTime(HORA_CORTE, 0);
            long minutosA = Duration.between(entrada, mediodiaEntrada).toMinutes();
            if (minutosA > 0) {
                int bloquesA = calcularBloques(minutosA);
                total += Math.min(bloquesA * precioBloque, precioNoche);
            }
        }

        // ── 3. TRAMO B: Noches Completas (ciclos de 24h entre mediodias) ──
        // El primer mediodia siempre es el de la fecha de entrada.
        // La noche se cobra completa aunque el huesped llegue despues de las 12:00.
        LocalDateTime primerMediodia = diaEntrada.atTime(HORA_CORTE, 0);

        // Ultimo mediodia efectivo antes de la salida
        LocalDateTime ultimoMediodia = salida.getHour() >= HORA_CORTE
            ? diaSalida.atTime(HORA_CORTE, 0)
            : diaSalida.minusDays(1).atTime(HORA_CORTE, 0);

        if (primerMediodia.isBefore(ultimoMediodia)) {
            long noches = ChronoUnit.DAYS.between(primerMediodia, ultimoMediodia);
            total += noches * precioNoche;
        }

        // ── 4. TRAMO C: Horas Extras al Salir ─────────────────────────────
        // El punto de inicio del tramo final es el ultimo mediodia (si existe)
        // o el primer mediodia (si nunca hubo noches completas)
        LocalDateTime inicioTramoFinal = primerMediodia.isAfter(ultimoMediodia)
            ? primerMediodia
            : ultimoMediodia;

        if (salida.isAfter(inicioTramoFinal)) {
            long minutosC = Duration.between(inicioTramoFinal, salida).toMinutes();
            if (minutosC > 0) {
                int bloquesC = calcularBloques(minutosC);
                total += Math.min(bloquesC * precioBloque, precioNoche);
            }
        }

        return total;
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Logica de bloques de 3h con tolerancia de 20 min
    // ─────────────────────────────────────────────────────────────────────
    private static int calcularBloques(long minutos) {
        if (minutos <= 0) return 0;
        int completos = (int) (minutos / BLOQUE_MINUTOS);
        int resto = (int) (minutos % BLOQUE_MINUTOS);
        int bloques = completos;
        if (resto > TOLERANCIA_MINUTOS) {
            bloques++;
        }
        if (bloques < 1) {
            bloques = 1;
        }
        return bloques;
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Parseo de fecha+hora desde la BD (formato "yyyy-MM-dd HH:mm")
    // ─────────────────────────────────────────────────────────────────────
    private static LocalDateTime parseDateTime(String fecha, String hora) {
        if (hora == null || hora.isEmpty()) hora = "12:00";
        return LocalDateTime.parse(fecha + " " + hora,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
