import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:instamojo_flutter/instamojo_flutter.dart';
import 'package:instamojo_flutter_example/screen/payment_screen.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  @override
  void initState() {
    super.initState();
    InstamojoFlutter.setActionBarColor(color: InstamojoFlutter.getHexMaterialColor(Colors.amber),
    actionBarTextColor:InstamojoFlutter.getHexColor((Colors.black)) );
  }


  @override
  Widget build(BuildContext context) {

    return MaterialApp(
      color: Colors.black,
      routes: {
      },
      title: 'Styli',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
          appBarTheme: AppBarTheme(brightness: Brightness.dark),
          primarySwatch: Colors.amber,
          canvasColor: Colors.white,
          backgroundColor: Colors.white,
          brightness: Brightness.light,
          cupertinoOverrideTheme: CupertinoThemeData(
            brightness: Brightness.light,
            scaffoldBackgroundColor: Colors.white,
            barBackgroundColor: Colors.white,
            primaryColor: Colors.amber,
          )),
      home: PaymentScreen()
    );
  }
}

print(String message) {
  debugPrint(message);
}
