package com.di.kevin.revolution;

import android.os.AsyncTask;
import android.util.Log;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by dike on 27/5/2015.
 */
public class XMPP{
    private String serverAddress;
    private XMPPTCPConnection connection;
    private final static String TAG = XMPP.class.getSimpleName();
    private ConnectedToServerListener connectedToServerListener;
    private LoginStateListener loginStateListener;

    public XMPP(String serverAddress){
        this.serverAddress = serverAddress;
    }

    public boolean isConnectedToServer() {
        if (connection != null) {
            return connection.isConnected();
        } else {
            return false;
        }
    }

    public ArrayList<String> getRoster() {
        if (isConnectedToServer()) {
            Roster roster = Roster.getInstanceFor(connection);

            ArrayList<String> rosterEntries = new ArrayList<>();

            for (RosterEntry rosterEntry : roster.getEntries()) {
                rosterEntries.add(rosterEntry.getName());
            }

            return rosterEntries;
        } else {
            return null;
        }

//        final AsyncTask<Void, Void, Void> rosterThread = new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//
//
//
//
//                return null;
//            }
//        };
    }

    public void setConnectedToServerListener(ConnectedToServerListener connectedToServerListener) {
        this.connectedToServerListener = connectedToServerListener;
    }

    public void setLoginStateListener(LoginStateListener loginStateListener) {
        this.loginStateListener = loginStateListener;
    }

    public void connectToServer(){
        final AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... arg0){
                XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
                configBuilder.setServiceName(serverAddress);
                configBuilder.setCompressionEnabled(false);
                configBuilder.setDebuggerEnabled(true);
                configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

                connection = new XMPPTCPConnection(configBuilder.build());
                boolean isConnected = false;
                // Connect to the server
                try {
                    connection.connect();
                    isConnected = true;
                } catch (SmackException e) {
                    Log.d(TAG, "SmackException: " + e.toString());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d(TAG, "IOException: " + e.toString());
                    e.printStackTrace();
                } catch (XMPPException e) {
                    Log.d(TAG, "XMPPException: " + e.toString());
                    e.printStackTrace();
                }

                Log.d(TAG, "isConnected: " + isConnected);
                return  isConnected;
            }

            @Override
            protected void onPostExecute(Boolean isConnected) {
                Log.d(TAG, "isConnected: " + isConnected);

                if (connectedToServerListener != null) {
                    connectedToServerListener.onConnectedToServer(isConnected);
                }

                super.onPostExecute(isConnected);
            }
        };
        connectionThread.execute();
    }

    public void login(final String username, final String password) {

        final AsyncTask<Void, Void, Boolean> loginThread = new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... arg0){

                boolean isLoggedIn = false;
                // Log into the server
                try {
                    SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
                    SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
                    if (connection != null) {
                        connection.login(username, password);
                    }
                    isLoggedIn = true;
                } catch (XMPPException e) {
                    Log.d(TAG, "XMPPException" + e.toString());
                    e.printStackTrace();
                } catch (SmackException e) {
                    Log.d(TAG, "SmackException" + e.toString());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d(TAG, "IOException" + e.toString());
                    e.printStackTrace();
                }
                return  isLoggedIn;
            }

            @Override
            protected void onPostExecute(Boolean isLoggedIn) {

                if (loginStateListener != null) {
                    loginStateListener.onLogin(isLoggedIn);
                }


                Log.d(TAG, "success: " + isLoggedIn);
                super.onPostExecute(isLoggedIn);
            }
        };
        loginThread.execute();

    }


}