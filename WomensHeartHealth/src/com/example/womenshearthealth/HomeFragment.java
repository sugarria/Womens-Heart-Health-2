package com.example.womenshearthealth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.example.womenshearthealth.helpers.CalculationsHelper;
import com.example.womenshearthealth.helpers.SQLDatabaseHelper;
import com.example.womenshearthealth.helpers.SettingsHelper;
import com.example.womenshearthealth.models.MetActivity;
import com.fima.chartview.ChartView;
import com.fima.chartview.LinearSeries;
import com.fima.chartview.LinearSeries.LinearPoint;
import com.fima.chartview.ValueLabelAdapter;
import com.fima.chartview.ValueLabelAdapter.LabelOrientation;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeFragment extends Fragment implements OnClickListener {

	private Activity mActivity;
	private SQLDatabaseHelper mSqlDBHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_home, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
		mSqlDBHelper = new SQLDatabaseHelper(mActivity);
	}

	@Override
	public void onStart() {
		super.onStart();

		// Mets Chart Card
		LinearLayout metsCard = (LinearLayout) this.getActivity().findViewById(R.id.graph_card);
		metsCard.setOnClickListener(this);
		metsCard.setVisibility(8);
		
		View metsCardDropshadow = (View) this.getActivity().findViewById(R.id.graphCardDropshadow);
		metsCardDropshadow.setVisibility(8);
		
		
		// Target HR Card
		LinearLayout targetHRCard = (LinearLayout) this.getActivity().findViewById(R.id.target_hr_card);
		targetHRCard.setOnClickListener(this);
		targetHRCard.setVisibility(8);
		
		View targetHRCardDropshadow = (View) this.getActivity().findViewById(R.id.targerHRCardDropshadow);
		targetHRCardDropshadow.setVisibility(8);

		
		// Calories Chart Card
		LinearLayout caloriesCard = (LinearLayout) this.getActivity().findViewById(R.id.graph_card2);
		caloriesCard.setOnClickListener(this);
		caloriesCard.setVisibility(8);
		
		View caloriesCardDropshadow = (View) this.getActivity().findViewById(R.id.graphCardDropshadow2);
		caloriesCardDropshadow.setVisibility(8);

		
		// Random Facts Card
		LinearLayout randomFactsCard = (LinearLayout) this.getActivity().findViewById(R.id.random_facts_card);
		randomFactsCard.setOnClickListener(this);
		randomFactsCard.setVisibility(8);
		
		View randomFactsCardDropshadow = (View) this.getActivity().findViewById(R.id.factsCardDropshadow);
		randomFactsCardDropshadow.setVisibility(8);
		
	}

	@Override
	public void onResume() {
		super.onResume();
		
		// start card animation
		startAnimationPopOut(R.id.graph_card);
		startAnimationPopOut(R.id.graphCardDropshadow);
		startAnimationPopOut(R.id.target_hr_card);
		startAnimationPopOut(R.id.targerHRCardDropshadow);
		startAnimationPopOut(R.id.graph_card2);
		startAnimationPopOut(R.id.graphCardDropshadow2);
		startAnimationPopOut(R.id.random_facts_card);
		startAnimationPopOut(R.id.factsCardDropshadow);

		updateUIValues();
	}

	private void startAnimationPopOut(int id) {
		View myLayout = (View) getActivity().findViewById(id);
		myLayout.setVisibility(0);
		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.slide_up_from_right);

		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {

			}
		});

		myLayout.clearAnimation();
		myLayout.startAnimation(animation);

	}

	/**
	 * Loads saved data and fills in the UI elements
	 */
	private void updateUIValues() {

		int myAge = SettingsHelper.getAge(this.getActivity());
		Typeface typeface = Typeface.createFromAsset(getActivity()
				.getAssets(), "font/roboto.ttf");
		
		// target heart rates
		TextView targetHR = (TextView) this.getActivity().findViewById(
				R.id.targertHeartRatesTextView);
		targetHR.setTypeface(typeface);
		
		TextView BPM1 = (TextView) this.getActivity().findViewById(R.id.BPM1);
		int bpm50 = CalculationsHelper.getTargetHeartRateFromAge(myAge,
				CalculationsHelper.TARGET_50_PERCENT);
		BPM1.setText(bpm50 + " BPM: 50% MHR");
		
		TextView BPM2 = (TextView) this.getActivity().findViewById(R.id.BPM2);
		int bpm85 = CalculationsHelper.getTargetHeartRateFromAge(myAge,
				CalculationsHelper.TARGET_85_PERCENT);
		BPM2.setText(bpm85 + " BPM: 85% MHR");
		
		TextView BPM3 = (TextView) this.getActivity().findViewById(R.id.BPM3);
		int bpm100 = CalculationsHelper.getTargetHeartRateFromAge(myAge,
				CalculationsHelper.TARGET_MAX);
		BPM3.setText(bpm100 + " BPM: 100% MHR");
		
		
		// mets chart
		TextView metsLogged = (TextView) this.getActivity().findViewById(
				R.id.metsLoggedTextView);
		metsLogged.setTypeface(typeface);
		buildMetsChart();
		
		// calories chart
		TextView calsLogged = (TextView) this.getActivity().findViewById(
				R.id.metsLoggedTextView2);
		calsLogged.setTypeface(typeface);
		buildCalsChart();
		
		
		// random facts
		Random rand = new Random();
		Resources res = getResources();
		String[] randomFacts = res.getStringArray(R.array.random_facts_array);
		
		TextView randomFactsTextView = (TextView) this.getActivity().findViewById(R.id.randomFact);
		randomFactsTextView.setText(randomFacts[rand.nextInt(randomFacts.length)]);

	}

	private void buildMetsChart() {
		
		ChartView chart = (ChartView) mActivity.findViewById(R.id.chart_view);
		chart.setLeftLabelAdapter(new ValueLabelAdapter(getActivity(),
				LabelOrientation.VERTICAL));
		
		// highest achieved mets per day
		LinearSeries weeklyMetsSeries = new LinearSeries();
		for (LinearPoint p : getLinearPointsForTheWeek()) {
			weeklyMetsSeries.addPoint(p);
		}
		weeklyMetsSeries.setLineColor(0xFFFF99CC);
		weeklyMetsSeries.setLineWidth(4);
		chart.addSeries(weeklyMetsSeries);
		
	}

	private void buildCalsChart() {
		
		ChartView chart = (ChartView) mActivity.findViewById(R.id.chart_view2);
		chart.setLeftLabelAdapter(new ValueLabelAdapter(getActivity(),
				LabelOrientation.VERTICAL));

		// calories burned each day
		LinearSeries weeklyCalSeries = new LinearSeries();
		for (LinearPoint p : getCaloriePointsForTheWeek()) {
			weeklyCalSeries.addPoint(p);
		}
		weeklyCalSeries.setLineColor(0xFFFF99CC);
		weeklyCalSeries.setLineWidth(4);
		chart.addSeries(weeklyCalSeries);

	}

	/**
	 * Returns a list of Calories burned per each day of the current calendar week
	 * @return
	 */
	private List<LinearPoint> getCaloriePointsForTheWeek() {
		
		int weight = SettingsHelper.getWeight(getActivity());
		List<Set<MetActivity>> days = getMetActivitiesForTheWeek();

		ArrayList<LinearPoint> points = new ArrayList<LinearPoint>();
		for (int day = 0; day < 7; day++) {
			
			int count = 0;
			for (MetActivity activity : days.get(day)) {
				count += activity.getMetMinutes();
			}
			double cals = CalculationsHelper.getCaloriesFromMetMinutes(weight,
					count);
			
			points.add(new LinearPoint(day, cals));
		}

		return points;
	}

	/**
	 * This returns a list of 7 linear points for each of the 7 days of the
	 * week. The y-value is the highest recorded met activity for that day.
	 * 
	 * @return
	 */
	private List<LinearPoint> getLinearPointsForTheWeek() {

		List<Set<MetActivity>> days = getMetActivitiesForTheWeek();

		ArrayList<LinearPoint> points = new ArrayList<LinearPoint>();
		for (int day = 0; day < 7; day++) {
			
			double max = 0; // there is no such thing as negative met values
			for (MetActivity activity : days.get(day)) {
				double metval = activity.getMetsvalue();
				if (max < metval) {
					max = metval;
				}
			}
			
			points.add(new LinearPoint(day, max));
		}

		return points;
	}

	/**
	 * This method returns all the met activities saved for the current calendar
	 * week. (Sun - Sat) The List represents a 7 day, each element of the list
	 * represents a day of the week Each Set is a collection of all the met
	 * activities for that day
	 * 
	 * @return
	 */
	private List<Set<MetActivity>> getMetActivitiesForTheWeek() {

		Calendar c = Calendar.getInstance();
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		int daysSinceSunday = dayOfWeek - Calendar.SUNDAY;
		int daysUntilSaturday = Calendar.SATURDAY - dayOfWeek;

		
		Calendar upperLimit = Calendar.getInstance();
		upperLimit.roll(Calendar.DAY_OF_WEEK, daysUntilSaturday);
		Calendar lowerLimit = Calendar.getInstance();
		lowerLimit.roll(Calendar.DAY_OF_WEEK, -1 * daysSinceSunday);

		
		Calendar dayCursor = lowerLimit;
		Date sunday = dayCursor.getTime();
		dayCursor.roll(Calendar.DATE, true);
		Date monday = dayCursor.getTime();
		dayCursor.roll(Calendar.DATE, true);
		Date tuesday = dayCursor.getTime();
		dayCursor.roll(Calendar.DATE, true);
		Date wednesday = dayCursor.getTime();
		dayCursor.roll(Calendar.DATE, true);
		Date thursday = dayCursor.getTime();
		dayCursor.roll(Calendar.DATE, true);
		Date friday = dayCursor.getTime();
		dayCursor.roll(Calendar.DATE, true);
		Date saturday = dayCursor.getTime();
		

		ArrayList<Set<MetActivity>> days = new ArrayList<Set<MetActivity>>();
		days.add(0, mSqlDBHelper.getMetActivitiesForDay(sunday));
		days.add(1, mSqlDBHelper.getMetActivitiesForDay(monday));
		days.add(2, mSqlDBHelper.getMetActivitiesForDay(tuesday));
		days.add(3, mSqlDBHelper.getMetActivitiesForDay(wednesday));
		days.add(4, mSqlDBHelper.getMetActivitiesForDay(thursday));
		days.add(5, mSqlDBHelper.getMetActivitiesForDay(friday));
		days.add(6, mSqlDBHelper.getMetActivitiesForDay(saturday));

		return days;
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;

		switch (v.getId()) {
		case R.id.graph_card: // Display past activities
			intent = new Intent(getActivity(), AllMetsPrintedActivity.class);
			getActivity().startActivity(intent);
			break;
			
		case R.id.target_hr_card: // Display info on target heart rates
			intent = new Intent(getActivity(), TargetHRInformationActivity.class);
			getActivity().startActivity(intent);
			break;
			
		case R.id.graph_card2: // Display mets information
			intent = new Intent(getActivity(), MetsInformationActivity.class);
			getActivity().startActivity(intent);
			break;
		}
	}

}
