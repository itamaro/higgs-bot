package com.higgsbot.robodrive;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.higgsbot.robodrive.DriverActivity.ClientThread;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class ArmControlActivity extends Activity {
	public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
	public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
	private Socket socket;
	private String mHost;
	private int mPort;
	private PrintWriter mPrintWriter;

	
	TextView txtArmSpeed;
	JoystickView armSpeedCtrl;
	int armSpeed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.armcontrol);

		// Get the host and the port
	    Intent intent = getIntent();
	    mHost = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
    	mPort = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
    	
    	new Thread(new ClientThread()).start();

		txtArmSpeed = (TextView)findViewById(R.id.armSpeed);

		armSpeedCtrl = (JoystickView)findViewById(R.id.armSpeedCtrl);
		armSpeedCtrl.setOnJostickMovedListener(_listener);
		
		armSpeed = 0;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.arm_control, menu);
		return true;
	}
	
	private void _updateSpeed() {
		assert Math.abs(armSpeed) <= 9;
		// Send speeds over WiFi to RoboDroid
		char wifiMsg[] = "A??".toCharArray();
		wifiMsg[1] = (armSpeed >= 0 ? '+' : '-');
		wifiMsg[2] = Integer.toString(Math.abs(armSpeed)).charAt(0);
		Log.d("Arm", new String(wifiMsg));
		// **** send wifiMsg here! ****
		
		// **** send wifiMsg ****
		this.sendData(new String(wifiMsg));
	}

	private JoystickMovedListener _listener = new JoystickMovedListener() {

        @Override
        public void OnMoved(int pan, int tilt) {
        	txtArmSpeed.setText(Integer.toString(pan));
        	armSpeed = pan;
        	_updateSpeed();
        }

        @Override
        public void OnReleased() {
        	txtArmSpeed.setText("released");
        	armSpeed = 0;
        	_updateSpeed();
        }
        
        public void OnReturnedToCenter() {
        	txtArmSpeed.setText("stopped");
        	armSpeed = 0;
        	_updateSpeed();
        };
	};

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
	
	private void sendData(String value) {
		mPrintWriter.println(value);
		Log.d("SENDDATA", "SENDDATA: " + value);
	}
}
