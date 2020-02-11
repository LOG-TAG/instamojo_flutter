import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:instamojo_flutter/instamojo_flutter.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _paymentResponse = 'Unknown';

  @override
  void initState() {
    super.initState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String paymentResponse;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      String orderId = await InstamojoFlutter.createOrder(
        baseUrl: "http://10.0.2.2:8080",
          name: "Kushal",
          email: "kushalmahapatro@gmail.com",
          mobileNumber: "9692968333",
          amount: "8333",
          description: "test payment",
          isProduction: false);
      if (orderId != null) {
        print("OrderId : $orderId");
        Future.delayed(Duration(seconds: 10));
        paymentResponse = await InstamojoFlutter.startPayment(orderId: orderId);
        print("Payment : $paymentResponse");
      }
    } on PlatformException {
      paymentResponse = 'Failed to make payment.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _paymentResponse = paymentResponse;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              RaisedButton(
                color: Colors.deepOrangeAccent,
                child: Container(
                  child: Text("Make Payment"),
                ),
                onPressed: () {
                  initPlatformState();
                },
              ),
              SizedBox(
                height: 20,
              ),
              Text("Response: $_paymentResponse"),
            ],
          ),
        ),
      ),
    );
  }
}

print(String message) {
  debugPrint(message);
}
