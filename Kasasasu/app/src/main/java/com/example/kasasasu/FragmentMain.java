package com.example.kasasasu;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FragmentMain extends Fragment implements LocationListener, View.OnClickListener, SensorEventListener {
	private HashMap<String, Double> latlon;
	private LocationManager mLocationManager;
	private KasasasuSQLiteOpenHelper DBHelper;
	private HashMap<String, String> settings;
	private View v;
	private Button button;
	private Activity activity;

	private SensorManager sensorManager;
	private double count;
	private TextView textView;
	private MediaPlayer mediaPlayer;
	private Date lastDate = new Date(0);

	private int MY_PERMISSION_REQUEST_MULTI = 3;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		activity = getActivity();
		v = inflater.inflate(R.layout.fragment_main, null);

		button = (Button)v.findViewById(R.id.button);
		button.setOnClickListener(this);

		latlon = new HashMap<>();
		DBHelper = new KasasasuSQLiteOpenHelper(activity);
		settings = DBHelper.get();
		Log.d("Start","start4");

		if (PermissionChecker.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED){
			new AlertDialog.Builder(activity)
					.setTitle("アプリケーション権限について")
					.setMessage("以下アプリ権限を許可してください" + "\n"
							+ "・位置情報取得権限")
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@TargetApi(Build.VERSION_CODES.M)
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							//パーミッション許可取得
							requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_MULTI);
						}
					})
					.create()
					.show();
			return v;
		}

		if (settings.containsKey("textSetting") && settings.get("textSetting").equals("on")) {
			Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
			try{
				List<Address> addressList = geocoder.getFromLocationName(settings.get("prefecture") + settings.get("city"), 1);
				Address address = addressList.get(0);

				double lat;
				double lon;
				lat = address.getLatitude();
				lon = address.getLongitude();
				latlon.put("lat", lat);
				latlon.put("lon", lon);
				Log.d("geocode", lat + "/" + lon);
			}catch(IOException e){
				e.printStackTrace();
			}
		} else {

			mLocationManager = (LocationManager) activity.getSystemService(activity.LOCATION_SERVICE);
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		}

		sensorManager = (SensorManager)activity.getSystemService(activity.SENSOR_SERVICE);
		textView = (TextView) v.findViewById(R.id.tv2);

		Button startButton = (Button) v.findViewById(R.id.start_button);
		startButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.startService( new Intent( activity, SensorService.class ) );
			}

		});


		Button stopButton = (Button) v.findViewById(R.id.stop_button);
		stopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				activity.stopService( new Intent( activity, SensorService.class ) );
				NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.cancel(0);
			}
		});

		return v;
	}

	public void updateLatLon (boolean settingIsText, String strAddress) {
		if (settingIsText) {
			Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

			try{
				List<Address> addressList = geocoder.getFromLocationName(strAddress, 1);
				Address address = addressList.get(0);

				double lat;
				double lon;
				lat = address.getLatitude();
				lon = address.getLongitude();
				latlon.put("lat", lat);
				latlon.put("lon", lon);
				settings.put("textSetting", "on");
				Log.d("geocode", lat + "/" + lon);
			}catch(IOException e){
				e.printStackTrace();
			}
		} else {
			latlon.clear();
			mLocationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		}
	}

	@Override
	public void onClick(View v) {
		/*if (latlon.containsKey("lat") && latlon.containsKey("lon")) {
			TextView tv1 = (TextView) this.v.findViewById(R.id.tv1);
			HttpGetTask task = new HttpGetTask(getActivity(), tv1, latlon);
			task.execute();
			Log.d("latlon", latlon.toString());
		} else if (settings.containsKey("textSetting") && settings.get("textSetting").equals("on")) {
			Toast.makeText(getActivity(), "位置設定を正しく入力してください。", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getActivity(), "少し時間をおいて再度ボタンを押してください。", Toast.LENGTH_LONG).show();
		}*/
		Log.d("frag", "onclick");

		List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
		if(sensors.size() > 0) {
			Sensor s = sensors.get(0);
			sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	public void onLocationChanged(Location location){
		if (! latlon.containsKey("lat")) latlon.put("lat", location.getLatitude());
		if (! latlon.containsKey("lon")) latlon.put("lon", location.getLongitude());
		if (latlon.containsKey("lat") && latlon.containsKey("lon"))mLocationManager.removeUpdates(this);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras){}

	@Override
	public void onProviderEnabled(String s){}

	@Override
	public void onProviderDisabled(String s){}

	@Override
	public void onStop() {
		super.onStop();

		sensorManager.unregisterListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		/*List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
		if(sensors.size() > 0) {
			Sensor s = sensors.get(0);
			sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
		}*/
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		count = Math.sqrt(Math.pow( event.values[1],2) + Math.pow( event.values[2],2));
		count = count * 100;
		BigDecimal bd = new BigDecimal(count);
		BigDecimal res = bd.setScale(3, BigDecimal.ROUND_HALF_UP);

		String doubleString = Double.toString(res.doubleValue());
		textView.setText(doubleString);

		Date date = new Date();

		if(count >= 5 && date.getTime() - lastDate.getTime() > 20000 ){
			lastDate = new Date();
			judgeNeedOfUmb();
		}
	}

	private void audioPlay() {
		mediaPlayer = new MediaPlayer();
		String filePath = "rain.mp3";

		try {
			AssetFileDescriptor afdescripter = activity.getAssets().openFd(filePath);

			mediaPlayer.setDataSource(afdescripter.getFileDescriptor(),
					afdescripter.getStartOffset(),
					afdescripter.getLength());

			activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer.prepare();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		mediaPlayer.start();
	}

	private void judgeNeedOfUmb () {
		if (latlon.containsKey("lat") && latlon.containsKey("lon")) {
			TextView tv1 = (TextView) v.findViewById(R.id.tv1);
			HttpGetTask task = new HttpGetTask(activity, tv1, latlon);
			task.execute();
			Log.d("latlon", latlon.toString());
			//audioPlay();
		} else if (settings.containsKey("textSetting") && settings.get("textSetting").equals("on")) {
			Toast.makeText(activity, "位置設定を正しく入力してください。", Toast.LENGTH_LONG).show();
		}
		Log.d("frag", "judgeNeedOfUmb");
	}
}
