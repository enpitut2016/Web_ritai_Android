package com.example.kasasasu;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentSetting extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
	private View v;
	private Activity activity;
	private HashMap<String, String> settings;
	private KasasasuSQLiteOpenHelper DBHelper;
	private Switch switch1;
	private boolean swIsChecked;
	private ArrayList<Setting> settingArrayList;
	private SettingAdapter adapter;
	private String address;

	private MediaRecorder mediaRecorder;
	// private Button record_start;
	private boolean flag_start_button;
	private ImageButton record_start;
	private ImageButton startButton;
	private ImageButton stopButton;
	private ImageButton deleteButton;
	private MediaPlayer mediaPlayer;
	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;
	private List<String> musicList = new ArrayList<>();
	private ListView listView;
	private File[] files;
	private ArrayAdapter<String> arrayAdapter;

	String filePath;
	String item = null;
	String renameFilePath = null;
	String playMusicPath = null;
	public static String listSoundPath = "";

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		activity = getActivity();
		DBHelper = new KasasasuSQLiteOpenHelper(activity);
		settings = DBHelper.get();
		settingArrayList = new ArrayList<>();

		flag_start_button = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//フラグメントのビューを取得
		v = inflater.inflate(R.layout.fragment_setting2, null);

		//MainActivityを取得
		//activity = getActivity();
        Log.d("activity",activity.toString());

		switch1 = (Switch)v.findViewById(R.id.switch1);
		switch1.setOnCheckedChangeListener(this);

		//設定内容をDBから取得
		/*DBHelper = new KasasasuSQLiteOpenHelper(getActivity());
		settings = DBHelper.get();*/

		//住所文字列を設定内容から取得
		address = "";
		if (settings.containsKey("prefecture")) address += settings.get("prefecture");
		if (settings.containsKey("city")) address += settings.get("city");

		//ListViewのアダプターを生成
		//settingArrayList = new ArrayList<>();
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

		playMusicPath = Environment.getExternalStorageDirectory() + "/Music";
		files = new File(playMusicPath).listFiles();
		if(files !=null) {
			for(int i = 0; i < files.length; i++) {
				if(files[i].getName().endsWith(".3gp")) {
					musicList.add(files[i].getName());
				}
			}
		}

		listView = (ListView)v.findViewById(R.id.listView);

		// リストビューのカスタム
		arrayAdapter = new ArrayAdapter<String>(activity,android.R.layout.simple_expandable_list_item_1,musicList );
		listView.setAdapter(arrayAdapter);

		// リストビューがクリックされたときの処理
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView listView1 = (ListView)parent;
				item = (String)listView1.getItemAtPosition(position);
				listSoundPath = playMusicPath + "/" + item;

				Toast.makeText(activity.getApplicationContext(), "声質を\"" + item + "\"に設定しました", Toast.LENGTH_LONG).show();
				startButton.setEnabled(true);
			}
		});

		// 保存先(一時的な保存)
		filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/audioTest.3gp";

		// 録音ボタン
		record_start = (ImageButton)v.findViewById(R.id.redording_start_button);
		record_start.setOnClickListener(this);

		// 再生・停止ボタン
		startButton = (ImageButton)v.findViewById(R.id.media_start_button);
		startButton.setOnClickListener(this);
		stopButton = (ImageButton)v.findViewById(R.id.media_stop_button);
		stopButton.setOnClickListener(this);

		// 削除ボタン
		deleteButton = (ImageButton)v.findViewById(R.id.delete_button);
		deleteButton.setOnClickListener(this);

		// ボタン状態の設定
		startButton.setEnabled(false);
		stopButton.setEnabled(false);

		return v;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		swIsChecked = isChecked;
		DBHelper.add("selfAreaSetting", String.valueOf(isChecked));

		if (isChecked) adapter.add(new Setting("地域", address));
		else adapter.delete("地域");
		((FragmentMain)(((MainActivity)activity).getKasasasuFragmentStatePagerAdapter().getItem(0))).updateLatLon(isChecked);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			// 録音ボタンが押されたときの処理
			case R.id.redording_start_button:

				// listSoundPathの初期化
				listSoundPath = "";
				// プログレスダイアログで録音中を知らせる
				progressDialog = new ProgressDialog(activity);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setMessage("録音中");
				progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"保存", new DialogInterface.OnClickListener() {
					// 録音終了
					@Override
					public void onClick(DialogInterface dialog,int which ){

						mediaRecorder.stop();
						startButton.setEnabled(true);
						progressDialog.dismiss();

						// ファイル名を指定して保存
						// alertDialogにEditTextを追加、入力に対応
						// AlertDialogのカスタム
						// （LayouInflater で指定したview(res/layoutに置いたxmlファイル)の取り込み）
						LayoutInflater inflater = LayoutInflater.from(activity);
						View view = inflater.inflate(R.layout.fragment_alertdialog, null);
						final EditText editText = (EditText)view.findViewById(R.id.editText);

						AlertDialog.Builder alertDialogBuilder  = new AlertDialog.Builder(activity);
						alertDialogBuilder.setMessage("ファイル名を入力して下さい");
						alertDialogBuilder.setView(view);
						alertDialogBuilder.setPositiveButton("保存",new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String fileName = editText.getText().toString();
								renameFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/" + fileName + ".3gp";
								Log.d("renameFilePath", renameFilePath);

								// ファイル名変更処理
								File renameFile = new File(renameFilePath);

								files = new File(playMusicPath).listFiles();

								if(files !=null) {
									for(int i = 0; i < files.length; i++) {
										if(files[i].getName().endsWith("audioTest.3gp")) {
											files[i].renameTo(renameFile);
											arrayAdapter.add(fileName + ".3gp");
											listSoundPath = renameFilePath;

										}

									}
								}

								Toast.makeText(activity.getApplicationContext(), "\"" + fileName + "\"を保存しました",Toast.LENGTH_LONG).show();

								alertDialog.dismiss();

							}
						});

						alertDialog = alertDialogBuilder.create();
						alertDialog.setCancelable(true);
						alertDialog.setCanceledOnTouchOutside(false);
						alertDialog.show();

					}

				});
				progressDialog.setButton(DialogInterface.BUTTON_POSITIVE,"キャンセル", new DialogInterface.OnClickListener() {
					// 録音終了
					@Override
					public void onClick(DialogInterface dialog,int which ){
						mediaRecorder.reset();

						// ボタン設定
						// 録音可能
						startButton.setEnabled(false);
						stopButton.setEnabled(false);
						record_start.setEnabled(true);
						progressDialog.dismiss();

					}

				});
				progressDialog.setCancelable(true);
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.show();

				// 録音の設定
				mediaRecorder = new MediaRecorder();
				mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

				// 保存先の設定
				mediaRecorder.setOutputFile(filePath);
				Log.d("filePath", filePath);

				// 録音開始
				try {
					mediaRecorder.prepare();

				} catch (IOException e) {
					e.printStackTrace();
				}
				mediaRecorder.start();
				break;

			// 再生
			case R.id.media_start_button:

				mediaPlayer = new MediaPlayer();

				try {
					// 声質を設定してない場合
					if(listSoundPath.isEmpty()) {
						mediaPlayer.setDataSource(filePath);
						Log.d("testes","filePath");
					} else { //声質を設定した場合
						mediaPlayer.setDataSource(listSoundPath);
						Log.d("testes", "listSoundPath");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				Log.d("test", "再生");
				try {
					mediaPlayer.prepare();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mediaPlayer.start();
				mediaPlayer.setLooping(true);
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				record_start.setEnabled(false);
				deleteButton.setEnabled(false);
				flag_start_button = true;
				break;

			// 停止
			case R.id.media_stop_button:

				mediaPlayer.stop();
				mediaPlayer.release();
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				record_start.setEnabled(true);
				deleteButton.setEnabled(true);
				flag_start_button = false;
				break;

			case R.id.delete_button:
				if(item != null) {
					arrayAdapter.remove(item);
					File deleteFile = new File(listSoundPath);
					Log.d("listSoundPath", item);
					Log.d("listSoundPath",listSoundPath);
					deleteFile.delete();

					Toast.makeText(activity.getApplicationContext(), "ファイル名：" + item + "を削除しました", Toast.LENGTH_LONG).show();
				}
				startButton.setEnabled(false);
				stopButton.setEnabled(false);

				break;

		}

	}

	@Override
	public void onStop() {
		super.onStop();
		if(flag_start_button) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}

		arrayAdapter.clear();
	}
}