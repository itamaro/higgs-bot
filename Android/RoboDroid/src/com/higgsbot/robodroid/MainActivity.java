package com.higgsbot.robodroid;

import java.util.Arrays;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private AudioModem arduinoModem = new AudioModem();

    public void playTestSignal(View view) {
    	//byte data[] = {(byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef,
    	//		(byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef};
    	//byte data[] = new byte[4*10];
    	//Arrays.fill(data, (byte) 0xaa);
    	
    	arduinoModem.sendData("Go Higgs! ZZ!".toCharArray());
    }

    public void startHiggs(View view) {
    	// TODO:
    	// - Start commandToArduino thread
    	// - Start autonomous mode
    	// - Start listening on WiFi and passing to commandToArduino thread
    	String driverIp = ((EditText) findViewById(R.id.driverIP)).getText().toString();
    	String armCtrlIp = ((EditText) findViewById(R.id.armCtrlIP)).getText().toString();
    	String snitchColor = ((Spinner) findViewById(R.id.snitchColor)).getSelectedItem().toString();
    }
    
}
