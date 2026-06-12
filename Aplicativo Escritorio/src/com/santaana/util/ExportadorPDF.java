package com.santaana.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.santaana.dao.ReporteDAO;
import com.santaana.dao.CierreMesDAO;
import com.santaana.model.CierreMes;
import com.santaana.db.DatabaseException;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

/**
 * Exporta el reporte mensual del Hotel Santa Ana a PDF.
 * Uso: ExportadorPDF.exportar(mes, idUsuario, nombreUsuario, destino)
 *   o: ExportadorPDF.exportar(mes, idUsuario, nombreUsuario, null) → abre diálogo guardar
 */
public class ExportadorPDF {

    // ── Paleta ────────────────────────────────────────────────────────────────
    private static final BaseColor C_DARK    = new BaseColor(0x0D, 0x0F, 0x14);
    private static final BaseColor C_NAVY    = new BaseColor(0x13, 0x17, 0x20);
    private static final BaseColor C_GOLD    = new BaseColor(0xC9, 0xA8, 0x4C);
    private static final BaseColor C_GREEN   = new BaseColor(0x22, 0xC5, 0x5E);
    private static final BaseColor C_BLUE    = new BaseColor(0x3A, 0x7B, 0xD5);
    private static final BaseColor C_RED     = new BaseColor(0xEF, 0x44, 0x44);
    private static final BaseColor C_AMBER   = new BaseColor(0xF5, 0x9E, 0x0B);
    private static final BaseColor C_PURPLE  = new BaseColor(0xA8, 0x55, 0xF7);
    private static final BaseColor C_LIGHT   = new BaseColor(0xF8, 0xF9, 0xFB);
    private static final BaseColor C_MUTED   = new BaseColor(0x64, 0x74, 0x8B);
    private static final BaseColor C_TEXT    = new BaseColor(0x1E, 0x29, 0x3B);
    private static final BaseColor C_BORDER  = new BaseColor(0xE2, 0xE8, 0xF0);
    private static final BaseColor C_WHITE   = BaseColor.WHITE;

