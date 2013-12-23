package com.higgsbot.wifidirect;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
* A service that process each file transfer request i.e Intent by opening a
* socket connection with the WiFi Direct Group Owner and writing the file
*/
public class DataTransferService extends IntentService {
	
	private static final int SOCKET_TIMEOUT = 5000;
	public static final String ACTION_SEND_DATA = "com.example.android.wifidirect.SEND_DATA";
	public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
	public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
	public static final String EXTRAS_GROUP_DATA_TO_SEND = "go_higgs";

	private Socket mSocket = null;
 
	public DataTransferService(String name) {
	    super(name);
	}
	
	public DataTransferService() {
	    super("DataTransferService");
	}
	
	public void onDestroy () {
		this.closeSocket();
		super.onDestroy();
	}
	 
	public void closeSocket() {
		if (mSocket != null) {
	        if (mSocket.isConnected()) {
	            try {
	                mSocket.close();
	            } catch (IOException e) {
	                // Give up
	                e.printStackTrace();
	            }
	        }
	    }
	}
	/*
	 * (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
	
		Context context = getApplicationContext();
     
		if (intent.getAction().equals(ACTION_SEND_DATA)) {
    	 
        String dataToSend = intent.getExtras().getString(EXTRAS_GROUP_DATA_TO_SEND);
         
        if (mSocket == null) {
        	String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
        	int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
        	Log.d(WiFiDirectActivity.TAG, "Opening client socket - ");
        	mSocket = new Socket();
        	
        	try {
        		mSocket.bind(null);
        		mSocket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
        	} catch (IOException e) {
        		e.printStackTrace();
        	}

        	Log.d(WiFiDirectActivity.TAG, "Client socket - " + mSocket.isConnected());
        }

        try {
        	OutputStream stream = mSocket.getOutputStream();
            stream.write(dataToSend.getBytes());
            
            Toast.makeText(context, dataToSend, Toast.LENGTH_SHORT).show();
            Toast.makeText(context, dataToSend.getBytes().toString(), Toast.LENGTH_SHORT).show();
            
        } catch (IOException e) {
             Log.e(WiFiDirectActivity.TAG, e.getMessage());
             this.closeSocket();
        	}
		}
	}
}
