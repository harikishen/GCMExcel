package com.gcm.haxorware.gcmexcel.Subscription;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gcm.haxorware.gcmexcel.MainActivity;
import com.gcm.haxorware.gcmexcel.R;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * Created by haxorware on 27/5/15.
 */
public class MainSubscription extends Activity implements AdapterView.OnItemClickListener {
    String regid,msg;
    String TAG="Channel Subscription";
    String SERVER_URL="http://exceltest.comuv.com/subscribe.php";
    //String SERVER_URL="http://doylefermi.x2y2.net/default.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_channels);

        ListView listView = (ListView) findViewById(R.id.channels);
        listView.setOnItemClickListener(this);

    }



    @Override
    public void onItemClick(AdapterView<?> adapter, View view,
                            int position, long id) {
        Toast.makeText(getApplicationContext(), "Subscribing to "+((TextView) view).getText(),
                Toast.LENGTH_SHORT).show();
        String subscr=""+((TextView) view).getText();
        regid = MainActivity.preferences.getString("id", "");
        if(regid=="") {
            Toast.makeText(getApplicationContext(), "Unregistered Device. Login via FB",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            registerInBackground(regid, subscr);
        }
    }

    private void registerInBackground(final String devid, final String subscr) {
        new AsyncTask<Void,Void,String>() {
            @Override
            protected String doInBackground(Void... params) {
                msg = "Subscribed " +subscr+" with ID=" +devid;
                sendRegistrationIdToBackend(devid,subscr);
                return msg;


            }

            private void sendRegistrationIdToBackend(String devid, String subscr) {
                final int MAX_ATTEMPTS = 5;
                final int BACKOFF_MILLI_SECONDS = 2000;
                final Random random = new Random();
                String serverUrl = SERVER_URL;
                Map<String, String> params = new HashMap<String, String>();
                params.put("fb_id", devid);
                params.put("event_id", subscr);

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
                    OutputStream out = conn.getOutputStream();
                    out.write(bytes);
                    out.close();
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
        }.execute(null, null, null);}








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_display_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       /* int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}