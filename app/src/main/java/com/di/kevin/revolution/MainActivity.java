package com.di.kevin.revolution;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ListView lvRoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvRoster = (ListView)findViewById(R.id.lv_roster);
        lvRoster.setAdapter(rosterAdapter);

        lvRoster.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, MessagingService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
    }

    private MessagingService mService;
    private boolean mBound;
    private ArrayList<String> rosterEntries;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.d(TAG, "onServiceConnected ComponentName: " + className.toString() + " service: " + service.toString());

            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MessagingService.MessagingBinder binder = (MessagingService.MessagingBinder) service;
            mService = binder.getService();
            mBound = true;

            rosterEntries = mService.getRoster();
            lvRoster.invalidate();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            Log.d(TAG, "onServiceDisconnected ComponentName: " + arg0.toString());
            mBound = false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    BaseAdapter rosterAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if (rosterEntries != null) {
                return rosterEntries.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.roster_item, null);
            }

            ((TextView)view.findViewById(R.id.tv_roster_item)).setText(rosterEntries.get(position));


            return view;
        }
    };
}
