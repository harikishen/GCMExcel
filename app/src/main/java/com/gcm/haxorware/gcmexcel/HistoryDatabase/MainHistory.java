package com.gcm.haxorware.gcmexcel.HistoryDatabase;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.gcm.haxorware.gcmexcel.R;

/**
 * Created by haxorware on 27/5/15.
 */
public class MainHistory extends Activity {
    String  history;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stc
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toast.makeText(getApplicationContext(), "History",
                Toast.LENGTH_LONG).show();
        DatabaseHelper databaseHelper=new DatabaseHelper(getApplicationContext());
        Cursor AllFriends = databaseHelper.getFriends();

        AllFriends.moveToFirst();

        while (!AllFriends.isAfterLast()) {
            String id= AllFriends.getString(0);
            String Name = AllFriends.getString(1);
            AllFriends.moveToNext();
            history=history+id+"."+Name+"\n";
            Log.w("test", Name + id);

        }
        TextView body = (TextView)findViewById(R.id.textViewhistory);
        body.setText(history);
        // setContentView(R.layout.history);

    }
}