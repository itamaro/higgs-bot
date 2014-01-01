package com.higgsbot.robodroid;

import com.higgsbot.wifidirect.AudioModem;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

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
    
    public void playTestSound(View view) {
    	AudioModem modem = new AudioModem();
    	modem.sendData("Go Higgs abcdefg0123456789 yo yo yo yo".toCharArray());
    }
    
}
