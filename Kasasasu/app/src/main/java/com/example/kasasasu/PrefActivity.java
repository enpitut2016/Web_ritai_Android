package com.example.kasasasu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;


public class PrefActivity extends Activity {

	private HashMap<String, String> prefToRoman = new HashMap<>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pref);

		Intent intent = getIntent();
		int pos = intent.getIntExtra("area", 0);

		String[] prefs = {};
		switch (pos) {
			case 0:
				prefs = getResources().getStringArray(R.array.tohoku_prefecture);
				break;
			case 1:
				prefs = getResources().getStringArray(R.array.kanto_prefecture);
				break;
			case 2:
				prefs = getResources().getStringArray(R.array.hokurikukoshinetsu_prefecture);
				break;
			case 3:
				prefs = getResources().getStringArray(R.array.chubu_prefecture);
				break;
			case 4:
				prefs = getResources().getStringArray(R.array.kinki_prefecture);
				break;
			case 5:
				prefs = getResources().getStringArray(R.array.chugoku_prefecture);
				break;
			case 6:
				prefs = getResources().getStringArray(R.array.shikoku_prefecture);
				break;
			case 7:
				prefs = getResources().getStringArray(R.array.kyushu_prefecture);
				break;
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1, prefs);

		// リストビューにアイテム (adapter) を追加
		ListView prefListView = (ListView)findViewById(R.id.prefListView);
		prefListView.setAdapter(adapter);

		// アイテムクリック時ののイベントを追加
		prefListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent,
									View view, int pos, long id) {
				// 選択アイテムを取得
				ListView listView = (ListView)parent;
				String item = (String)listView.getItemAtPosition(pos);


				Intent intent = new Intent(PrefActivity.this, CityActivity.class);
				intent.putExtra("pref", item);
				startActivity(intent);
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_pref, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
