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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Login extends Activity implements ConnectedToServerListener, LoginStateListener{

    private static final String TAG = Login.class.getSimpleName();
    private Button btnLogin;
    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText)findViewById(R.id.et_username);
        etPassword = (EditText)findViewById(R.id.et_password);

        btnLogin = (Button)findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBound) {
                    if (mService.isConnectedToServer()) {
                        String username = etUsername.getText().toString();
                        String password = etPassword.getText().toString();

                        mService.login(username, password);
                    } else {
                        Toast.makeText(Login.this, "Not connect to server", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        btnLogin.setEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, MessagingService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private MessagingService mService;
    private boolean mBound;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.d(TAG, "onServiceConnected ComponentName: " + className.toString() + " service: " + service.toString());

            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MessagingService.MessagingBinder binder = (MessagingService.MessagingBinder) service;
            mService = binder.getService();
            mService.setConnectedToServerListener(Login.this);
            mService.setLoginStateListener(Login.this);
            mBound = true;
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
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
    }

    @Override
    public void onConnectedToServer(boolean result) {
        btnLogin.setEnabled(true);
    }

    @Override
    public void onLogin(boolean result) {
        if (result) {
            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        } else {
            Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show();
        }
    }
}
