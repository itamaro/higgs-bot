package com.higgsbot.robodrive;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class ArmControlActivity extends Activity {
	
	TextView txtArmSpeed;
	JoystickView armSpeedCtrl;
	int armSpeed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.armcontrol);
		
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

}
