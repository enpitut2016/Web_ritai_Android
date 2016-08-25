package com.example.kasasasu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import java.util.HashMap;

public class FragmentSetting extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

	private View v;
	private EditText prefectureEditText, cityEditText;
	private Button saveButton;
	private KasasasuSQLiteOpenHelper DBHelper;
	private boolean swIsChecked;
	private HashMap<String, String> settings;
	private Switch switch1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_setting, null);
		prefectureEditText = (EditText)v.findViewById(R.id.prefecureText);
		cityEditText = (EditText)v.findViewById(R.id.cityText);

		saveButton = (Button)v.findViewById(R.id.saveButton);
		saveButton.setOnClickListener(this);

		switch1 = (Switch)v.findViewById(R.id.switch1);
		switch1.setOnCheckedChangeListener(this);

		DBHelper = new KasasasuSQLiteOpenHelper(getActivity());
		settings = DBHelper.get();
		Log.d("show", settings.toString());
		if (settings.containsKey("prefecture")) prefectureEditText.setText(settings.get("prefecture"));
		if (settings.containsKey("city")) cityEditText.setText(settings.get("city"));

		if (settings.containsKey("textSetting") && settings.get("textSetting").equals("on")){
			//Log.d("show", settings.get("prefecture"));
			switch1 = (Switch)v.findViewById(R.id.switch1);
			switch1.setChecked(true);
		} else {
			prefectureEditText.setFocusable(false);
			prefectureEditText.setFocusableInTouchMode(false);
			cityEditText.setFocusable(false);
			cityEditText.setFocusableInTouchMode(false);
		}
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		//Log.d("frag", "onclick");
		boolean settingIsText = false;
		String on_or_off = "off";
		if (swIsChecked) {
			settingIsText = true;
			on_or_off = "on";
		}
		DBHelper.add("textSetting", on_or_off);

		prefectureEditText = (EditText)getActivity().findViewById(R.id.prefecureText);
		cityEditText = (EditText)getActivity().findViewById(R.id.cityText);
		DBHelper.add("prefecture", prefectureEditText.getText().toString());
		DBHelper.add("city", cityEditText.getText().toString());
		((FragmentMain)getTargetFragment()).updateLatLon(settingIsText, prefectureEditText.getText().toString() + cityEditText.getText().toString());
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if ((swIsChecked = isChecked) == true) {
			prefectureEditText.setFocusable(true);
			prefectureEditText.setFocusableInTouchMode(true);
			cityEditText.setFocusable(true);
			cityEditText.setFocusableInTouchMode(true);
		} else {
			prefectureEditText.setFocusable(false);
			prefectureEditText.setFocusableInTouchMode(false);
			cityEditText.setFocusable(false);
			cityEditText.setFocusableInTouchMode(false);
		}
	}
}