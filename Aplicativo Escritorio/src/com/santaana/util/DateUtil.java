package com.santaana.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_HORA  = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FMT_MES   = DateTimeFormatter.ofPattern("yyyy-MM");

    public static String formatearFecha(Date d) {
        if (d == null) return null;
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(FMT_FECHA);
    }

    public static String formatearFecha(LocalDate d) {
        return d.format(FMT_FECHA);
    }

    public static String formatearHora(Date d) {
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().format(FMT_HORA);
    }

    public static String formatearHora(LocalTime t) {
        return t.format(FMT_HORA);
    }

    public static String formatearMes(YearMonth m) {
        return m.format(FMT_MES);
    }

    public static String formatearMes(LocalDate d) {
        return YearMonth.from(d).format(FMT_MES);
    }

    public static String formatearFechaHora(Date d) {
        if (d == null) return null;
        return d.toInstant().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public static String formatearFechaHora(LocalDate fecha, LocalTime hora) {
        return fecha.format(FMT_FECHA) + " " + hora.format(FMT_HORA);
    }

    public static LocalDate parseFecha(String str) {
        return LocalDate.parse(str, FMT_FECHA);
    }

    public static LocalTime parseHora(String str) {
        return LocalTime.parse(str, FMT_HORA);
    }

    public static YearMonth parseMes(String str) {
        return YearMonth.parse(str, FMT_MES);
    }
}
