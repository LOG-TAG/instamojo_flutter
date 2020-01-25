library instamojo_flutter;
import 'dart:async';

import 'package:flutter/services.dart';

class InstamojoFlutter {
  static const MethodChannel _channel =
      const MethodChannel('instamojo_flutter');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> createOrder(
      {String name,
      String email,
      String mobileNumber,
      String amount,
      String description,
      bool isProduction}) async {
    final String orderId = await _channel.invokeMethod(
      'createOrder',
      <String, dynamic>{
        'isProduction': isProduction,
        'name': name,
        'email': email,
        'mobileNumber':mobileNumber,
        'amount':amount,
        'description':description,
      },
    );
    return orderId;
  }

  static Future<String> startPayment(
      {String orderId}) async {
    final String paymentResponse = await _channel.invokeMethod(
      'startPayment',
      <String, dynamic>{
        'orderId': orderId,
      },
    );
    return paymentResponse;
  }
}
