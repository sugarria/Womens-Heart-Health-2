package com.example.newwomenshearthealth;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Monitor extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		TextView textView = new TextView (this);
		textView.setText("Monitor");
		setContentView(textView);
		
	}

}
