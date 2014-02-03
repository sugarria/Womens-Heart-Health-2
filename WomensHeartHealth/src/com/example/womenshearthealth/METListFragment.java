package com.example.womenshearthealth;

import com.example.womenshearthealth.helpers.METSCSVHelper;
import com.example.womenshearthealth.helpers.SQLDatabaseHelper;
import com.example.womenshearthealth.models.GeneralMetActivity;
import com.example.womenshearthealth.models.MetActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class METListFragment extends Fragment implements OnClickListener, OnItemClickListener {

	SQLDatabaseHelper mSQLDbHelper;
	Activity mActivity;
	
	private ArrayAdapter<GeneralMetActivity> loadedMetsListAdapter;
	private ArrayAdapter<MetActivity> selectedMetsListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
		mSQLDbHelper = new SQLDatabaseHelper(mActivity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_metlist, container, false);
	}

	/**
	 * Builds the Selected METs list, All METs list, and save button
	 */
	@Override
	public void onStart() {
		super.onStart();

		// selected mets list
		selectedMetsListAdapter = new ArrayAdapter<MetActivity>(mActivity, android.R.layout.simple_list_item_1);
		ListView extraListView = (ListView) mActivity.findViewById(R.id.lstvw_metlistfragment_extralistview);
		extraListView.setAdapter(selectedMetsListAdapter);
		extraListView.setOnItemClickListener(this);

		// loaded mets list
		loadedMetsListAdapter = new ArrayAdapter<GeneralMetActivity>(mActivity, android.R.layout.simple_list_item_1);
		ListView loadedMetsListView = (ListView) mActivity.findViewById(R.id.lstvw_metlistfragment_loadedmets);
		loadedMetsListView.setAdapter(loadedMetsListAdapter);
		loadedMetsListView.setOnItemClickListener(this);

		// save button
		Button saveButton = (Button) mActivity.findViewById(R.id.btn_metlistfragment_savebutton);
		saveButton.setOnClickListener(this);
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
		for (GeneralMetActivity activity : METSCSVHelper.getAllAvailableMetActivities(mActivity)) {
			loadedMetsListAdapter.add(activity);
		}
		loadedMetsListAdapter.notifyDataSetChanged();
	}

	private void addToCart(MetActivity activity) {
		
		selectedMetsListAdapter.add(activity);
		selectedMetsListAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		switch (parent.getId()) {

		case R.id.lstvw_metlistfragment_extralistview: // temporary activity
			handleExtraList(position);
			return;
			
		case R.id.lstvw_metlistfragment_loadedmets: // loaded activities
			handleAvailableList(position);
			return;

		}
	}

	private void handleAvailableList(int position) {
		
		final GeneralMetActivity metActivity = loadedMetsListAdapter.getItem(position);

		// alert dialog
		final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle("For How Long Did You Do It?");
		alert.setMessage("For how many minutes did you do: "+metActivity.getName());

		// minutes input
		final EditText input = new EditText(getActivity());
		input.setHint("How many minutes?");
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(input);

		// save button
		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				if (value.isEmpty()) {
					value = "0";
				}
				MetActivity activity = new MetActivity(metActivity, Integer.valueOf(value));
				addToCart(activity);
			}
		});

		// cancel button
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Do nothing.
					}
				});

		alert.show();
	}
	
	private void handleExtraList(int position) {
		
		final MetActivity metActivity = selectedMetsListAdapter.getItem(position);

		// alert dialog
		final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle("For How Long Did You Do It?");
		alert.setMessage("For how many minutes did you do: "+metActivity.getName());

		// minutes input
		final EditText input = new EditText(getActivity());
		input.setText("" + metActivity.getMinutes());
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(input);

		// save button
		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				if (value.isEmpty()) {
					value = "0";
				}
				metActivity.setMinutes(Integer.valueOf(value));
				selectedMetsListAdapter.notifyDataSetChanged();
			}
		});

		// delete button
		alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				selectedMetsListAdapter.remove(metActivity);
				selectedMetsListAdapter.notifyDataSetChanged();
			}
		});

		alert.show();
	}


	/**
	 * Handles clicks for the MET list fragment
	 */
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		
		case R.id.btn_metlistfragment_savebutton: // save selected activities
			
			int count = selectedMetsListAdapter.getCount();
			for (int i = 0; i < count; i++) {
				MetActivity a = selectedMetsListAdapter.getItem(i);
				mSQLDbHelper.saveMetActivity(a);
			}
			selectedMetsListAdapter.clear();
			
			Toast.makeText(mActivity, "Saved", Toast.LENGTH_SHORT).show();
			break;
		}

	}
}
