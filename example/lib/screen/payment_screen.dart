import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:instamojo_flutter/instamojo_flutter.dart';
import 'package:instamojo_flutter_example/model/data_model.dart';


class PaymentScreen extends StatefulWidget {
  @override
  _PaymentScreenState createState() => _PaymentScreenState();
}

class _PaymentScreenState extends State<PaymentScreen> with SingleTickerProviderStateMixin{
  String _paymentResponse = 'Unknown';
  bool isLive, apiCalled;
  final _formKey = GlobalKey<FormState>();
  final _data = DataModel();
  bool _autoValidate = false;
  AnimationController _controller;


  @override
  void initState() {
    super.initState();
    isLive = false;
    apiCalled = false;
    _controller = AnimationController(
      vsync: this,
      lowerBound: 0.5,
      duration: Duration(milliseconds: 500),
    )..repeat();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    setState(() {
      apiCalled = true;
    });
    String paymentResponse;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      String orderId = await InstamojoFlutter.createOrder(
          baseUrl: "http://10.14.169.218:8333",
//          baseUrl: "https://sample-sdk-server.instamojo.com/",
          name: _data.name,
          email: _data.email,
          mobileNumber: _data.number,
          amount: _data.amount,
          description: _data.description,
          isProduction: _data.isLive);
      if (orderId != null) {
        Future.delayed(Duration(seconds: 10));
        setState(() {
          apiCalled = false;
        });
        paymentResponse = await InstamojoFlutter.startPayment(orderId: orderId);
        print("Payment : $paymentResponse");
      }else{
        setState(() {
          apiCalled = false;
        });
      }
    } on PlatformException {
      setState(() {
        apiCalled = false;
      });
      paymentResponse = 'Failed to make payment.';
    }

    if (!mounted) return;

    setState(() {
      _paymentResponse = paymentResponse;
    });
  }
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: const Text('Instamojo Flutter'),
        ),
        body: SingleChildScrollView(child: Column(
          children: <Widget>[
            Container(
                padding: EdgeInsets.symmetric(horizontal: 16),
                child: Builder(
                    builder: (context) => Form(
                        key: _formKey,
                        autovalidate: _autoValidate,
                        child: Column(
                            crossAxisAlignment: CrossAxisAlignment.stretch,
                            children: [
                              TextFormField(
                                initialValue: "Test Payments",
                                keyboardType: TextInputType.text,
                                decoration:
                                InputDecoration(labelText: 'Name'),
                                // ignore: missing_return
                                validator: (value) {
                                  if (value.isEmpty) {
                                    return 'Please enter the name';
                                  }
                                },
                                onSaved: (val) =>
                                    setState(() => _data.name = val),
                              ),
                              TextFormField(
                                initialValue: "test@test.com",
                                  keyboardType: TextInputType.emailAddress,
                                  decoration:
                                  InputDecoration(labelText: 'Email Id'),
                                  // ignore: missing_return
                                  validator: validateEmail,
                                  onSaved: (val) =>
                                      setState(() => _data.email = val)),

                              TextFormField(
                                initialValue: "1234567890",
                                  keyboardType: TextInputType.phone,
                                  maxLength: 10,
                                  decoration:
                                  InputDecoration(labelText: 'Mobile Number'),
                                  // ignore: missing_return
                                  validator: (value) {
                                    if (value.isEmpty) {
                                      return 'Please enter the phone number.';
                                    }else if(value.length < 10){
                                      return "Please enter a valid phone number";
                                    }
                                  },
                                  onSaved: (val) =>
                                      setState(() => _data.number = val)),
                              TextFormField(
                                initialValue: "33",
                                  keyboardType: TextInputType.number,
                                  maxLength: 4,
                                  decoration:
                                  InputDecoration(labelText: 'Amount'),
                                  // ignore: missing_return
                                  validator: (value) {
                                    if (value.isEmpty) {
                                      return 'Please enter the amount.';
                                    }
                                  },
                                  onSaved: (val) =>
                                      setState(() => _data.amount = val)),
                              TextFormField(
                                initialValue: "test description",
                                  keyboardType: TextInputType.text,
                                  decoration:
                                  InputDecoration(labelText: 'Description'),
                                  onSaved: (val) =>
                                      setState(() => _data.description = val)),
                              SwitchListTile(
                                  title: Text(_data.isLive ? 'Live Account' : 'Test Account'),
                                  value: _data.isLive,
                                  onChanged: (bool val) =>
                                      setState(() => _data.isLive = val)),
                               Container(
                                 height: 50,
                                 child: RaisedButton(

                                        onPressed: () {
                                          final form = _formKey.currentState;
                                          if (form.validate()) {
                                            form.save();
                                            initPlatformState();
                                          }else{
                                            _autoValidate = true;
                                          }
                                        },
                                        child: apiCalled ? animation()
                                            :  Text('Make Payment')),
                               ),
                            ])))),
            SizedBox(
              height: 20,
            ),
            Text("Response: $_paymentResponse"),
            SizedBox(
              height: 30,
            ),
          ],
        )),
    );
  }

  Widget animation() {
    return AnimatedBuilder(
      animation: CurvedAnimation(parent: _controller, curve: Curves.fastOutSlowIn),
      builder: (context, child) {
        return Stack(
          alignment: Alignment.center,
          children: <Widget>[
            _buildContainer(15 * _controller.value),
            _buildContainer(20 * _controller.value),
            _buildContainer(25 * _controller.value),
            _buildContainer(30 * _controller.value),
            _buildContainer(35 * _controller.value),
          ],
        );
      },
    );
  }
  Widget _buildContainer(double radius) {
    return Container(
      width: radius,
      height: radius,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        color: Colors.amber.withOpacity(1 - _controller.value),
      ),
    );
  }

  String validateEmail(String value) {
    Pattern pattern =
        r'^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$';
    RegExp regex = new RegExp(pattern);
    if (!regex.hasMatch(value))
      return 'Enter Valid Email';
    else
      return null;
  }
}
