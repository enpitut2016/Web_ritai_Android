package com.example.kasasasu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class KasasasuFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
	private FragmentMain fragmentMain;
	private FragmentSetting fragmentSetting;

	public KasasasuFragmentStatePagerAdapter(FragmentManager fm) {
		super(fm);

		fragmentMain = new FragmentMain();
		fragmentSetting = new FragmentSetting();
		//fragmentSetting.setTargetFragment(fragmentMain, 0);
	}

	@Override public Fragment getItem(int i) {
		switch(i){
			case 0:
				return fragmentMain;
			default:
				return fragmentSetting;
		}
	}

	@Override public int getCount() {
		return 2;
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
		}
		return title;
	}
}