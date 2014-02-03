package com.example.womenshearthealth;

import com.example.womenshearthealth.helpers.SettingsHelper;
import com.example.womenshearthealth.utils.TabListener;

import android.os.Bundle;
import android.os.Handler;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class MainActivity extends Activity {

	final private int SPLASH_DURATION = 2000;
	
	private ActionBar mActionBar;
	private Dialog mSplashScreenDialog;
	
	/**
	 * Extends the onCreate method of Activity to show splash screen,
	 * set the content of the main activity, and setup navigation tabs
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        showSplashScreen();
        
        setContentView(R.layout.activity_main);
		
        setupTabs();
        if (savedInstanceState != null) {
        	mActionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }

	/**
	 * Method to display splash screen image when starting the app
	 */
	protected void showSplashScreen() {
		
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
				}
	    	  
	      }
	    }, SPLASH_DURATION);
		
	}

	private boolean isInitalRun() {
		return SettingsHelper.isInitialRun(this);
	}
	
	private void setInitialRun (boolean initRun) {
		SettingsHelper.setInitialRun(this, initRun);
	}
	
	/**
	 * Method used to hide splash screen image
	 */
	protected void dismissSplashScreen() {
		if (mSplashScreenDialog != null) {
			mSplashScreenDialog.dismiss();
			mSplashScreenDialog = null;
		}
	}
	
	/**
	 * Saves current navigation tab when saving instance state
	 */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	/**
	 * Starts selected activity when user selects options menu item
	 * @return boolean returns true if valid menu option selected
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		switch(item.getItemId()) {
		
		case R.id.menu_settings: //user selects "Settings" from the menu
			Intent intent = new Intent(this, SettingsActivity.class);
			this.startActivity(intent);
			return true;
			
		default:
			return false;
				
		}
		
	}

	/**
	 * Initializes and displays the tabs
	 */
	private void setupTabs() {
		
		mActionBar = getActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Home tab
		Tab homeTab = mActionBar.newTab();
		homeTab.setText("Home");
		homeTab.setTabListener(new TabListener<HomeFragment>(this,
				"home", HomeFragment.class));
		mActionBar.addTab(homeTab);
		
		// METs tab
		Tab metsTab = mActionBar.newTab();
		metsTab.setText("METs");
		metsTab.setTabListener(new TabListener<METListFragment>(this,
				"mets", METListFragment.class));
		mActionBar.addTab(metsTab);
		
		// About tab
		Tab aboutTab = mActionBar.newTab();
		aboutTab.setText("About");
		aboutTab.setTabListener(new TabListener<AboutFragment>(this,
				"about", AboutFragment.class));
		mActionBar.addTab(aboutTab);
		
	}

}
