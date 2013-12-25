package com.higgsbot.wifidirect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class NetworkService extends Service {
	public static final String TAG = "NetworkService";
	 
    int mStartMode = START_STICKY;       // indicates how to behave if the service is killed
    IBinder mBinder = null;      		 // interface for clients that bind
    boolean mAllowRebind = false; 	     // indicates whether onRebind should be used
    //private ServerSocket mServerSocket;
    Thread mServerThread = null;

    
    @Override
    public void onCreate() {
        // The service is being created
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "The service is starting");
    	
        // The intent can be null because this function START_STICKY and the intent sended is not sent back as is.
        if (intent == null) {
        	// ...
    	} else {
	        // The service is starting, due to a call to startService()
	    	int port = intent.getIntExtra("port", -1);
	    	
	    	
	    	if (port == -1) {
	    		Log.d(TAG, "error port");
	    		stopSelf();
	    	} else {
	    		this.mServerThread = new Thread(new ServerThread(port));
	        	this.mServerThread.start();
	    	}
    	}
    	
        return mStartMode;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return mAllowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    
    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
    }
    
    class ServerThread implements Runnable {
    	
    	private int mPort;
    	
    	public ServerThread(int port) {
			this.mPort = port;
		}
    	
		@Override
		public void run() {
			ServerSocket serverSocket = null;
			Socket socket = null;
			try {
				serverSocket = new ServerSocket(this.mPort);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Thread t = null;
			while (! Thread.currentThread().isInterrupted()) {
				try {
					socket = serverSocket.accept();
					Log.d("", "Server: connection done");
					CommunicationThread commThread = new CommunicationThread(socket);
					if (t != null) {
						t.interrupt();
					}
					t = new Thread(commThread);
					t.start();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				serverSocket.close();
				Log.d(TAG, "Socket closed");
			} catch (IOException e) {
				e.printStackTrace();
			}
 		}
    }
    
    class CommunicationThread implements Runnable {

		private Socket clientSocket;
		private BufferedReader input;

		public CommunicationThread(Socket clientSocket) {

			this.clientSocket = clientSocket;
			
			try {
				this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			
			try {
				String read;
				while (! Thread.currentThread().isInterrupted()) {
					read = input.readLine();
					yourTurnItamar(read);
				}
				
				

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void yourTurnItamar(String message) {
			/* Call here to android audio */
			if (message == null) {
				Log.d("NetworkService", "toAudio: null message");
			} else {
				Log.d("NetworkService", "toAudio: " + message);
			}
			
		}
	}
}