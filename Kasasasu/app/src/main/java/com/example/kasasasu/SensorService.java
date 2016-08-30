package com.example.kasasasu;

/*Service化を行うクラス
* センサー値を常にとり続ける
* 取得のセンサー値が閾値以上の値になったらMadiaPleyerから音声を流す
*/

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shunpei on 16/08/26.
 */
public class SensorService extends Service implements SensorEventListener, LocationListener {

    private MediaPlayer mediaPlayer;
    private SensorManager sensorManager;
    double count;
    Date lastDate = new Date(0);
    Timer timer;
    private Activity activity;
    private View v;
    private boolean rainFlag = false;

    private HashMap<String, Double> latlon;
    private HashMap<String, String> locate;
    private LocationManager mLocationManager;
    private KasasasuSQLiteOpenHelper DBHelper;
    private HashMap<String, String> settings;


    //閾値以上の値の時の画面呼び起こしを行う
    private PendingIntent sender;
    private Calendar calendar;
    private Context context;

    private HashMap<String, String> weather_results;
    public static final int RAIN_PROB = 10;
    //private MediaPlayer mediaPlayer;
    Handler handler= new Handler();

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    private int waitperiod;


    @Override
    public void onCreate(){
        super.onCreate();
        //audioPlay();
        activity = getActivity();
        context = getApplicationContext();
        DBHelper = new KasasasuSQLiteOpenHelper(this);
        //Log.d("activity",activity.toString());
        //サービス化開始の際にタブレット上部に通知を行うPostnotificationクラスのインスタンスを生成
        PostNotification send = new PostNotification(this);
        send.postNotification_send();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        List<Sensor> sensors = sensorManager.getSensorList(android.hardware.Sensor.TYPE_LINEAR_ACCELERATION);
        if(sensors.size() > 0) {
            android.hardware.Sensor s = sensors.get(0);
            sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
            //context = getApplicationContext();
            //waitperiod = 000; // 5sec
        }


        /*latlon = new HashMap<>();
        locate = new HashMap<>();
        weather_results = new HashMap<>();

        DBHelper = new KasasasuSQLiteOpenHelper(this);
        settings = DBHelper.get();

        Log.d("settings", settings.toString());
        if (settings.containsKey("selfAreaSetting") && settings.get("selfAreaSetting").equals("true")) {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
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

            }catch(IOException e){
                e.printStackTrace();
            }
        } else {
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        }*/


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        Toast.makeText(this, "Stop Service", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
		return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub

        //加速度センサのy軸、z軸の値から閾値を設定
        //閾値がみやすいように100倍
        count = Math.sqrt(Math.pow(event.values[1],2) + Math.pow(event.values[2],2));
        count = count * 100;
        //count値が変わるときにメソッドを呼びだす
        checkThreshold(count);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    private void audioPlay() {
        // インタンスを生成
        mediaPlayer = new MediaPlayer();
        //音楽ファイル名, あるいはパス
        String filePath = "rain.mp3";
        try {
            // assetsから mp3 ファイルを読み込み
            AssetFileDescriptor afdescripter = getAssets().openFd(filePath);
            //MediaPlayerに読み込んだ音楽ファイルを指定
            mediaPlayer.setDataSource(afdescripter.getFileDescriptor(),
                    afdescripter.getStartOffset(),
                    afdescripter.getLength());
            mediaPlayer.prepare();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        // 再生する
        mediaPlayer.start();
    }

/*

    void restart(Context cnt, int period){
        // intent 設定で自分自身のクラスを設定
        Intent mainActivity = new Intent(cnt, TestView.class);
        // PendingIntent , ID=0
        PendingIntent pendingIntent = PendingIntent.getActivity(cnt, 0, mainActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        // AlarmManager のインスタンス生成
        AlarmManager alarmManager = (AlarmManager)cnt.getSystemService(Context.ALARM_SERVICE);
        // １回のアラームを現在の時間からperiod（５秒）後に実行させる
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + period, pendingIntent);

    }
*/

    public void checkThreshold(double count){
        //現在の時間を保存する
        Date date = new Date();
        //count：閾値　センサ値が閾値以上かつ最後の通知から30秒後にはいるループ
        if(count >= 5 && date.getTime() - lastDate.getTime() > 10000 ) {
            Log.d("Tag", "double" + count);
            //judgeNeedOfUmb();
            boolean need;
            need = Boolean.valueOf(DBHelper.get().get("need"));
            //if(need) audioPlay();
            if(DBHelper.get().get("need").equals("true")) audioPlay();
            else Log.d("false", "false");

            //音声を流した時間を取得しておく
            lastDate = new Date();
                        Intent i = new Intent(getApplicationContext(), Reciver.class);
                        i.putExtra("need",need);

                        //i.putExtra("weather_results",weather_results);
                        Log.d("sensor need", String.valueOf(rainFlag));
                        //Log.d("sensor Hash", weather_results.toString());

                        sender = PendingIntent.getBroadcast(getBaseContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

                        calendar = Calendar.getInstance();
                        //calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.setTimeInMillis(0);
                        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            //sensorManager.unregisterListener(this);
            stopSelf();

/*                        String tmp = "Calendar: " + calendar.get(Calendar.YEAR) + "/"
                                + (calendar.get(Calendar.MONTH)) + "/" + calendar.get(Calendar.DATE)
                                + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                                + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);

                        Log.d("Tag",tmp);*/


            //音声再生メソッド

            /*
            try{
                Thread.sleep(5000);
        }catch(Exception e){}
        */

           // audioPlay();
        }
    }

    private void judgeNeedOfUmb () {
        if (latlon.containsKey("lat") && latlon.containsKey("lon")) {
            //TextView tv1 = (TextView) v.findViewById(R.id.tv1);
            HttpGetTask task = null;
            try {
                task = new HttpGetTask(null, weather_results, locate);
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
                    for (String key : weather_results.keySet()) {
                        String str = weather_results.get(key);
                        int prob = Integer.parseInt(String.valueOf(str.split("/")[0]));
                        double temperature = Double.parseDouble(String.valueOf(str.split("/")[1]));
                        Log.d("split test", str.split(" / ")[0]);

                        if (prob > RAIN_PROB) {
                            rainFlag = true;
                            audioPlay();
                            break;
                        }
                    }
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
    public void onLocationChanged(Location location){
        if (! latlon.containsKey("lat")) latlon.put("lat", location.getLatitude());
        if (! latlon.containsKey("lon")) latlon.put("lon", location.getLongitude());
        //if (latlon.containsKey("lat") && latlon.containsKey("lon"))mLocationManager.removeUpdates(this);
        if (latlon.containsKey("lat") && latlon.containsKey("lon")){
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
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
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    public Activity getActivity() {
        return activity;
    }
}
