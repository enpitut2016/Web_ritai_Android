package com.example.kasasasu;

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
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shunpei on 16/08/26.
 */
public class SensorService extends Service implements SensorEventListener{

    private MediaPlayer mediaPlayer;
    private SensorManager sensorManager;
    public static double count;
    Date lastDate = new Date(0);
    public static String doubleString;
    Timer timer;
    private PendingIntent sender;
    private Calendar calendar;


    private Context context;
    private int waitperiod;


    @Override
    public void onCreate(){
        super.onCreate();

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final TimerTask check = new TimerTask(){
            public void run(){
                //Toast.makeText(this, doubleString, Toast.LENGTH_SHORT).show();
                //現在の時間を保存する
                Date date = new Date();
                if(count >= 5 && date.getTime() - lastDate.getTime() > 30000 ) {
                    Log.d("Tag", "double" + count);
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
                    audioPlay();
                }
            }
        };

        timer = new Timer();
        timer.schedule(check,0,10);
        Log.d("TAG", "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        Toast.makeText(this, "MyService#onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        count = Math.sqrt(Math.pow(event.values[1],2) + Math.pow(event.values[2],2));
        count = count * 100;
        // Double型をString型に変更
        BigDecimal bd = new BigDecimal(count);
        BigDecimal res = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
        doubleString = Double.toString(res.doubleValue());
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


}
