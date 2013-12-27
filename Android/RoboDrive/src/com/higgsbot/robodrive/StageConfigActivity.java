package com.higgsbot.robodrive;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class StageConfigActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stage_config);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stage_config, menu);
		return true;
	}

}
