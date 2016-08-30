package com.example.kasasasu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class KasasasuFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
	private FragmentMain fragmentMain;
	private FragmentSetting fragmentSetting;
	private FragmentRecording fragmentRecording;
	private Schedule schedule;

	public KasasasuFragmentStatePagerAdapter(FragmentManager fm) {
		super(fm);

		fragmentMain = new FragmentMain();
		fragmentSetting = new FragmentSetting();
		fragmentRecording = new FragmentRecording();
		schedule = new Schedule();
		fragmentSetting.setTargetFragment(fragmentMain, 0);
		fragmentSetting.setTargetFragment(fragmentSetting,1);
	}

	@Override public Fragment getItem(int i) {
		switch(i){
			case 0:
				return fragmentMain;
			case 1:
				return fragmentSetting;
			case 2:
				return schedule;
			default:
				return fragmentRecording;
		}
	}

	@Override public int getCount() {
		return 4;
	}

	@Override public CharSequence getPageTitle(int position) {
		String title = "";

		switch (position) {
			case 0:
				title = "ホーム";
				break;
			case 1:
				title = "設定";
				break;
			case 2:
				title = "カレンダー";
				break;
			case 3:
				title = "声質設定";
				break;
		}
		return title;
	}
}