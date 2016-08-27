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

public class FragmentMain extends Fragment {
	/*
	private HashMap<String, Double> latlon;
	private LocationManager mLocationManager;
	private KasasasuSQLiteOpenHelper DBHelper;
	private HashMap<String, String> settings;

	private Button button;


	private SensorManager sensorManager;
	private double count;
	private TextView textView;
	private MediaPlayer mediaPlayer;
	private Date lastDate = new Date(0);

	private int MY_PERMISSION_REQUEST_MULTI = 3;
	*/
	private Activity activity;
	private View v;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		activity = getActivity();
		v = inflater.inflate(R.layout.fragment_main, null);

		/*
		latlon = new HashMap<>();
		DBHelper = new KasasasuSQLiteOpenHelper(activity);
		settings = DBHelper.get();

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
		*/

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
/*
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
*/

}
