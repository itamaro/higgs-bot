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
import android.widget.TextView;
import android.widget.ToggleButton;

public class ArmControlActivity extends Activity {
	public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
	public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
	private Socket socket;
	private String mHost;
	private int mPort;
	private PrintWriter mPrintWriter;

	
	TextView txtDebugState;
	JoystickView armSpeedCtrl;
	int armSpeed;
	boolean knife;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.armcontrol);

		// Get the host and the port
	    Intent intent = getIntent();
	    mHost = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
    	mPort = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
    	
    	new Thread(new ClientThread()).start();

		txtDebugState = (TextView)findViewById(R.id.txtDebugState);

		armSpeedCtrl = (JoystickView)findViewById(R.id.armSpeedCtrl);
		armSpeedCtrl.setOnJostickMovedListener(_listener);
		
		armSpeed = 0;
		knife = false;
		
		//updateRemoteState();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.arm_control, menu);
		return true;
	}
	
	private void updateRemoteState() {
		txtDebugState.setText("Arm: " + Integer.toString(armSpeed) +
				", Knife: " + (knife ? "On" : "Off"));
		assert Math.abs(armSpeed) <= 7;
		// Send speeds over WiFi to RoboDroid
		char wifiMsg[] = "A??K?".toCharArray();
		wifiMsg[1] = (armSpeed >= 0 ? '+' : '-');
		wifiMsg[2] = Integer.toString(Math.abs(armSpeed)).charAt(0);
		wifiMsg[4] = (knife ? '+' : '-');
		Log.d("Arm", new String(wifiMsg));
		mPrintWriter.println(new String(wifiMsg));
	}

	private JoystickMovedListener _listener = new JoystickMovedListener() {

        @Override
        public void OnMoved(int pan, int tilt) {
        	armSpeed = pan;
        	updateRemoteState();
        }

        @Override
        public void OnReleased() {
        	armSpeed = 0;
        	updateRemoteState();
        }
        
        public void OnReturnedToCenter() {
        	armSpeed = 0;
        	updateRemoteState();
        };
	};
	
	public void onKnifeToggled(View view) {
		// Is the toggle on?
	    knife = ((ToggleButton) view).isChecked();
	    updateRemoteState();
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
