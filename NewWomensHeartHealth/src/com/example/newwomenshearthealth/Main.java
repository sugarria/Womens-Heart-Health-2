package com.example.newwomenshearthealth;

import com.example.newwomenshearthealth.adapter.TabsPagerAdapter;
import com.example.newwomenshearthealth.helper.SettingsHelper;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class Main extends FragmentActivity implements ActionBar.TabListener {

	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	
	
	//splash screen
	final private int SPLASH_DURATION = 2000;	
	private Dialog mSplashScreenDialog;
	
	// Tab titles
	private String[] tabs = { "Home", "Workout Log", "Monitor" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		showSplashScreen();
		setContentView(R.layout.main);

		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);		

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
	
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
	    		  Intent intent = new Intent(Main.this, Settings.class);
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
}

