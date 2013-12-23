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
 
	public DataTransferService(String name) {
	    super(name);
	}
	
	public DataTransferService() {
	    super("DataTransferService");
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
	
		Context context = getApplicationContext();
		Log.d(WiFiDirectActivity.TAG, "service start");
        if (intent.getAction().equals(ACTION_SEND_DATA)) {
        	String dataToSend = intent.getExtras().getString(EXTRAS_GROUP_DATA_TO_SEND);
            
        	String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

            try {
                Log.d(WiFiDirectActivity.TAG, "Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                Log.d(WiFiDirectActivity.TAG, "Client socket - " + socket.isConnected());
                OutputStream stream = socket.getOutputStream();
                stream.write(dataToSend.getBytes());
                
                Toast.makeText(context, dataToSend, Toast.LENGTH_SHORT).show();
                Toast.makeText(context, dataToSend.getBytes().toString(), Toast.LENGTH_SHORT).show();
                
                Log.d(WiFiDirectActivity.TAG, "Client: Data written");
            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
	}
}
