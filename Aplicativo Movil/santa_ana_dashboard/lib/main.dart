import 'package:flutter/material.dart';

import 'views/Login_view.dart';
import 'views/failure_view.dart';
import 'views/initial_view.dart';
import 'views/loading_view.dart';

void main(){
  runApp(MyApp());
}

class MyApp extends StatelessWidget{
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: FailureView(),
    );
  }

}

