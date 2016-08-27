package com.example.kasasasu;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import java.util.ArrayList;
import java.util.HashMap;

public class FragmentSetting extends Fragment implements CompoundButton.OnCheckedChangeListener {
	private View v;
	private Activity activity;
	private HashMap<String, String> settings;
	private KasasasuSQLiteOpenHelper DBHelper;
	private Switch switch1;
	private boolean swIsChecked;
	private ArrayList<Setting> settingArrayList;
	private SettingAdapter adapter;
	private String address;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//フラグメントのビューを取得
		v = inflater.inflate(R.layout.fragment_setting2, null);

		//MainActivityを取得
		activity = getActivity();

		switch1 = (Switch)v.findViewById(R.id.switch1);
		switch1.setOnCheckedChangeListener(this);

		//設定内容をDBから取得
		DBHelper = new KasasasuSQLiteOpenHelper(getActivity());
		settings = DBHelper.get();

		//住所文字列を設定内容から取得
		address = "";
		if (settings.containsKey("prefecture")) address += settings.get("prefecture");
		if (settings.containsKey("city")) address += settings.get("city");

		//ListViewのアダプターを生成
		settingArrayList = new ArrayList<>();
		if (swIsChecked) settingArrayList.add(new Setting("地域", address));
		adapter = new SettingAdapter(activity, settingArrayList);

		// リストビューにアイテム (adapter) を追加
		ListView listView1 = (ListView)v.findViewById(R.id.listView1);
		listView1.setAdapter(adapter);

		// アイテムクリック時ののイベントを追加
		listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent,
									View view, int pos, long id) {
				// 選択アイテムを取得
				ListView listView = (ListView)parent;
				String item = ((Setting)listView.getItemAtPosition(pos)).getName();

				switch (item) {
					case "地域":
						//スイッチが有効状態であれば住所選択画面へ
						if (swIsChecked) {
							Intent intent = new Intent(activity, AreaActivity.class);
							startActivity(intent);
						}
						break;
				}
			}
		});

		//スイッチのオン・オフをDBから取得
		if (settings.containsKey("selfAreaSetting") && settings.get("selfAreaSetting").equals("true")) {
			switch1.setChecked(true);
			swIsChecked = true;
		} else {
			swIsChecked = false;
		}
		return v;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		swIsChecked = isChecked;
		DBHelper.add("selfAreaSetting", String.valueOf(isChecked));

		if (isChecked) adapter.add(new Setting("地域", address));
		else adapter.delete("地域");
        ((FragmentMain)getTargetFragment()).updateLatLon(isChecked,address);
		adapter.notifyDataSetChanged();
	}
}