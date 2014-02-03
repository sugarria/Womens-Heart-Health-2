package com.example.womenshearthealth.utils;

import com.example.womenshearthealth.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Build;


public class TabListener<T extends Fragment> implements ActionBar.TabListener {
	
	private Fragment mFragment;
	private final Activity mActivity;
	private final Class<T> mFragmentClass;
	private final String mTag;
	
	public TabListener(Activity activity, String tag, Class<T> fragmentClass)
	{
		this.mActivity = activity;
		this.mFragmentClass = fragmentClass;
		this.mTag = tag;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft)
	{	

	    mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);

		if (mFragment == null) 
		{
			mFragment = (Fragment)Fragment.instantiate(mActivity, mFragmentClass.getName());
			ft.add(R.id.fragmentContainer, mFragment, mTag);
		} else {
			ft.attach(mFragment);
		}	
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		
	}


	@SuppressLint("NewApi")
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if (mFragment != null) {
			ft.detach(mFragment);
		}
	}


}
