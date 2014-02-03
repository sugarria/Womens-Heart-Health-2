package com.example.womenshearthealth;

import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.app.Fragment;

public class AboutFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_about, container, false);
	}

	/**
	 * Method called on starting AboutFragment to add links to AboutPage text
	 */
	@Override
	public void onStart() {
		super.onStart();
		
		TextView textview = (TextView)getActivity().findViewById(R.id.txtvw_about_text);
		Linkify.addLinks(textview, Linkify.WEB_URLS);
		
	}

	
	
	
}
