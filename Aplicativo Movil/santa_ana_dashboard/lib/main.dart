import 'package:flutter/material.dart';
import 'package:santa_ana_dashboard/feature/home/presentation/views/failure_view.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(debugShowCheckedModeBanner: false, home: FailureView());
  }
}
