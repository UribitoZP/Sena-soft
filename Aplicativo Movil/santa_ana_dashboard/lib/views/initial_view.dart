import 'package:flutter/material.dart';

class InitialView extends StatelessWidget {
  const InitialView({super.key});

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      body: Center(
        child: Text(
          'Estado inicial',
          style: TextStyle(fontSize: 22),
        ),
      ),
    );
  }
}
