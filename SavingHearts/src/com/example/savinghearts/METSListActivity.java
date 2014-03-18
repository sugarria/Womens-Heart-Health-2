package com.example.savinghearts;

import com.example.savinghearts.helpers.METSCSVHelper;
import com.example.savinghearts.helpers.SQLDatabaseHelper;
import com.example.savinghearts.GeneralMetActivity;
import com.example.savinghearts.MetActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class METSListActivity extends Activity implements OnItemClickListener{
	
	SQLDatabaseHelper mSQLDbHelper;
	
	private ArrayAdapter<GeneralMetActivity> loadedMetsListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSQLDbHelper = new SQLDatabaseHelper(this);
		setContentView(R.layout.activity_metlist);		
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	/**
	 * Code to open up workout screen goes here
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//metActivity gives the names of the exercise selected
		//final GeneralMetActivity metActivity = loadedMetsListAdapter.getItem(position);

	}
	
	/**
	 * Builds METs list
	 */
	@Override
	public void onStart() {
		super.onStart();

		// loaded mets list
		loadedMetsListAdapter = new ArrayAdapter<GeneralMetActivity>(this, android.R.layout.simple_list_item_1);
		ListView loadedMetsListView = (ListView) this.findViewById(R.id.lstvw_metlistfragment_loadedmets);
		loadedMetsListView.setAdapter(loadedMetsListAdapter);
		loadedMetsListView.setOnItemClickListener(this);
	}
	
	/**
	 * Refreshes Available METs list
	 */
	@Override
	public void onResume() {
		super.onResume();
		
		updateAvailableMetsList();
	}

	private void updateAvailableMetsList() {
		
		loadedMetsListAdapter.clear();
		for (GeneralMetActivity activity : METSCSVHelper.getAllAvailableMetActivities(this)) {
			loadedMetsListAdapter.add(activity);
		}
		loadedMetsListAdapter.notifyDataSetChanged();
	}
}