package com.example.kasasasu;

import android.app.Activity;
import android.content.Context;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class FragmentMain extends Fragment implements LocationListener {
	private HashMap<String, Double> latlon;
    private HashMap<String, String> locate;
    private HashMap<String, String> weather_results;
    private boolean rainFlag = false;
	private LocationManager mLocationManager;
	private KasasasuSQLiteOpenHelper DBHelper;
	private HashMap<String, String> settings;
	private Button button;
	private SensorManager sensorManager;
	private double count;
	private TextView textView;
	private Date lastDate = new Date(0);
	private int MY_PERMISSION_REQUEST_MULTI = 3;
	private Activity activity;
	private View v;
	private PowerManager.WakeLock wakeLock;
	private KeyguardManager.KeyguardLock keyguardLock;
	public static final int RAIN_PROB = 10;
	private Handler handler= new Handler();
    private boolean serviceRestartFlag;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		activity = getActivity();
		v = inflater.inflate(R.layout.fragment_main, null);

        boolean need = getArguments().getBoolean("need");
        weather_results = new HashMap<>();
        if (need){
            Log.d("need", "need");
            TextView v1 = (TextView) v.findViewById(R.id.tv1);
            v1.setText("傘が必要です。");
        }

		if ( getArguments().getInt("flag", 0) != 0) serviceRestartFlag = true;
		else serviceRestartFlag = false;
		Log.d("argument flag", String.valueOf(getArguments().getInt("flag", 0)));
		Log.d("Tag","FragmentMain");

		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
				WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		latlon = new HashMap<>();
        locate = new HashMap<>();

		DBHelper = new KasasasuSQLiteOpenHelper(activity);
		settings = DBHelper.get();

        Log.d("settings", settings.toString());
        if (settings.containsKey("selfAreaSetting") && settings.get("selfAreaSetting").equals("true")) {
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
                Log.d("address get", address.getAdminArea() );
                Log.d("address get", address.getLocality() );
                Log.d("address get", address.getFeatureName());
                locate.put("admin", address.getAdminArea());
                locate.put("local", address.getLocality());
                locate.put("feature", address.getFeatureName());

                judgeNeedOfUmb();
			}catch(IOException e){
				e.printStackTrace();
			}
		} else {
			mLocationManager = (LocationManager) activity.getSystemService(activity.LOCATION_SERVICE);
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		}

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
				serviceRestartFlag = false;
				activity.stopService( new Intent( activity, SensorService.class ) );
				NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.cancel(0);
			}
		});

		return v;
	}

    @Override
    public void onLocationChanged(Location location){
        if (! latlon.containsKey("lat")) latlon.put("lat", location.getLatitude());
        if (! latlon.containsKey("lon")) latlon.put("lon", location.getLongitude());
        if (latlon.containsKey("lat") && latlon.containsKey("lon")){
            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                Address address = addressList.get(0);
                Log.d("address get wifi", address.getAddressLine(1) );
                Log.d("address get wifi", address.toString() );
                addressList = geocoder.getFromLocationName(address.getAddressLine(1),1);
                address = addressList.get(0);
                Log.d("address get wifi", address.getAdminArea() );
                Log.d("address get wifi", address.getLocality() );
                Log.d("address get wifi", address.getFeatureName());

                locate.put("admin", address.getAdminArea());
                locate.put("local", address.getLocality());
                locate.put("feature", address.getFeatureName());
				mLocationManager.removeUpdates(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

	public void updateLatLon (boolean settingIsText) {
		if (settingIsText) {
			Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

			try{
				HashMap<String, String> DBData = DBHelper.get();
				List<Address> addressList = geocoder.getFromLocationName(DBData.get("prefecture") + DBData.get("city"), 1);
				Address address = addressList.get(0);

				double lat;
				double lon;
				lat = address.getLatitude();
				lon = address.getLongitude();
				latlon.put("lat", lat);
				latlon.put("lon", lon);
				settings.put("textSetting", "on");
				Log.d("geocode", lat + "/" + lon);
                Log.d("address get", address.toString());
                Log.d("address get", address.getAdminArea() );
                Log.d("address get", address.getLocality() );
                Log.d("address get", address.getFeatureName());
                locate.put("admin", address.getAdminArea());
                locate.put("local", address.getLocality());
                locate.put("feature", address.getFeatureName());
			}catch(IOException e){
				e.printStackTrace();
			}
		} else {
			latlon.clear();
            locate.clear();
			mLocationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		}
	}

    private void judgeNeedOfUmb () {
        if (latlon.containsKey("lat") && latlon.containsKey("lon")) {
            //TextView tv1 = (TextView) v.findViewById(R.id.tv1);
            HttpGetTask task = null;
            try {
                task = new HttpGetTask(activity, weather_results, locate);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            task.execute();
            Log.d("latlon", latlon.toString());

            new Thread(new Runnable() {

                @Override
                public void run() {
                    while (! weather_results.containsKey("finished")) {}
                    rainFlag = false;
                    settings = DBHelper.get();
                    for (String key : settings.keySet()) {
                        if(key.matches("^[0-9]{2}")){
                            String str = settings.get(key);

                            int prob = Integer.parseInt(String.valueOf(str.split("/")[0]));
                            double temperature = Double.parseDouble(String.valueOf(str.split("/")[1]));
                            Log.d("split test", str.split(" / ")[0]);

                            if (prob > RAIN_PROB) {
                                rainFlag = true;
                                break;
                            }
                        }
                    }
                    DBHelper.add("need",String.valueOf(rainFlag));
                    Log.d("main flag", String.valueOf(rainFlag));
                    weather_results.remove("finished");
                }
            }).start();
        }
    }

	@Override
	public void onDestroy() {
		if (serviceRestartFlag) {
			activity.startService(new Intent(activity, SensorService.class));
			Log.d("ondestroy", "ondestroy");
		}
		super.onDestroy();
	}
}
