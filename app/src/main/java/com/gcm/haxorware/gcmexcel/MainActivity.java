package com.gcm.haxorware.gcmexcel;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gcm.haxorware.gcmexcel.Facebook.MainFragment;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener {
    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";
    private static ProgressDialog pd;
    public static SharedPreferences preferences;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    static final String DISPLAY_MESSAGE_ACTION="com.example.test.DISPLAY_MESSAGE";;
    static final String SERVER_URL = "http://doylefermi.x2y2.net/default.php";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static String acc = "";
    public static String msg = "";
    public static String accn = "";
    static String SENDER_ID = "1019787135827";
    static final String TAG = "GCMDemo";
    static GoogleCloudMessaging gcm;
    static Context context;
    static AtomicInteger msgId = new AtomicInteger();
    static SharedPreferences prefs;
    static String regid="";
    static String email="";
    static String title="";
    static String name="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
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
    public static void gcmcheck()
    {



        Account[] accounts = AccountManager.get(context).getAccounts();

        acc= accounts[0].name;
        accn=acc.substring(0, acc.indexOf('@'));
        name=accn;
        email=acc;
        gcm = GoogleCloudMessaging.getInstance(context);
        regid = getRegistrationId(context);

        if (regid=="") {    Toast.makeText(context, "Registering device...", Toast.LENGTH_SHORT).show();
            registerInBackground();
        }

    }
    private static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        return registrationId;
    }
    private static SharedPreferences getGCMPreferences(Context context) {
        return preferences;
    }
    private static void registerInBackground() {

        new AsyncTask<Void,Void,String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (gcm == null) {

                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    regid = gcm.register(SENDER_ID);
                    Log.d("TEST",regid);

                    msg = "Device registered, registration ID=" + regid;

                    sendRegistrationIdToBackend();

                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            private void sendRegistrationIdToBackend() {
                final int MAX_ATTEMPTS = 5;
                final int BACKOFF_MILLI_SECONDS = 2000;
                final Random random = new Random();
                Log.i(TAG, "registering device (regId = " + regid + ")");
                String serverUrl = SERVER_URL;
                Map<String, String> params = new HashMap<String, String>();
                params.put("regId", regid);
                Log.d("fbid",MainActivity.preferences.getString("id",""));
                params.put("name",MainActivity.preferences.getString("id",""));
                params.put("email",email);

                long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
                for (int i = 1; i <= MAX_ATTEMPTS; i++) {
                    Log.d(TAG, "Attempt #" + i + " to register");
                    try {
                        post(serverUrl, params);
                        return;
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                        if (i == MAX_ATTEMPTS) {
                            break;
                        }
                        try {
                            Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                            Thread.sleep(backoff);
                        } catch (InterruptedException e1) {
                            Log.d(TAG, "Thread interrupted: abort remaining retries!");
                            Thread.currentThread().interrupt();
                            return;
                        }
                        backoff *= 2;
                    }
                }
            }
            private  void post(String endpoint, Map<String, String> params)throws IOException{
                URL url;
                try {
                    url = new URL(endpoint);
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException("invalid url: " + endpoint);
                }
                StringBuilder bodyBuilder = new StringBuilder();
                Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> param = iterator.next();
                    bodyBuilder.append(param.getKey()).append('=')
                            .append(param.getValue());
                    if (iterator.hasNext()) {
                        bodyBuilder.append('&');
                    }
                }
                String body = bodyBuilder.toString();
                Log.v(TAG, "Posting '" + body+ "' to " + url);
                byte[] bytes = body.getBytes();
                HttpURLConnection conn = null;
                try {
                    Log.e("URL", "> " + url);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setFixedLengthStreamingMode(bytes.length);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded;charset=UTF-8");
                    // post the request
                    OutputStream out = conn.getOutputStream();
                    out.write(bytes);
                    out.close();
                    // handle the response
                    int status = conn.getResponseCode();
                    if (status != 200) {
                        throw new IOException("Post failed with error code " + status);
                    }
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }

            protected void onPostExecute(String msg) {
            }


        }.execute();}

    private static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}