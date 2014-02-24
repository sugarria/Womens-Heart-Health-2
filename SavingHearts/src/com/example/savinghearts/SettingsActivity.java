package com.example.savinghearts;

import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends Activity implements OnClickListener,
		OnDateSetListener {

	public static final int DATE_DIALOG_ID = 0;
	
	private TextView mBirthDateTextView;
	private TextView mWeightTextView;
	
	private Date mBirthDate;
	private int mWeight;

	/**
	 * onCreate method to set the layout, options, and buttons
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		// birth date
		mBirthDate = new Date();
		mBirthDateTextView = (TextView) findViewById(R.id.txtvw_settings_bday);
		mBirthDateTextView.setOnClickListener(this);
		
		// weight
		mWeight = 0;
		mWeightTextView = (TextView) findViewById(R.id.txtvw_settings_weight);
		mWeightTextView.setOnClickListener(this);
		
		// save button
		Button saveButton = (Button) findViewById(R.id.btn_settings_savebutton);
		saveButton.setOnClickListener(this);
		
		updateUI();
		
	}

	/**
	 * Refreshes user interface upon restoring a saved instance state
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		updateUI();
	}

	/**
	 * Performs appropriate actions based on where user clicks
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View view) {

		switch (view.getId()) {

		case R.id.btn_settings_savebutton: // save button
			finish(); 
			break;
			
		case R.id.txtvw_settings_bday: // birth date
			showDialog(DATE_DIALOG_ID);
			break;
			
		case R.id.txtvw_settings_weight: // weight
			
			// alert
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Update Weight");
			alert.setMessage("Please enter in your weight in pounds:");
			
			// weight input
			final EditText input = new EditText(this);
			input.setText(""+this.mWeight);
			input.requestFocus();
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			alert.setView(input);
			
			// save button
			alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String value = input.getText().toString();
					int weight = Integer.valueOf(value).intValue();
					SettingsHelper.setWeight(SettingsActivity.this,
							weight);
					updateUI();
				}
			});
			
			// cancel button
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// do nothing
				}
			});
			
			alert.show();
			break;

		}
	}

	/**
	 * Updates interface after user selects a date from a DatePicker view
	 */
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		
		Calendar c = Calendar.getInstance();
		c.set(year, monthOfYear, dayOfMonth);
		SettingsHelper.setBirthdate(this, c.getTime());
		
		updateUI();
	}

	/**
	 * Loads saved data and fills in the UI elements
	 */
	private void updateUI() {

		// birth date
		this.mBirthDate = SettingsHelper.getBirthdate(this);
		Calendar c = Calendar.getInstance();
		c.setTime(mBirthDate);
		int month = c.get(Calendar.MONTH); 
		int date = c.get(Calendar.DATE);
		int year = c.get(Calendar.YEAR);
		StringBuilder dateBuilder = new StringBuilder();

		if (month < 10) {
			dateBuilder.append("0");
		}
		dateBuilder.append( (month+1) + "/");

		if (date < 10) {
			dateBuilder.append("0");
		}
		dateBuilder.append(date + "/" + year);
		
		mBirthDateTextView.setText(dateBuilder.toString());

		
		// weight
		this.mWeight = SettingsHelper.getWeight(this);
		mWeightTextView.setText(mWeight + " lbs");
		
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		
		case DATE_DIALOG_ID: // date picker dialog
			
			Calendar c = Calendar.getInstance();
			c.setTime(mBirthDate);
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DATE);
			
			return new DatePickerDialog(this, this, year, month, day);
		}

		return super.onCreateDialog(id);
	}

}