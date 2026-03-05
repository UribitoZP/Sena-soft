import 'package:flutter/material.dart';
import 'package:santa_ana_dashboard/core/theme/app_theme.dart';

import 'feature/home/presentation/views/Login_view.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: ThemeMode.dark,
      home: const InitialView(),
    );
  }
}
