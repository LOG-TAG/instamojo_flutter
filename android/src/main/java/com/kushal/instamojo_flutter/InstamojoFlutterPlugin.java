package com.kushal.instamojo_flutter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.kushal.instamojo_flutter.helpers.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * InstamojoFlutterPlugin
 */
public class InstamojoFlutterPlugin extends Activity implements FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler, Instamojo.InstamojoPaymentCallback {
    public static String serverUrl = "https://instamojoflutter.herokuapp.com/access_token.php/";
    private Context applicationContext;
    private MethodChannel methodChannel;
    private EventChannel eventChannel;
    private MyBackendService myBackendService;
    private AlertDialog dialog;
    private Instamojo.Environment mCurrentEnv = Instamojo.Environment.TEST;
    private Result finalResult;


    private static final String TAG = MainActivity.class.getSimpleName();
    private static final HashMap<Instamojo.Environment, String> env_options = new HashMap<>();

    static {
        env_options.put(Instamojo.Environment.TEST, "https://test.instamojo.com/");
        env_options.put(Instamojo.Environment.PRODUCTION, "https://api.instamojo.com/");
    }
    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        onAttachedToEngine(flutterPluginBinding.getApplicationContext(), flutterPluginBinding.getBinaryMessenger());
    }

    private void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger) {
        this.applicationContext = applicationContext;
        methodChannel = new MethodChannel(messenger, "instamojo_flutter");
        eventChannel = new EventChannel(messenger, "plugins.flutter.io/charging");
        eventChannel.setStreamHandler(this);
        methodChannel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        applicationContext = null;
        methodChannel.setMethodCallHandler(null);
        methodChannel = null;
        eventChannel.setStreamHandler(null);
        eventChannel = null;
    }


    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class.
    public static void registerWith(Registrar registrar) {
        final InstamojoFlutterPlugin instance = new InstamojoFlutterPlugin();
        instance.onAttachedToEngine(registrar.context(), registrar.messenger());
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        finalResult = result;
        if (call.method.equals("getPlatformVersixon")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("createOrder")) {
            Instamojo.getInstance().initialize(applicationContext,((boolean) call.argument("isProduction")) ? Instamojo.Environment.PRODUCTION : Instamojo.Environment.TEST);

            // Initialize the backend service client
            String baseUrl =  call.argument("baseUrl").toString();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            myBackendService = retrofit.create(MyBackendService.class);

            createOrderOnServer(call, result);

        } else if(call.method.endsWith("startPayment")){
            initiateSDKPayment(call.argument("orderId").toString());
        }
        else {
            result.notImplemented();

        }
    }
    private void createOrderOnServer(MethodCall call, final Result result) {
        GetOrderIDRequest request = new GetOrderIDRequest();
        request.setEnv(mCurrentEnv.name());
        request.setBuyerName(call.argument("name").toString());
        request.setBuyerEmail(call.argument("email").toString());
        request.setBuyerPhone(call.argument("mobileNumber").toString());
        request.setDescription(call.argument("description").toString());
        request.setAmount(call.argument("amount").toString());

        Call<GetOrderIDResponse> getOrderIDCall = myBackendService.createOrder(request);
        getOrderIDCall.enqueue(new retrofit2.Callback<GetOrderIDResponse>() {
            @Override
            public void onResponse(Call<GetOrderIDResponse> call, Response<GetOrderIDResponse> response) {
                if (response.isSuccessful()) {
                    String orderId = response.body().getOrderID();
                    finalResult.success(orderId);


                } else {
                    // Handle api errors
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.d(TAG, "Error in response" + jObjError.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    result.error("500"," Unable to create Order",null);
                }
            }

            @Override
            public void onFailure(Call<GetOrderIDResponse> call, Throwable t) {
                // Handle call failure
                Log.d(TAG, "Failure");
            }
        });
    }

    private void initiateSDKPayment(String orderID) {
        Instamojo.getInstance().initiatePayment(applicationContext, orderID, this);
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {

    }

    @Override
    public void onCancel(Object arguments) {

    }

    @Override
    public void onInstamojoPaymentComplete(String orderID, String transactionID, String paymentID, String paymentStatus) {
        Log.d(TAG, "Payment complete");
        showToast("Payment complete. Order ID: " + orderID + ", Transaction ID: " + transactionID
                + ", Payment ID:" + paymentID + ", Status: " + paymentStatus);
    }

    @Override
    public void onPaymentCancelled() {
        Log.d(TAG, "Payment cancelled");
        showToast("Payment cancelled by user");
    }

    @Override
    public void onInitiatePaymentFailure(String errorMessage) {
        Log.d(TAG, "Initiate payment failed");
        showToast("Initiating payment failed. Error: " + errorMessage);
    }

    private void showToast(final String message) {
        finalResult.success(message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE && data != null) {
            String orderID = data.getStringExtra(Constants.ORDER_ID);
            String transactionID = data.getStringExtra(Constants.TRANSACTION_ID);
            String paymentID = data.getStringExtra(Constants.PAYMENT_ID);

            // Check transactionID, orderID, and orderID for null before using them to check the Payment status.
            if (transactionID != null || paymentID != null) {
                checkPaymentStatus(transactionID, orderID);
            } else {
                showToast("Oops!! Payment was cancelled");
            }
        }
    }


    /**
     * Will check for the transaction status of a particular Transaction
     *
     * @param transactionID Unique identifier of a transaction ID
     */
    private void checkPaymentStatus(final String transactionID, final String orderID) {
        if (transactionID == null && orderID == null) {
            return;
        }

        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }

        showToast("Checking transaction status");
        Call<GatewayOrderStatus> getOrderStatusCall = myBackendService.orderStatus(mCurrentEnv.name().toLowerCase(),
                orderID, transactionID);
        getOrderStatusCall.enqueue(new retrofit2.Callback<GatewayOrderStatus>() {
            @Override
            public void onResponse(Call<GatewayOrderStatus> call, final Response<GatewayOrderStatus> response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (response.isSuccessful()) {
                    GatewayOrderStatus orderStatus = response.body();
                    if (orderStatus.getStatus().equalsIgnoreCase("successful")) {
                        showToast("Transaction still pending");
                        return;
                    }

                    showToast("Transaction successful for id - " + orderStatus.getPaymentID());
                    refundTheAmount(transactionID, orderStatus.getAmount());

                } else {
                    showToast("Error occurred while fetching transaction status");
                }
            }

            @Override
            public void onFailure(Call<GatewayOrderStatus> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        showToast("Failed to fetch the transaction status");
                    }
                });
            }
        });
    }

    /**
     * Will initiate a refund for a given transaction with given amount
     *
     * @param transactionID Unique identifier for the transaction
     * @param amount        amount to be refunded
     */
    private void refundTheAmount(String transactionID, String amount) {
        if (transactionID == null || amount == null) {
            return;
        }

        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }

        showToast("Initiating a refund for - " + amount);
        Call<ResponseBody> refundCall = myBackendService.refundAmount(
                mCurrentEnv.name().toLowerCase(),
                transactionID, amount);
        refundCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (response.isSuccessful()) {
                    showToast("Refund initiated successfully");

                } else {
                    showToast("Failed to initiate a refund");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                showToast("Failed to Initiate a refund");
            }
        });
    }
}
