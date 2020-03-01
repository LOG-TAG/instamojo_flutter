package com.kushal.instamojo_flutter.activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.kushal.instamojo_flutter.R;
import com.kushal.instamojo_flutter.fragments.JuspaySafeBrowser;
import com.kushal.instamojo_flutter.helpers.Constants;
import com.kushal.instamojo_flutter.helpers.Logger;

import in.juspay.godel.browser.JuspayWebViewClient;
import in.juspay.godel.ui.JuspayBrowserFragment;
import in.juspay.godel.ui.JuspayWebView;

/**
 * Activity subclass extending {@link BaseActivity}. Activity for {@link JuspaySafeBrowser} fragment.
 */
public class PaymentActivity extends BaseActivity {

    private static final String TAG = PaymentActivity.class.getSimpleName();
    private JuspayBrowserFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_instamojo);
        inflateXML();
        showFragment();
    }

    private void inflateXML() {
        updateActionBar();
//        setSupportActionBar(toolbar);
        Logger.d(TAG, "Inflated XML");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFragment() {
        final Bundle sourceArgs = getIntent().getBundleExtra(Constants.PAYMENT_BUNDLE);
        if (sourceArgs == null) {
            Logger.e(TAG, "Payment bundle is Null");
            returnResult(RESULT_CANCELED);
            return;
        }
        currentFragment = (JuspayBrowserFragment) getSupportFragmentManager().findFragmentById(R.id.juspay_browser_fragment);
        JuspayBrowserFragment.JuspayWebviewCallback juspayWebViewCallback = new JuspayBrowserFragment.JuspayWebviewCallback() {
            public void webviewReady() {
                JuspayWebView juspayWebView = currentFragment.getWebView();
                juspayWebView.setWebViewClient(new JuspayWebViewClient(juspayWebView, currentFragment));
                currentFragment.startPaymentWithArguments(sourceArgs);
            }
        };
        currentFragment.setupJuspayWebviewCallbackInterface(juspayWebViewCallback);
        Logger.d(TAG, "Loaded Fragment - " + currentFragment.getClass().getSimpleName());
    }

    @Override
    public void onBackPressed() {
        Logger.d(TAG, "Invoking Juspay Cancel Payment Handler");
        currentFragment.juspayBackPressedHandler(true);
    }
}