    // ── Fuentes ───────────────────────────────────────────────────────────────
    private static final Font F_TITLE_BIG  = new Font(Font.FontFamily.HELVETICA, 26, Font.BOLD,   C_WHITE);
    private static final Font F_TITLE_SUB  = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, new BaseColor(0xC9, 0xA8, 0x4C));
    private static final Font F_SECTION    = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD,   C_TEXT);
    private static final Font F_KPI_VAL    = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD,   C_TEXT);
    private static final Font F_KPI_LBL    = new Font(Font.FontFamily.HELVETICA, 9,  Font.NORMAL, C_MUTED);
    private static final Font F_KPI_SUB    = new Font(Font.FontFamily.HELVETICA, 8,  Font.NORMAL, C_MUTED);
    private static final Font F_TABLE_H    = new Font(Font.FontFamily.HELVETICA, 9,  Font.BOLD,   C_WHITE);
    private static final Font F_TABLE_C    = new Font(Font.FontFamily.HELVETICA, 9,  Font.NORMAL, C_TEXT);
    private static final Font F_BODY       = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, C_TEXT);
    private static final Font F_FOOTER     = new Font(Font.FontFamily.HELVETICA, 8,  Font.NORMAL, C_MUTED);

    private static final NumberFormat FMT_COP = NumberFormat.getInstance(new Locale("es", "CO"));
    static { FMT_COP.setMaximumFractionDigits(0); }

    // ── Método principal ──────────────────────────────────────────────────────

    /**
     * Genera el PDF del reporte mensual.
     *
     * @param mes          Formato "yyyy-MM" (ej: "2026-05")
     * @param idUsuario    ID del usuario que exporta
     * @param nombreUsuario Nombre del usuario
     * @param destino      Ruta completa del archivo destino, o null para usar JFileChooser
     * @return             File generado, o null si se canceló
     */
    public static File exportar(String mes, int idUsuario, String nombreUsuario, File destino) {
        // Si no se pasó destino, abrir diálogo para guardar
        if (destino == null) {
            javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
            fc.setDialogTitle("Guardar reporte PDF");
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivo PDF", "pdf"));
            String mesLabel = mes.replace("-", "_");
            fc.setSelectedFile(new File("Reporte_HotelSantaAna_" + mesLabel + ".pdf"));
            int res = fc.showSaveDialog(null);
            if (res != javax.swing.JFileChooser.APPROVE_OPTION) return null;
            destino = fc.getSelectedFile();
            if (!destino.getName().toLowerCase().endsWith(".pdf")) {
                destino = new File(destino.getAbsolutePath() + ".pdf");
            }
        }

        // Cargar datos
        ReporteDAO  dao      = new ReporteDAO();
        CierreMesDAO cierreDAO = new CierreMesDAO();

        double totalIngresos, totalAnticipos;
        int    totalReservas;
        Map<String, Integer> porEstado;
        Map<String, Double>  ingresosMes;
        Map<String, Integer> reservasMes;
        Map<String, Integer> topHabs;
        CierreMes cierre = null;

        try {
            totalIngresos  = dao.getTotalIngresos();
            totalAnticipos = dao.getTotalAnticipos();
            totalReservas  = dao.getTotalReservas();
            porEstado   = dao.getReservasPorEstado();
            ingresosMes = dao.getIngresosPorMes();
            reservasMes = dao.getReservasPorMes();
            topHabs     = dao.getTopHabitaciones();
            try { cierre = cierreDAO.getCierre(mes); } catch (Exception ignored) {}
        } catch (DatabaseException e) {
            javax.swing.JOptionPane.showMessageDialog(null,
                "Error al cargar datos: " + e.getMessage(),
                "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return null;
        }

        int completadas = porEstado.getOrDefault("Finalizada", 0);
        int canceladas  = porEstado.getOrDefault("Cancelada",  0);
        int activas     = porEstado.getOrDefault("Activa",     0);

        // Etiqueta del mes
        LocalDate fecha    = LocalDate.parse(mes + "-01");
        String    mesLabel = fecha.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "CO")));
        mesLabel = mesLabel.substring(0, 1).toUpperCase() + mesLabel.substring(1);

        try {
            Document doc = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(destino));
            writer.setPageEvent(new HeaderFooterEvent(mes, mesLabel, nombreUsuario));
            doc.open();

            // ── Portada / Header ─────────────────────────────────────────────
            agregarPortada(doc, writer, mesLabel, nombreUsuario, cierre);

            // ── Resumen KPIs ─────────────────────────────────────────────────
            agregarSeccion(doc, "Resumen Financiero");
            agregarKpis(doc, totalIngresos, totalAnticipos, totalReservas,
                        activas, completadas, canceladas);

            // ── Estado de cierre ─────────────────────────────────────────────
            agregarSeccion(doc, "Estado del Mes");
            agregarEstadoCierre(doc, cierre, mes, mesLabel);

            // ── Ingresos por mes ─────────────────────────────────────────────
            agregarSeccion(doc, "Ingresos por Mes (últimos 6 meses)");
            agregarTablaIngresos(doc, ingresosMes);

            // ── Reservas por mes ─────────────────────────────────────────────
            agregarSeccion(doc, "Reservas por Mes (últimos 6 meses)");
            agregarTablaReservas(doc, reservasMes);

            // ── Reservas por estado ──────────────────────────────────────────
            agregarSeccion(doc, "Distribución de Reservas por Estado");
            agregarTablaPorEstado(doc, porEstado, totalReservas);

            // ── Top habitaciones ─────────────────────────────────────────────
            agregarSeccion(doc, "Top 5 Habitaciones más Reservadas");
            agregarTablaTopHabitaciones(doc, topHabs);

            // ── Firma / Cierre ───────────────────────────────────────────────
            agregarFirma(doc, nombreUsuario, mes, cierre);

            doc.close();

            // Abrir el PDF automáticamente
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(destino);
            }

            return destino;

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null,
                "Error al generar el PDF: " + e.getMessage(),
                "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    // ── Portada ───────────────────────────────────────────────────────────────

    private static void agregarPortada(Document doc, PdfWriter writer,
            String mesLabel, String usuario, CierreMes cierre) throws DocumentException {

        PdfContentByte cb = writer.getDirectContent();
        float w = doc.getPageSize().getWidth();

        // Rectángulo oscuro superior
        cb.setColorFill(C_DARK);
        cb.rectangle(0, doc.getPageSize().getHeight() - 130, w, 130);
        cb.fill();

        // Línea dorada
        cb.setColorFill(C_GOLD);
        cb.rectangle(0, doc.getPageSize().getHeight() - 133, w, 3);
        cb.fill();

        // Rectángulo izquierdo dorado
        cb.setColorFill(C_GOLD);
        cb.rectangle(0, doc.getPageSize().getHeight() - 130, 4, 130);
        cb.fill();

        // Título en el header oscuro
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
            new Phrase("HOTEL SANTA ANA", new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, new BaseColor(0xC9, 0xA8, 0x4C))),
            50, doc.getPageSize().getHeight() - 30, 0);

        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
            new Phrase("REPORTE MENSUAL DE CONTABILIDAD", F_TITLE_BIG),
            50, doc.getPageSize().getHeight() - 65, 0);

        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
            new Phrase(mesLabel + "  ·  Generado por: " + usuario, F_TITLE_SUB),
            50, doc.getPageSize().getHeight() - 90, 0);

        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
            new Phrase("Fecha: " + fechaHoy, F_FOOTER),
            w - 40, doc.getPageSize().getHeight() - 90, 0);

        doc.add(new Paragraph(" ", new Font(Font.FontFamily.HELVETICA, 8)));
    }

    // ── Sección header ────────────────────────────────────────────────────────

    private static void agregarSeccion(Document doc, String titulo) throws DocumentException {
        doc.add(new Paragraph(" ", new Font(Font.FontFamily.HELVETICA, 6)));
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase(titulo, F_SECTION));
        cell.setBackgroundColor(C_LIGHT);
        cell.setBorderColor(C_GOLD);
        cell.setBorderWidthBottom(2);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthLeft(3);
        cell.setBorderWidthRight(0);
        cell.setPadding(8);
        table.addCell(cell);
        doc.add(table);
        doc.add(new Paragraph(" ", new Font(Font.FontFamily.HELVETICA, 4)));
    }

    // ── KPIs ──────────────────────────────────────────────────────────────────

    private static void agregarKpis(Document doc, double totalIngresos, double totalAnticipos,
            int totalReservas, int activas, int completadas, int canceladas) throws DocumentException {

        double tasaCanc = totalReservas > 0 ? 100.0 * canceladas / totalReservas : 0;

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(4);

        // KPI 1 — Ingresos
        table.addCell(kpiCell("INGRESOS CONFIRMADOS",
            "$ " + FMT_COP.format(totalIngresos),
            "De reservas finalizadas", C_GREEN));

        // KPI 2 — Anticipos
        table.addCell(kpiCell("ANTICIPOS TOTALES",
            "$ " + FMT_COP.format(totalAnticipos),
            "Suma de todos los anticipos", C_BLUE));

        // KPI 3 — Reservas
        table.addCell(kpiCell("TOTAL RESERVAS",
            String.valueOf(totalReservas),
            activas + " activas · " + completadas + " completadas", C_AMBER));

        // KPI 4 — Cancelaciones
        table.addCell(kpiCell("CANCELACIONES",
            String.valueOf(canceladas),
            String.format("%.1f%% tasa cancelación", tasaCanc), C_RED));

        doc.add(table);
    }

    private static PdfPCell kpiCell(String label, String valor, String sub, BaseColor accent) {
        PdfPCell cell = new PdfPCell();
        cell.setBorderColor(C_BORDER);
        cell.setBorderWidth(1);
        cell.setBorderWidthLeft(3);
        cell.setBorderColorLeft(accent);
        cell.setPadding(10);
        cell.setBackgroundColor(C_WHITE);

        Paragraph p = new Paragraph();
        p.add(new Chunk(label + "\n", F_KPI_LBL));
        Chunk valChunk = new Chunk(valor, new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, accent));
        p.add(valChunk);
        p.add(new Chunk("\n" + sub, F_KPI_SUB));
        cell.addElement(p);
        return cell;
    }

    // ── Estado de cierre ──────────────────────────────────────────────────────

    private static void agregarEstadoCierre(Document doc, CierreMes cierre,
            String mes, String mesLabel) throws DocumentException {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        t.setSpacingBefore(4);

        PdfPCell cell = new PdfPCell();
        cell.setPadding(12);

        if (cierre != null) {
            cell.setBackgroundColor(new BaseColor(0xF0, 0xFD, 0xF4));
            cell.setBorderColor(new BaseColor(0x86, 0xEF, 0xAC));
            cell.setBorderWidth(1);
            cell.setBorderWidthLeft(4);
            cell.setBorderColorLeft(C_GREEN);

            String fecha = cierre.getFechaCierre() != null && cierre.getFechaCierre().length() >= 10
                    ? cierre.getFechaCierre().substring(0, 10) : "";
            String usuario = cierre.getNombreUsuario() != null ? cierre.getNombreUsuario() : "N/A";
            String notas   = (cierre.getNotas() != null && !cierre.getNotas().isEmpty())
                    ? cierre.getNotas() : "Sin notas.";

            Paragraph p = new Paragraph();
            p.add(new Chunk("✓  Mes CERRADO contablemente\n",
                new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, C_GREEN)));
            p.add(new Chunk("Cerrado por: " + usuario + "   |   Fecha de cierre: " + fecha + "\n", F_BODY));
            p.add(new Chunk("Notas: " + notas, F_KPI_LBL));
            cell.addElement(p);
        } else {
            cell.setBackgroundColor(new BaseColor(0xFF, 0xFB, 0xEB));
            cell.setBorderColor(new BaseColor(0xFD, 0xE6, 0x8A));
            cell.setBorderWidth(1);
            cell.setBorderWidthLeft(4);
            cell.setBorderColorLeft(C_AMBER);


            Paragraph p = new Paragraph();
            p.add(new Chunk("⚠  Mes PENDIENTE de cierre contable\n",
                new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, C_AMBER)));
            p.add(new Chunk("El mes de " + mesLabel + " aún no ha sido cerrado. "
                + "Los datos pueden cambiar hasta que se realice el cierre.", F_BODY));
            cell.addElement(p);
        }

        t.addCell(cell);
        doc.add(t);
    }

    // ── Tabla ingresos por mes ─────────────────────────────────────────────────

    private static void agregarTablaIngresos(Document doc,
            Map<String, Double> data) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{3, 5, 3});
        table.setWidthPercentage(100);
        table.setSpacingBefore(4);

        agregarHeaderCelda(table, "Mes",       C_DARK);
        agregarHeaderCelda(table, "Ingresos",  C_DARK);
        agregarHeaderCelda(table, "Variación", C_DARK);

        Double anterior = null;
        int row = 0;
        for (Map.Entry<String, Double> e : data.entrySet()) {
            BaseColor bg = row % 2 == 0 ? C_WHITE : C_LIGHT;
            String mes = e.getKey().length() >= 7
                ? e.getKey().substring(5) + "/" + e.getKey().substring(0, 4)
                : e.getKey();

            String variacion = "—";
            BaseColor varColor = C_MUTED;
            if (anterior != null && anterior > 0) {
                double pct = (e.getValue() - anterior) / anterior * 100;
                variacion = String.format("%+.1f%%", pct);
                varColor = pct >= 0 ? C_GREEN : C_RED;
            }

            agregarCelda(table, mes,                                  bg, F_TABLE_C, Element.ALIGN_LEFT);
            agregarCelda(table, "$ " + FMT_COP.format(e.getValue()),  bg, F_TABLE_C, Element.ALIGN_RIGHT);
            PdfPCell varCell = new PdfPCell(new Phrase(variacion,
                new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, varColor)));
            varCell.setBackgroundColor(bg);
            varCell.setBorderColor(C_BORDER);
            varCell.setBorderWidth(0.5f);
            varCell.setPadding(6);
            varCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(varCell);

            anterior = e.getValue();
            row++;
        }

        if (data.isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase("Sin datos disponibles", F_KPI_LBL));
            empty.setColspan(3);
            empty.setPadding(10);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(empty);
        }

        doc.add(table);
    }

    // ── Tabla reservas por mes ─────────────────────────────────────────────────

    private static void agregarTablaReservas(Document doc,
            Map<String, Integer> data) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{4, 3, 4});
        table.setWidthPercentage(100);
        table.setSpacingBefore(4);

        agregarHeaderCelda(table, "Mes",        C_DARK);
        agregarHeaderCelda(table, "Reservas",   C_DARK);
        agregarHeaderCelda(table, "Variación",  C_DARK);

        Integer anterior = null;
        int row = 0;
        for (Map.Entry<String, Integer> e : data.entrySet()) {
            BaseColor bg = row % 2 == 0 ? C_WHITE : C_LIGHT;
            String mes = e.getKey().length() >= 7
                ? e.getKey().substring(5) + "/" + e.getKey().substring(0, 4)
                : e.getKey();

            String variacion = "—";
            BaseColor varColor = C_MUTED;
            if (anterior != null && anterior > 0) {
                double pct = (e.getValue() - anterior) * 100.0 / anterior;
                variacion = String.format("%+.1f%%", pct);
                varColor = pct >= 0 ? C_GREEN : C_RED;
            }

            agregarCelda(table, mes,                    bg, F_TABLE_C, Element.ALIGN_LEFT);
            agregarCelda(table, String.valueOf(e.getValue()), bg, F_TABLE_C, Element.ALIGN_CENTER);
            PdfPCell varCell = new PdfPCell(new Phrase(variacion,
                new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, varColor)));
            varCell.setBackgroundColor(bg);
            varCell.setBorderColor(C_BORDER);
            varCell.setBorderWidth(0.5f);
            varCell.setPadding(6);
            varCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(varCell);

            anterior = e.getValue();
            row++;
        }

        if (data.isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase("Sin datos disponibles", F_KPI_LBL));
            empty.setColspan(3);
            empty.setPadding(10);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(empty);
        }

        doc.add(table);
    }

    // ── Tabla por estado ──────────────────────────────────────────────────────

    private static void agregarTablaPorEstado(Document doc,
            Map<String, Integer> data, int total) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{4, 3, 3, 3});
        table.setWidthPercentage(100);
        table.setSpacingBefore(4);

        agregarHeaderCelda(table, "Estado",     C_DARK);
        agregarHeaderCelda(table, "Cantidad",   C_DARK);
        agregarHeaderCelda(table, "% del Total",C_DARK);
        agregarHeaderCelda(table, "Indicador",  C_DARK);

        Map<String, BaseColor> colores = new java.util.HashMap<>();
        colores.put("Activa",     C_BLUE);
        colores.put("Finalizada", C_GREEN);
        colores.put("Cancelada",  C_RED);

        int row = 0;
        for (Map.Entry<String, Integer> e : data.entrySet()) {
            BaseColor bg = row % 2 == 0 ? C_WHITE : C_LIGHT;
            BaseColor accent = colores.getOrDefault(e.getKey(), C_MUTED);
            double pct = total > 0 ? 100.0 * e.getValue() / total : 0;

            agregarCelda(table, e.getKey(), bg, F_TABLE_C, Element.ALIGN_LEFT);
            agregarCelda(table, String.valueOf(e.getValue()), bg, F_TABLE_C, Element.ALIGN_CENTER);
            agregarCelda(table, String.format("%.1f%%", pct), bg, F_TABLE_C, Element.ALIGN_CENTER);

            // Barra de progreso simple
            PdfPCell barCell = new PdfPCell();
            barCell.setBackgroundColor(bg);
            barCell.setBorderColor(C_BORDER);
            barCell.setBorderWidth(0.5f);
            barCell.setPadding(6);
            barCell.addElement(new Phrase(
                "█".repeat(Math.max(1, (int)(pct / 10))) +
                "░".repeat(Math.max(0, 10 - (int)(pct / 10))),
                new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, accent)));
            table.addCell(barCell);
            row++;
        }

        doc.add(table);
    }

    // ── Top habitaciones ──────────────────────────────────────────────────────

    private static void agregarTablaTopHabitaciones(Document doc,
            Map<String, Integer> data) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{1, 4, 3, 3});
        table.setWidthPercentage(100);
        table.setSpacingBefore(4);

        agregarHeaderCelda(table, "#",          C_DARK);
        agregarHeaderCelda(table, "Habitación", C_DARK);
        agregarHeaderCelda(table, "Reservas",   C_DARK);
        agregarHeaderCelda(table, "Posición",   C_DARK);

        BaseColor[] medals = { C_GOLD, C_MUTED, new BaseColor(0xCD, 0x7F, 0x32), C_MUTED, C_MUTED };
        String[]    pos    = { "🥇 1°", "🥈 2°", "🥉 3°", "4°", "5°" };

        int row = 0;
        for (Map.Entry<String, Integer> e : data.entrySet()) {
            BaseColor bg = row % 2 == 0 ? C_WHITE : C_LIGHT;
            BaseColor medal = row < medals.length ? medals[row] : C_MUTED;

            PdfPCell numCell = new PdfPCell(new Phrase(String.valueOf(row + 1),
                new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, medal)));
            numCell.setBackgroundColor(bg);
            numCell.setBorderColor(C_BORDER);
            numCell.setBorderWidth(0.5f);
            numCell.setPadding(7);
            numCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(numCell);

            agregarCelda(table, e.getKey(),              bg, F_TABLE_C, Element.ALIGN_LEFT);
            agregarCelda(table, String.valueOf(e.getValue()) + " reservas", bg,
                new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, medal), Element.ALIGN_CENTER);
            agregarCelda(table, row < pos.length ? pos[row] : (row + 1) + "°",
                bg, F_TABLE_C, Element.ALIGN_CENTER);
            row++;
        }

        if (data.isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase("Sin datos disponibles", F_KPI_LBL));
            empty.setColspan(4);
            empty.setPadding(10);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(empty);
        }

        doc.add(table);
    }

    // ── Firma / Cierre del documento ──────────────────────────────────────────

    private static void agregarFirma(Document doc, String usuario,
            String mes, CierreMes cierre) throws DocumentException {
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(" "));

        PdfPTable t = new PdfPTable(new float[]{1, 1});
        t.setWidthPercentage(100);
        t.setSpacingBefore(16);

        // Firma izquierda — quien exporta
        PdfPCell left = new PdfPCell();
        left.setBorderColor(C_BORDER);
        left.setBorderWidth(0.5f);
        left.setBorderWidthTop(2);
        left.setBorderColorTop(C_GOLD);
        left.setPadding(12);
        Paragraph pLeft = new Paragraph();
        pLeft.add(new Chunk(usuario + "\n",
            new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, C_TEXT)));
        pLeft.add(new Chunk("Responsable del reporte\n", F_KPI_LBL));
        pLeft.add(new Chunk("Fecha: " + LocalDate.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy")), F_KPI_LBL));
        left.addElement(pLeft);
        t.addCell(left);

        // Firma derecha — estado del cierre
        PdfPCell right = new PdfPCell();
        right.setBorderColor(C_BORDER);
        right.setBorderWidth(0.5f);
        right.setBorderWidthTop(2);
        right.setBorderColorTop(cierre != null ? C_GREEN : C_AMBER);
        right.setPadding(12);
        Paragraph pRight = new Paragraph();
        if (cierre != null) {
            pRight.add(new Chunk("MES CERRADO CONTABLEMENTE\n",
                new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, C_GREEN)));
            pRight.add(new Chunk("Este reporte tiene carácter definitivo.", F_KPI_LBL));
        } else {
            pRight.add(new Chunk("MES EN CURSO — PENDIENTE DE CIERRE\n",
                new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, C_AMBER)));
            pRight.add(new Chunk("Los datos pueden variar hasta el cierre contable.", F_KPI_LBL));
        }
        right.addElement(pRight);
        t.addCell(right);

        doc.add(t);
    }

    // ── Helpers de tabla ──────────────────────────────────────────────────────

    private static void agregarHeaderCelda(PdfPTable table, String texto, BaseColor bg) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, F_TABLE_H));
        cell.setBackgroundColor(bg);
        cell.setBorderColor(C_BORDER);
        cell.setBorderWidth(0.5f);
        cell.setPadding(7);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private static void agregarCelda(PdfPTable table, String texto,
            BaseColor bg, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(bg);
        cell.setBorderColor(C_BORDER);
        cell.setBorderWidth(0.5f);
        cell.setPadding(6);
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }

    // ── Header/Footer de páginas ──────────────────────────────────────────────

    static class HeaderFooterEvent extends PdfPageEventHelper {
        private final String mes, mesLabel, usuario;

        HeaderFooterEvent(String mes, String mesLabel, String usuario) {
            this.mes = mes;
            this.mesLabel = mesLabel;
            this.usuario = usuario;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document doc) {
            PdfContentByte cb = writer.getDirectContent();
            float w = doc.getPageSize().getWidth();

            // Footer
            cb.setColorFill(new BaseColor(0xF1, 0xF5, 0xF9));
            cb.rectangle(0, 0, w, 30);
            cb.fill();

            cb.setColorFill(C_GOLD);
            cb.rectangle(0, 0, 3, 30);
            cb.fill();

            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                new Phrase("Hotel Santa Ana  ·  Reporte " + mesLabel, F_FOOTER),
                20, 10, 0);

            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                new Phrase("Página " + writer.getPageNumber() + "  |  Generado por: " + usuario, F_FOOTER),
                w - 20, 10, 0);
        }
    }
}
