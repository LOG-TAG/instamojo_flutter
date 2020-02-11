package com.kushal.instamojo_flutter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SwitchCompat;

import com.kushal.instamojo_flutter.helpers.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements Instamojo.InstamojoPaymentCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final HashMap<Instamojo.Environment, String> env_options = new HashMap<>();

    static {
        env_options.put(Instamojo.Environment.TEST, "https://test.instamojo.com/");
        env_options.put(Instamojo.Environment.PRODUCTION, "https://api.instamojo.com/");
    }

    private AlertDialog dialog;
    private AppCompatEditText nameBox, emailBox, phoneBox, amountBox, descriptionBox;
    private Instamojo.Environment mCurrentEnv = Instamojo.Environment.TEST;
    private boolean mCustomUIFlow = false;

    private MyBackendService myBackendService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.pay);
        nameBox = findViewById(R.id.name);
        emailBox = findViewById(R.id.email);
        phoneBox = findViewById(R.id.phone);
        amountBox = findViewById(R.id.amount);
        descriptionBox = findViewById(R.id.description);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog);
        dialog = builder.create();

        // Initialize the backend service client
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://206.189.138.85:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myBackendService = retrofit.create(MyBackendService.class);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOrderOnServer();
            }
        });
    }

    private void createOrderOnServer() {
        GetOrderIDRequest request = new GetOrderIDRequest();
        request.setEnv(mCurrentEnv.name());
        request.setBuyerName(nameBox.getText().toString());
        request.setBuyerEmail(emailBox.getText().toString());
        request.setBuyerPhone(phoneBox.getText().toString());
        request.setDescription(descriptionBox.getText().toString());
        request.setAmount(amountBox.getText().toString());

        Call<GetOrderIDResponse> getOrderIDCall = myBackendService.createOrder(request);
        getOrderIDCall.enqueue(new retrofit2.Callback<GetOrderIDResponse>() {
            @Override
            public void onResponse(Call<GetOrderIDResponse> call, Response<GetOrderIDResponse> response) {
                if (response.isSuccessful()) {
                    String orderId = response.body().getOrderID();

//                    if (!mCustomUIFlow) {
                        // Initiate the default SDK-provided payment activity
                        initiateSDKPayment(orderId);

//                    } else {
//                        // OR initiate a custom UI activity
//                        initiateCustomPayment(orderId);
//                    }

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
        Instamojo.getInstance().initiatePayment(this, orderID, this);
    }


    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
        });
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

}
