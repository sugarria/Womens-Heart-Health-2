package com.example.womenshearthealth.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import com.example.womenshearthealth.models.GeneralMetActivity;
import com.example.womenshearthealth.utils.CSVReader;

import android.content.Context;

public class METSCSVHelper {
	
	/***
	 * Returns a list of all activities in 'metactivies.csv' located in the application's assets
	 * @param context Android Context object for the app
	 * @return List<GeneralMetActivity> produced from the MET activities in 'metactivities.csv'
	 */
	public static List<GeneralMetActivity> getAllAvailableMetActivities(Context context) {
		
		LinkedList<GeneralMetActivity> activities = new LinkedList<GeneralMetActivity>();
		try {
			
			InputStream is = context.getAssets().open("metactivities.csv");
			CSVReader reader = new CSVReader(new InputStreamReader(is));
			
			String[] nextLine = reader.readNext();
			while (nextLine != null) {
				String name = nextLine[0];
				double metsValue = Double.valueOf(nextLine[1]);
				GeneralMetActivity activity = new GeneralMetActivity(name, metsValue);
				activities.add(activity);
				
				nextLine = reader.readNext();
			}	
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return activities;
	}

}
