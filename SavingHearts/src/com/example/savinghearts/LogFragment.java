package com.example.savinghearts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.graphics.Color;
import android.view.WindowManager;
import android.widget.ListView;

//import com.androidplot.demos.R;
//import com.androidplot.demos.ListViewActivity.MyViewAdapter;
//import com.androidplot.xy.*;

import java.util.Arrays;

public class LogFragment extends Fragment{

	//private XYPlot plot;
	
	public static final String ARG_SECTION_NUMBER = "section_number";
	private View v;
	private static final int NUM_PLOTS = 10;
    private static final int NUM_POINTS_PER_SERIES = 10;
    private static final int NUM_SERIES_PER_PLOT = 5;
    private ListView lv;
	
	public LogFragment(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_log, container, false);
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        /*setContentView(R.layout.listview_example);
        lv = (ListView) findViewById(R.id.listView1);
        lv.setAdapter(new MyViewAdapter(getApplicationContext(), R.layout.listview_example_item, null));
		*/
	}

	@Override
	public void onStart() {
		super.onStart();
	}
}
