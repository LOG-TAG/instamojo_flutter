package com.kushal.instamojo_flutter.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.kushal.instamojo_flutter.R;
import com.kushal.instamojo_flutter.helpers.Logger;

import java.lang.reflect.Method;

import io.flutter.embedding.android.FlutterActivity;

/**
 * Base Activity to all the Activities in the SDK.
 * Implements Calligraphy using reflection. The default font will be applied through context wrappers
 * as defined in the Calligraphy Documentation. Any newly created activity must extend this BaseActivity
 * for the proper application of the default font.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();
    public static String actionBarColor = "";
    public static String actionBarTextColor = "";

    @SuppressWarnings("unchecked")
    @Override
    protected void attachBaseContext(Context context) {
        Class calligraphyClass = getCalligraphyClass();
        if (calligraphyClass != null) {
            try {
                Method method = calligraphyClass.getMethod("wrap", Context.class);
                super.attachBaseContext((Context) method.invoke(calligraphyClass, context));
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.attachBaseContext(context);
    }

    private Class getCalligraphyClass() {
        try {
            return Class.forName("uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * returnResult is used to pass back the appropriate result to the initiator activity that
     * started this activity for result. The method will call {@link Activity#finish()} and the
     * current activity will be stopped.
     *
     * @param bundle if any extra params that need to be passed back.
     * @param result Appropriate Activity result to be sent back to caller Activity - {@link Activity#RESULT_OK}
     *               on Success or {@link Activity#RESULT_CANCELED} on Failure.
     */

    public void returnResult(Bundle bundle, int result) {
        Logger.d(TAG, "Returning back the result received");
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        setResult(result, intent);
        finish();
    }

    /**
     * Overloading method for {@link BaseActivity#returnResult(Bundle, int)} with null Bundle.
     *
     * @param result Appropriate Activity result to be sent back to caller Activity - {@link Activity#RESULT_OK}
     *               on Success or {@link Activity#RESULT_CANCELED} on Failure.
     */

    public void returnResult(int result) {
        returnResult(null, result);
    }

    /**
     * Hides the Soft keyboard if activated.
     */

    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Update the toolbar accordingly
     */
    protected void updateActionBar() {
        if (getSupportActionBar() == null) {
            return;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        if(BaseActivity.actionBarColor != "") {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(BaseActivity.actionBarColor)));
            int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
            if(BaseActivity.actionBarTextColor != "") {
                Spannable text = new SpannableString(getSupportActionBar().getTitle());
                text.setSpan(new ForegroundColorSpan(Color.parseColor(BaseActivity.actionBarTextColor)), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                getSupportActionBar().setTitle(text);
                Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
                upArrow.setColorFilter(Color.parseColor(BaseActivity.actionBarTextColor), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor(BaseActivity.actionBarColor));
            }
        }
    }

    /**
     * Update the actionbar of the associated activity
     *
     * @param title - title to be set to current activity's actionbar
     */
    public void updateActionBarTitle(@StringRes int title) {
        if (getActionBar() == null) {
            return;
        }
        Logger.d(TAG, "Setting title for Toolbar");
        getSupportActionBar().setTitle(title);
    }
}
