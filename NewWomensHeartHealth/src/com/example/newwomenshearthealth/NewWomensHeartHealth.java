package com.example.newwomenshearthealth;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class NewWomensHeartHealth extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_womens_heart_health);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_womens_heart_health, menu);
		return true;
	}
	//test

}
