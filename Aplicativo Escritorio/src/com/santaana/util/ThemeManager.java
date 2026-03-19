package com.santaana.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    public enum Theme {
        LIGHT, DARK
    }

    private static Theme currentTheme = Theme.LIGHT;
    private static List<ThemeListener> listeners = new ArrayList<>();

    // Light Theme Colors
    public static final Color LIGHT_BG = new Color(0xF0F6FF);
    public static final Color LIGHT_PANEL_BG = Color.WHITE;
    public static final Color LIGHT_PRIMARY = new Color(0x3A7BD5);
    public static final Color LIGHT_TEXT_PRIMARY = new Color(0x1F2937);
    public static final Color LIGHT_TEXT_SECONDARY = new Color(0x6B84A0);
    public static final Color LIGHT_BORDER = new Color(0xDDE8F5);

    // Dark Theme Colors (Dark Blue)
    public static final Color DARK_BG = new Color(0x0F172A);
    public static final Color DARK_PANEL_BG = new Color(0x1E293B);
    public static final Color DARK_PRIMARY = new Color(0x38BDF8);
    public static final Color DARK_TEXT_PRIMARY = new Color(0xF3F4F6);
    public static final Color DARK_TEXT_SECONDARY = new Color(0x94A3B8);
    public static final Color DARK_BORDER = new Color(0x334155);

    public static void toggleTheme() {
        currentTheme = (currentTheme == Theme.LIGHT) ? Theme.DARK : Theme.LIGHT;
        notifyListeners();
    }

    public static Theme getCurrentTheme() {
        return currentTheme;
    }

    public static Color getBackground() {
        return (currentTheme == Theme.LIGHT) ? LIGHT_BG : DARK_BG;
    }

    public static Color getPanelBackground() {
        return (currentTheme == Theme.LIGHT) ? LIGHT_PANEL_BG : DARK_PANEL_BG;
    }

    public static Color getPrimary() {
        return (currentTheme == Theme.LIGHT) ? LIGHT_PRIMARY : DARK_PRIMARY;
    }

    public static Color getTextPrimary() {
        return (currentTheme == Theme.LIGHT) ? LIGHT_TEXT_PRIMARY : DARK_TEXT_PRIMARY;
    }

    public static Color getTextSecondary() {
        return (currentTheme == Theme.LIGHT) ? LIGHT_TEXT_SECONDARY : DARK_TEXT_SECONDARY;
    }

    public static Color getBorder() {
        return (currentTheme == Theme.LIGHT) ? LIGHT_BORDER : DARK_BORDER;
    }

    public static void addListener(ThemeListener listener) {
        listeners.add(listener);
    }

    private static void notifyListeners() {
        for (ThemeListener listener : listeners) {
            listener.onThemeChanged();
        }
    }

    public interface ThemeListener {
        void onThemeChanged();
    }
}
