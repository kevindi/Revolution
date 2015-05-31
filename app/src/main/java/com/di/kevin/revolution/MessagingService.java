package com.di.kevin.revolution;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import java.util.ArrayList;

public class MessagingService extends Service implements ConnectedToServerListener, LoginStateListener {
    private ConnectedToServerListener connectedToServerListener;
    private LoginStateListener loginStartListener;
    private XMPP xmpp;
    
    public MessagingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private final IBinder mBinder = new MessagingBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        xmpp = new XMPP("192.168.1.7");
        xmpp.connectToServer();
        xmpp.setConnectedToServerListener(this);
    }

    public void login(String username, String password) {
        xmpp.setLoginStateListener(this);
        xmpp.login(username, password);
    }

    public boolean isConnectedToServer() {
        return xmpp.isConnectedToServer();
    }

    public ArrayList<String> getRoster() {
        return xmpp.getRoster();
    }

    public void setConnectedToServerListener(ConnectedToServerListener connectedToServerListener) {
        this.connectedToServerListener = connectedToServerListener;
    }

    public void setLoginStateListener(LoginStateListener loginStartListener) {
        this.loginStartListener = loginStartListener;
    }

//    public void setOnRoasterUpdatedListener(RosterUpdatedListener roasterUpdatedListener) {
//        this.roasterUpdatedListener = roasterUpdatedListener;
//    }

    @Override
    public void onConnectedToServer(boolean result) {
        if (this.connectedToServerListener != null) {
            this.connectedToServerListener.onConnectedToServer(result);
        }
    }

    @Override
    public void onLogin(boolean result) {
        if (this.loginStartListener != null) {
            this.loginStartListener.onLogin(result);
        }
    }

//    @Override
//    public void onRoasterUpdated() {
//        if (this.roasterUpdatedListener != null) {
//            this.roasterUpdatedListener.onRoasterUpdated();
//        }
//    }

    public class MessagingBinder extends Binder {
        public MessagingService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MessagingService.this;
        }
    }
}
