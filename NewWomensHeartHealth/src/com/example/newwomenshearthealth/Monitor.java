package com.example.newwomenshearthealth;

import com.example.newwomenshearthealth.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Monitor extends Fragment  {

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.monitor, container, false);
		
		return rootView;
	}
	
}

