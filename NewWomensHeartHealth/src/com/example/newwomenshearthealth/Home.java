package com.example.newwomenshearthealth;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Home extends Activity  {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		TextView textView = new TextView (this);
		textView.setText("Home");
		setContentView(textView);
		
	}
	
}
