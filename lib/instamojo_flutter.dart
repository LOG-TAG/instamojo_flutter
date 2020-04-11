library instamojo_flutter;
import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
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
      bool isProduction,
     @required String baseUrl}) async {
    final String orderId = await _channel.invokeMethod(
      'createOrder',
      <String, dynamic>{
        'isProduction': isProduction,
        'name': name,
        'email': email,
        'mobileNumber':mobileNumber,
        'amount':amount,
        'description':description,
        'baseUrl': baseUrl,
      },
    );
    return orderId;
  }
  
  static Future<String> startPayment(
      {String orderId, bool isProduction = false}) async {
    final String paymentResponse = await _channel.invokeMethod(
      'startPayment',
      <String, dynamic>{
        'orderId': orderId,
        'isProduction': isProduction,
      },
    );
    return paymentResponse;
  }

  static Future<String> createOrderAndStartPayment(
      {String name,
        String email,
        String mobileNumber,
        String amount,
        String description,
        bool isProduction,
        @required String baseUrl}) async {
    final String response = await _channel.invokeMethod(
      'createOrderAndStartPayment',
      <String, dynamic>{
        'isProduction': isProduction,
        'name': name,
        'email': email,
        'mobileNumber':mobileNumber,
        'amount':amount,
        'description':description,
        'baseUrl': baseUrl,
      },
    );
    return response;
  }

  static Future<String> setActionBarColor(
      {String color, String actionBarTextColor}) async {
    final String paymentResponse = await _channel.invokeMethod(
      'setActionBarColor',
      <String, dynamic>{
        'color': color,
        'actionBarTextColor': actionBarTextColor,
      },
    );
    return paymentResponse;
  }

 static getHexMaterialColor(MaterialColor color){
    return '#${color.value.toRadixString(16)}';
  }

  static getHexColor(Color color){
    return '#${color.value.toRadixString(16)}';
  }
}
