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
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

public class DriverActivity extends Activity {
	
	public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
	public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
	private Socket socket;
	private String mHost;
	private int mPort;
	private PrintWriter mPrintWriter;
	
	TextView txtDebugState;
	ToggleButton nitroToggle;
    DualJoystickView driverCtrls;
    int leftSpeed, rightSpeed;
    boolean nitro;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driver);
		
		// Get the host and the port
	    Intent intent = getIntent();
	    mHost = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
    	mPort = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
    	
    	new Thread(new ClientThread()).start();

        txtDebugState = (TextView)findViewById(R.id.txtDebugState);
        nitroToggle = (ToggleButton)findViewById(R.id.toggleNitro);
        
        driverCtrls = (DualJoystickView)findViewById(R.id.dualjoystickView);
        driverCtrls.setOnJostickMovedListener(_listenerLeft, _listenerRight);
        
        leftSpeed = rightSpeed = 0;
        nitro = false;
        
        //updateRemoteState();
	}
	
	private void updateRemoteState() {
		txtDebugState.setText("Left: " + Integer.toString(leftSpeed) +
				", Right: " + Integer.toString(rightSpeed) +
				", Nitro: " + (nitro ? "On" : "Off"));
		assert Math.abs(leftSpeed) <= 7;
		assert Math.abs(rightSpeed) <= 7;
		// Send state over WiFi to RoboDroid
		char wifiMsg[] = "L??R??N?".toCharArray();
		wifiMsg[1] = (leftSpeed >= 0 ? '+' : '-');
		wifiMsg[2] = Integer.toString(Math.abs(leftSpeed)).charAt(0);
		wifiMsg[4] = (rightSpeed >= 0 ? '+' : '-');
		wifiMsg[5] = Integer.toString(Math.abs(rightSpeed)).charAt(0);
		wifiMsg[7] = (nitro ? '+' : '-');
		Log.d("Driver", new String(wifiMsg));
		mPrintWriter.println(new String(wifiMsg));
	}

	private JoystickMovedListener _listenerLeft = new JoystickMovedListener() {

        @Override
        public void OnMoved(int pan, int tilt) {
            leftSpeed = tilt;
        	updateRemoteState();
        }

        @Override
        public void OnReleased() {
            leftSpeed = 0;
        	updateRemoteState();
        }
        
        public void OnReturnedToCenter() {
            leftSpeed = 0;
        	updateRemoteState();
        };
	}; 

	private JoystickMovedListener _listenerRight = new JoystickMovedListener() {

        @Override
        public void OnMoved(int pan, int tilt) {
            rightSpeed = tilt;
        	updateRemoteState();
        }

        @Override
        public void OnReleased() {
            rightSpeed = 0;
        	updateRemoteState();
        }
        
        public void OnReturnedToCenter() {
            rightSpeed = 0;
        	updateRemoteState();
        };
	};
	
	public void onNitroToggled(View view) {
		// Is the toggle on?
	    nitro = ((ToggleButton) view).isChecked();
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

