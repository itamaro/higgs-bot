package com.higgsbot.robodrive;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Spinner;

public class StageConfigActivity extends Activity {
	public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
	public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
	private Socket socket;
	private String mHost;
	private int mPort;
	private PrintWriter mPrintWriter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stage_config);

		// Get the host and the port
	    Intent intent = getIntent();
	    mHost = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
    	mPort = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
    	
    	new Thread(new ClientThread()).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stage_config, menu);
		return true;
	}

    public void playTestSignal(View view) {
		Log.d("StageConfig", "Sending test sound");
    	mPrintWriter.println("T");
    }

    public void startHiggs(View view) {
    	String snitchColor = ((Spinner) findViewById(R.id.snitchColor)).getSelectedItem().toString();
    	if ("Red".equals(snitchColor)) {
    		Log.d("StageConfig", "Start with red snitch");
    		mPrintWriter.println("S0");
    	} else {
    		Log.d("StageConfig", "Start with black snitch");
    		mPrintWriter.println("S1");
    	}
    }

	class ClientThread implements Runnable {
		@Override
		public void run() {

			try {
				socket = new Socket();
				socket.bind(null);
	            socket.connect((new InetSocketAddress(mHost, mPort)), 5000);
	    		mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
