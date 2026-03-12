import 'package:flutter/material.dart';

class AppTheme {
  // ── Paleta principal ──────────────────────────────────────────
  static const Color bgColor        = Color(0xFF0D0F14);
  static const Color surfaceColor   = Color(0xFF161920);
  static const Color cardColor      = Color(0xFF1C2029);
  static const Color borderColor    = Color(0xFF272C38);

  static const Color goldColor      = Color(0xFFC9A84C);
  static const Color goldLight      = Color(0xFFE8C97A);
  static const Color goldDim        = Color(0x26C9A84C); // ~15% opacity

  static const Color textColor      = Color(0xFFF0ECE3);
  static const Color textMuted      = Color(0xFF7A8099);

  static const Color successColor   = Color(0xFF4CAF82);
  static const Color errorColor     = Color(0xFFE05C5C);
  static const Color infoColor      = Color(0xFF5B8DEE);

  // ── Dark Theme (principal) ────────────────────────────────────
  static ThemeData get darkTheme {
    return ThemeData(
      useMaterial3: true,
      brightness: Brightness.dark,
      scaffoldBackgroundColor: bgColor,
      colorScheme: const ColorScheme.dark(
        primary:   goldColor,
        secondary: goldLight,
        surface:   surfaceColor,
        error:     errorColor,
      ),

      // AppBar
      appBarTheme: const AppBarTheme(
        backgroundColor:  surfaceColor,
        foregroundColor:  textColor,
        elevation:        0,
        centerTitle:      false,
        titleTextStyle: TextStyle(
          fontFamily:  'Georgia',
          fontSize:    20,
          fontWeight:  FontWeight.w700,
          color:       textColor,
          letterSpacing: 0.3,
        ),
      ),

      // Cards
      cardTheme: CardThemeData(
        color:       cardColor,
        elevation:   0,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
          side: const BorderSide(color: borderColor, width: 1),
        ),
      ),

      // Botón principal
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor:  goldColor,
          foregroundColor:  bgColor,
          elevation:        0,
          textStyle: const TextStyle(
            fontWeight: FontWeight.w700,
            fontSize:   15,
            letterSpacing: 0.5,
          ),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
        ),
      ),

      // TextButton
      textButtonTheme: TextButtonThemeData(
        style: TextButton.styleFrom(
          foregroundColor: goldColor,
          textStyle: const TextStyle(fontSize: 13, fontWeight: FontWeight.w500),
        ),
      ),

      // Inputs
      inputDecorationTheme: InputDecorationTheme(
        filled:      true,
        fillColor:   cardColor,
        hintStyle:   const TextStyle(color: textMuted, fontSize: 14),
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: borderColor),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: borderColor),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: borderColor),
        ),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: errorColor),
        ),
      ),

      // Divider
      dividerTheme: const DividerThemeData(
        color:     borderColor,
        thickness: 1,
        space:     1,
      ),

      // BottomNavigationBar
      bottomNavigationBarTheme: const BottomNavigationBarThemeData(
        backgroundColor:     surfaceColor,
        selectedItemColor:   goldColor,
        unselectedItemColor: textMuted,
        elevation:           0,
        type: BottomNavigationBarType.fixed,
        selectedLabelStyle: TextStyle(
          fontSize:   10,
          fontWeight: FontWeight.w600,
          letterSpacing: 0.5,
        ),
        unselectedLabelStyle: TextStyle(fontSize: 10),
      ),

      // Typography
      textTheme: const TextTheme(
        // Títulos con serif (similar a Playfair)
        displayLarge: TextStyle(
          fontFamily:  'Georgia',
          fontSize:    40,
          fontWeight:  FontWeight.w700,
          color:       textColor,
          letterSpacing: -0.5,
        ),
        displayMedium: TextStyle(
          fontFamily:  'Georgia',
          fontSize:    32,
          fontWeight:  FontWeight.w700,
          color:       textColor,
        ),
        headlineLarge: TextStyle(
          fontFamily:  'Georgia',
          fontSize:    26,
          fontWeight:  FontWeight.w700,
          color:       textColor,
        ),
        headlineMedium: TextStyle(
          fontFamily:  'Georgia',
          fontSize:    22,
          fontWeight:  FontWeight.w600,
          color:       textColor,
        ),
        headlineSmall: TextStyle(
          fontFamily:  'Georgia',
          fontSize:    18,
          fontWeight:  FontWeight.w600,
          color:       textColor,
        ),
        // Cuerpo sans-serif
        titleLarge: TextStyle(
          fontSize:    16,
          fontWeight:  FontWeight.w600,
          color:       textColor,
          letterSpacing: 0.1,
        ),
        titleMedium: TextStyle(
          fontSize:    14,
          fontWeight:  FontWeight.w600,
          color:       textColor,
        ),
        titleSmall: TextStyle(
          fontSize:    12,
          fontWeight:  FontWeight.w600,
          color:       textMuted,
          letterSpacing: 0.5,
        ),
        bodyLarge: TextStyle(fontSize: 15, color: textColor, height: 1.5),
        bodyMedium: TextStyle(fontSize: 13, color: textMuted, height: 1.5),
        bodySmall: TextStyle(
          fontSize:    11,
          color:       textMuted,
          letterSpacing: 0.3,
        ),
        labelLarge: TextStyle(
          fontSize:    11,
          fontWeight:  FontWeight.w700,
          letterSpacing: 0.8,
          color:       textColor,
        ),
      ),
    );
  }

  // ── Light Theme (secundario, por compatibilidad) ───────────────
  static ThemeData get lightTheme {
    return ThemeData(
      useMaterial3: true,
      brightness: Brightness.light,
      scaffoldBackgroundColor: const Color(0xFFF5F3EE),
      colorScheme: const ColorScheme.light(
        primary:   goldColor,
        secondary: Color(0xFF8B6914),
        surface:   Colors.white,
        error:     errorColor,
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: goldColor,
          foregroundColor: Colors.white,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
        ),
      ),
      textTheme: const TextTheme(
        headlineMedium: TextStyle(
          fontFamily:  'Georgia',
          fontSize:    22,
          fontWeight:  FontWeight.w700,
          color:       Color(0xFF1A1A1A),
        ),
        bodyLarge: TextStyle(fontSize: 15, color: Color(0xFF444444)),
        bodyMedium: TextStyle(fontSize: 13, color: Color(0xFF777777)),
      ),
    );
  }
}