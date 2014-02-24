package com.example.savinghearts;

import java.util.Locale;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	final private int SPLASH_DURATION = 2000;
	private Dialog mSplashScreenDialog;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        //showSplashScreen();
        
        setContentView(R.layout.activity_main);
        
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}
	
	/**
	 * Method to display splash screen image when starting the app
	 */
	/*protected void showSplashScreen() {
		
		ImageView splashscreen = new ImageView(this);
		splashscreen.setOnTouchListener(new OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
            	dismissSplashScreen(); 
                return false;
            }
            
       });
		
		Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.splashscreen);
		splashscreen.setImageBitmap(image);
		
		mSplashScreenDialog = new Dialog(this, R.style.SplashScreen);
		mSplashScreenDialog.setTitle(R.string.app_name);
		mSplashScreenDialog.setContentView(splashscreen);
		mSplashScreenDialog.setCancelable(false);
		mSplashScreenDialog.show();
		
		final Handler handler = new Handler();
	    handler.postDelayed(new Runnable() {
	      @Override
	      public void run() {

	    	  dismissSplashScreen();
	    	  
	    	  if (isInitalRun()) {
	    		  setInitialRun(false);
	    		  
	    		  // user will be redirected to the settings activity in order to
	    		  // save their weight and birthday
	    		  Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
	    		  startActivity(intent);
				
				} else {
					return;
				}return;
	    	  
	      }
	    }, SPLASH_DURATION);
		
	}

	private boolean isInitalRun() {
		return SettingsHelper.isInitialRun(this);
	}
	
	private void setInitialRun (boolean initRun) {
		SettingsHelper.setInitialRun(this, initRun);
	}*/
	
	/**
	 * Method used to hide splash screen image
	 */
	/*protected void dismissSplashScreen() {
		if (mSplashScreenDialog != null) {
			mSplashScreenDialog.dismiss();
			mSplashScreenDialog = null;
		}
	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * Starts selected activity when user selects options menu item
	 * @return boolean returns true if valid menu option selected
	 */
	//@Override
	/*public boolean onOptionsItemSelected(MenuItem item) {
	
		switch(item.getItemId()) {
		
		case R.id.menu_settings: //user selects "Settings" from the menu
			Intent intent = new Intent(this, SettingsActivity.class);
			this.startActivity(intent);
			return true;
		case R.id.menu_about: //user selects "About" from the menu
			Intent intent2 = new Intent(this, AboutActivity.class);
			this.startActivity(intent2);
			return true;
		default:
			return false;
				
		}
		
	}*/

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment;
			Bundle args = new Bundle();
			
			switch (position) {
			case 0:
				fragment = new HomeFragment();
				fragment.setArguments(args);
				args.putInt(HomeFragment.ARG_SECTION_NUMBER, position + 1);
				return fragment;
			case 1:
				fragment = new DummySectionFragment();
				fragment.setArguments(args);
				args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
				return fragment;
			case 2:
				fragment = new MonitorFragment();
				fragment.setArguments(args);
				args.putInt(MonitorFragment.ARG_SECTION_NUMBER, position + 1);
				return fragment;
			}
			return null;
			
			
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			/*Fragment fragment = new MonitorFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
			*/
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}
	
	/**
	 * Fires when Start Workout button is pushed, opening up the MET List activity
	 */
	/*public void startButton(View view) {
	    Intent i = new Intent(this, METListActivity.class);
	    startActivity(i);
	    finish();
	}*/
	
	/**
	 * Fires when Connect To Monitor button is pushed
	 */
	/*public void sendMessage(View view) {
	
	}*/

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

}
