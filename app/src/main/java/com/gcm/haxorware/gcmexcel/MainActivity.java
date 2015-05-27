package com.gcm.haxorware.gcmexcel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gcm.haxorware.gcmexcel.Facebook.MainFragment;
import com.gcm.haxorware.gcmexcel.HistoryDatabase.MainHistory;
import com.gcm.haxorware.gcmexcel.PushNotifications.GCM;
import com.gcm.haxorware.gcmexcel.Subscription.MainSubscription;
import com.google.android.gms.gcm.GoogleCloudMessaging;


public class MainActivity extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener {
    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";
    private static ProgressDialog pd;
    public static SharedPreferences preferences;
    public static Context context;
    public static String sim_operator_Name=null;
    public static String droid_version=null;
    public static String phone_no=null;
    public static String phone_dpi=null;
    public static String phone_manuf=null;
    public static String phone_model=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        sim_operator_Name=telephonyManager.getSimOperatorName();
        droid_version = android.os.Build.VERSION.RELEASE;
        phone_no=telephonyManager.getLine1Number();
        phone_dpi=metrics.densityDpi+"dp";
        phone_manuf= Build.MANUFACTURER;
        phone_model=android.os.Build.PRODUCT;

        if(sim_operator_Name==null){sim_operator_Name="null";}
        if(phone_no==null){phone_no="null";}
        if(phone_manuf==null){phone_manuf="null";}
        if(phone_model==null){phone_model="null";}

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        homeAsUpByBackStack();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new MainFragment()).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackStackChanged() {
        homeAsUpByBackStack();
    }

    private void homeAsUpByBackStack() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount > 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void showProgress(String message) {
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(message);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    public static void hideProgress() {
        pd.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void view_prev_msgs(View view) {
        Intent intent = new Intent(this, MainHistory.class);
        startActivity(intent);
    }

    public void subscribe_channel(View view) {
        Intent intent = new Intent(this, MainSubscription.class);
        startActivity(intent);
    }

}