package com.example.kasasasu;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.app.AlertDialog;
import android.app.KeyguardManager;
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
import android.os.PowerManager;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class FragmentMain extends Fragment implements LocationListener/* View.OnClickListener, SensorEventListener*/ {
//public class FragmentMain extends Fragment {

//>>>>>>> origin/shunpei
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

//<<<<<<< HEAD
//<<<<<<< HEAD
//=======
	private int MY_PERMISSION_REQUEST_MULTI = 3;

	private Activity activity;
	private View v;

	private PowerManager.WakeLock wakeLock;
	private KeyguardManager.KeyguardLock keyguardLock;
//>>>>>>> origin/shunpei
//=======

	public static final int RAIN_PROB = 10;
	Handler handler= new Handler();

    private boolean flag;

//>>>>>>> MVP

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		activity = getActivity();
		v = inflater.inflate(R.layout.fragment_main, null);

        boolean need = getArguments().getBoolean("need");
        //HashMap<String, String> weather_results = (HashMap)(getArguments().getSerializable("weather_results"));
		weather_results = new HashMap<>();
        if (need){
            Log.d("need", "need");
            TextView v1 = (TextView) v.findViewById(R.id.tv1);
            v1.setText("傘が必要です。");
        }

		if ( getArguments().getInt("flag", 0) != 0) flag = true;
		else flag = false;
		Log.d("argument flag", String.valueOf(getArguments().getInt("flag", 0)));

        /*if(!(settings == null || weather_results.size() == 0)){
            Log.d("weather Hash", weather_results.toString());
            TextView v2 = (TextView) v.findViewById(R.id.tv2);
            v2.setText(weather_results.toString());
        }*/

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
                /*DBHelper.add("admin",locate.get("admin"));
                DBHelper.add("local",locate.get("local"));
                DBHelper.add("feature",locate.get("feature"));*/
                //locate.put("admin", address.getAdminArea());
                //locate.put("local", address.getLocality());


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
				flag = false;
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
        //if (latlon.containsKey("lat") && latlon.containsKey("lon"))mLocationManager.removeUpdates(this);
        if (latlon.containsKey("lat") && latlon.containsKey("lon")){
            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
            List<Address> addressList = null;
            try {
                //addressList = geocoder.getFromLocation(latlon.get("lat"),latlon.get("lon"),1);
                addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                Address address = addressList.get(0);
                //Log.d("test", "test");
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
				/*DBHelper.add("admin", address.getAdminArea());
				DBHelper.add("local", address.getLocality());
				DBHelper.add("feature", address.getFeatureName());*/
            } catch (IOException e) {
                e.printStackTrace();
            }
            mLocationManager.removeUpdates(this);
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


	/*@Override
	public void onLocationChanged(Location location){
		if (! latlon.containsKey("lat")) latlon.put("lat", location.getLatitude());
		if (! latlon.containsKey("lon")) latlon.put("lon", location.getLongitude());
		if (latlon.containsKey("lat") && latlon.containsKey("lon")){
            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
            List<Address> addressList = null;
            try {
                //addressList = geocoder.getFromLocation(latlon.get("lat"),latlon.get("lon"),1);
                addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                Address address = addressList.get(0);
                //Log.d("test", "test");
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

            } catch (IOException e) {
                e.printStackTrace();
            }

            mLocationManager.removeUpdates(this);
        }
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras){}

	@Override
	public void onProviderEnabled(String s){}

	@Override
	public void onProviderDisabled(String s){}

<<<<<<< HEAD
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
		}
	}/*

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

<<<<<<< HEAD

//=======
//>>>>>>> origin/shunpei

=======
>>>>>>> MVP
	private void judgeNeedOfUmb () {
		if (latlon.containsKey("lat") && latlon.containsKey("lon")) {
			final TextView tv1 = (TextView) v.findViewById(R.id.tv1);
            HttpGetTask task = null;
            try {
                task = new HttpGetTask(activity, weather_results, locate);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            task.execute();
			Log.d ("results", weather_results.toString());
			Log.d("latlon", latlon.toString());
<<<<<<< HEAD
/*<<<<<<< HEAD
            /*if(tv1.getText().equals("傘が必要です。")){
                audioPlay();
            }
=======*/



//>>>>>>> MVP

/*=======
			//audioPlay();
>>>>>>> origin/shunpei*/
		/*} else if (settings.containsKey("textSetting") && settings.get("textSetting").equals("on")) {
			Toast.makeText(activity, "位置設定を正しく入力してください。", Toast.LENGTH_LONG).show();
		}
		Log.d("frag", "judgeNeedOfUmb");
	}*/

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

                   /* handler.post(new Runnable() {
                        private boolean rainFlag;

                        public Runnable setRainFlag(boolean rainFlag) {
                            this.rainFlag = rainFlag;
                            return this;
                        }
                        @Override
                        public void run() {
                            if (rainFlag) {
                                tv1.setText("傘が必要です。");
                                audioPlay();
                            } else {
                                tv1.setText("傘は不必要です。");
                            }
                        }
                    }.setRainFlag(rainFlag));*/
                }
            }).start();
        }
    }

	@Override
	public void onDestroy() {
		if (flag) {
			activity.startService(new Intent(activity, SensorService.class));
			Log.d("ondestroy", "ondestroy");
		}
		super.onDestroy();
	}

}
