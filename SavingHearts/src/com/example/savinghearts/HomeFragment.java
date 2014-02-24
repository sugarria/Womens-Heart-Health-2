package com.example.savinghearts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeFragment extends Fragment implements OnClickListener{

	public static final String ARG_SECTION_NUMBER = "section_number";
	
	public HomeFragment(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_home, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		
		// Target HR Card
		LinearLayout targetHRCard = (LinearLayout) this.getActivity().findViewById(R.id.target_hr_card);
		//targetHRCard.setOnClickListener(this);
		targetHRCard.setVisibility(8);
		
		View targetHRCardDropshadow = (View) this.getActivity().findViewById(R.id.targerHRCardDropshadow);
		targetHRCardDropshadow.setVisibility(8);

		// Random Facts Card
		LinearLayout randomFactsCard = (LinearLayout) this.getActivity().findViewById(R.id.random_facts_card);
		//randomFactsCard.setOnClickListener(this);
		randomFactsCard.setVisibility(8);
		
		View randomFactsCardDropshadow = (View) this.getActivity().findViewById(R.id.factsCardDropshadow);
		randomFactsCardDropshadow.setVisibility(8);
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = null;

		switch (v.getId()) {
		/*	
		case R.id.target_hr_card: // Display info on target heart rates
			intent = new Intent(getActivity(), TargetHRInformationActivity.class);
			getActivity().startActivity(intent);
			break;
		 */
		}
	}
}
