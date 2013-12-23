package com.higgsbot.robodrive;

import com.higgsbot.wifidirect.DataTransferService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class DriverActivity extends Activity {
	
	public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
	public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
	String mHost;
	int mPort;
	
	TextView txtLeftSpeed, txtRightSpeed;
    DualJoystickView driverCtrls;
    int leftSpeed, rightSpeed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driver);
		
		// Get the host and the port
	    Intent intent = getIntent();
	    mHost = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
    	mPort = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
    	
		
        txtLeftSpeed = (TextView)findViewById(R.id.TextViewY1);
        txtRightSpeed = (TextView)findViewById(R.id.TextViewY2);
        
        driverCtrls = (DualJoystickView)findViewById(R.id.dualjoystickView);
        driverCtrls.setOnJostickMovedListener(_listenerLeft, _listenerRight);
        
        leftSpeed = rightSpeed = 0;
        
        txtLeftSpeed.setText(mHost);
        txtRightSpeed.setText(Integer.toString(mPort));
	}
	
	private void _updateSpeeds() {
		assert Math.abs(leftSpeed) <= 9;
		assert Math.abs(rightSpeed) <= 9;
		// Send speeds over WiFi to RoboDroid
		char wifiMsg[] = "DL??R??".toCharArray();
		wifiMsg[2] = (leftSpeed >= 0 ? '+' : '-');
		wifiMsg[3] = Integer.toString(Math.abs(leftSpeed)).charAt(0);
		wifiMsg[5] = (rightSpeed >= 0 ? '+' : '-');
		wifiMsg[6] = Integer.toString(Math.abs(rightSpeed)).charAt(0);
		Log.d("Driver", new String(wifiMsg));
		
		// **** send wifiMsg ****
		this.sendData(new String(wifiMsg));
	}

	private JoystickMovedListener _listenerLeft = new JoystickMovedListener() {

        @Override
        public void OnMoved(int pan, int tilt) {
            txtLeftSpeed.setText(Integer.toString(tilt));
            leftSpeed = tilt;
        	_updateSpeeds();
        }

        @Override
        public void OnReleased() {
            txtLeftSpeed.setText("released");
            leftSpeed = 0;
        	_updateSpeeds();
        }
        
        public void OnReturnedToCenter() {
            txtLeftSpeed.setText("stopped");
            leftSpeed = 0;
        	_updateSpeeds();
        };
	}; 

	private JoystickMovedListener _listenerRight = new JoystickMovedListener() {

        @Override
        public void OnMoved(int pan, int tilt) {
            txtRightSpeed.setText(Integer.toString(tilt));
            rightSpeed = tilt;
        	_updateSpeeds();
        }

        @Override
        public void OnReleased() {
            txtRightSpeed.setText("released");
            rightSpeed = 0;
        	_updateSpeeds();
        }
        
        public void OnReturnedToCenter() {
            txtRightSpeed.setText("stopped");
            rightSpeed = 0;
        	_updateSpeeds();
        };
	};
	
	private void sendData(String value) {
		//Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
		
		Intent serviceIntent = new Intent(this, DataTransferService.class);
        serviceIntent.setAction(DataTransferService.ACTION_SEND_DATA);
        serviceIntent.putExtra(DataTransferService.EXTRAS_GROUP_OWNER_ADDRESS, mHost);
        serviceIntent.putExtra(DataTransferService.EXTRAS_GROUP_OWNER_PORT, mPort);
        serviceIntent.putExtra(DataTransferService.EXTRAS_GROUP_DATA_TO_SEND, value);
        this.startService(serviceIntent);
        
	}
}
