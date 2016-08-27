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
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
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

    private HashMap<String, Double> latlon;
    private LocationManager mLocationManager;
    private KasasasuSQLiteOpenHelper DBHelper;
    private HashMap<String, String> settings;


    //閾値以上の値の時の画面呼び起こしを行う
    private PendingIntent sender;
    private Calendar calendar;
    private Context context;
    private int waitperiod;


    @Override
    public void onCreate(){
        super.onCreate();
        activity = getActivity();
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


        latlon = new HashMap<>();
        DBHelper = new KasasasuSQLiteOpenHelper(this);
        settings = DBHelper.get();

        if (settings.containsKey("textSetting") && settings.get("textSetting").equals("on")) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }


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
        if(count >= 5 && date.getTime() - lastDate.getTime() > 30000 ) {
            Log.d("Tag", "double" + count);
            //音声を流した時間を取得しておく
            lastDate = new Date();
                      /*  Intent i = new Intent(getApplicationContext(), Reciver.class);
                        sender = PendingIntent.getBroadcast(getBaseContext(), 0, i, 0);
                        calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int hour = calendar.get(Calendar.HOUR);
                        int minute = calendar.get(Calendar.MINUTE);
                        //現在時刻をセット
                        //calendar.set(year, month, day, hour, minute);
                        calendar.set(year, month, day, hour,minute);
                        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                        String tmp = "Calendar: " + calendar.get(Calendar.YEAR) + "/"
                                + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE)
                                + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                                + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
                        Log.d("Tag",tmp);*/

            //音声再生メソッド
            audioPlay();
        }
    }

    private void judgeNeedOfUmb () {
        if (latlon.containsKey("lat") && latlon.containsKey("lon")) {
            TextView tv1 = (TextView) v.findViewById(R.id.tv1);
            HttpGetTask task = new HttpGetTask(activity, tv1, latlon);
            task.execute();
            Log.d("latlon", latlon.toString());
        } else if (settings.containsKey("textSetting") && settings.get("textSetting").equals("on")) {
            Toast.makeText(activity, "位置設定を正しく入力してください。", Toast.LENGTH_LONG).show();
        }
        Log.d("frag", "judgeNeedOfUmb");
    }

    @Override
    public void onLocationChanged(Location location){
        if (! latlon.containsKey("lat")) latlon.put("lat", location.getLatitude());
        if (! latlon.containsKey("lon")) latlon.put("lon", location.getLongitude());
        if (latlon.containsKey("lat") && latlon.containsKey("lon"))mLocationManager.removeUpdates(this);
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
