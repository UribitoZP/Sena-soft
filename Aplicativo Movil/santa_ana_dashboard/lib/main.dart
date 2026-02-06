import 'package:flutter/material.dart';

import 'views/Login_view.dart';

void main(){
  runApp(Myapp());
}

class Myapp extends StatelessWidget{
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Login(),
    );
  }

}

