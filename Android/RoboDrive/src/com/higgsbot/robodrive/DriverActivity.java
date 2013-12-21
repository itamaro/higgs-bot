package com.higgsbot.robodrive;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

public class DriverActivity extends Activity {
	
	TextView txtLeftSpeed, txtRightSpeed;
    DualJoystickView driverCtrls;
    int leftSpeed, rightSpeed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driver);
		
        txtLeftSpeed = (TextView)findViewById(R.id.TextViewY1);
        txtRightSpeed = (TextView)findViewById(R.id.TextViewY2);

        driverCtrls = (DualJoystickView)findViewById(R.id.dualjoystickView);
        driverCtrls.setOnJostickMovedListener(_listenerLeft, _listenerRight);
        
        leftSpeed = rightSpeed = 0;
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
		// **** send wifiMsg here! ****
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

}
