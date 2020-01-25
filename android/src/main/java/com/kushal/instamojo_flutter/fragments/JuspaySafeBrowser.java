package com.kushal.instamojo_flutter.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import com.kushal.instamojo_flutter.activities.PaymentActivity;
import com.kushal.instamojo_flutter.helpers.Logger;
import com.kushal.instamojo_flutter.network.JavaScriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import in.juspay.godel.ui.JuspayBrowserFragment;


/**
 */
public class JuspaySafeBrowser extends JuspayBrowserFragment {

    private static final String TAG = JuspaySafeBrowser.class.getSimpleName();

    /**
     * Creates new instance of Fragment.
     */
    public JuspaySafeBrowser() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadWebView();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void loadWebView() {
        setupJuspayBackButtonCallbackInterface(new JuspayBackButtonCallback() {
            @Override
            public void transactionCancelled(JSONObject jsonObject) throws JSONException {
                ((PaymentActivity) getActivity()).returnResult(Activity.RESULT_CANCELED);
            }
        });
        getWebView().addJavascriptInterface(new JavaScriptInterface(getActivity()), "AndroidScriptInterface");
        getWebView().getSettings().setJavaScriptEnabled(true);
        Logger.d(TAG, "Loaded Webview");
    }
}
