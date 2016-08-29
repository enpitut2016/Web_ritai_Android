package com.example.kasasasu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by shunpei on 16/08/26.
 */
/**
 *画面を呼び起こすためのレシーバーのクラス
 *
 */

public class Reciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Log.d("Tag","finish");
        Log.d("Tag",intent.toString());
        boolean rainFlag = intent.getBooleanExtra("need", false);
        //HashMap <String, String> weather_results;
        //weather_results = (HashMap)intent.getSerializableExtra("weather_results");
        Intent notification = new Intent(context, MainActivity.class);
        notification.putExtra("need",rainFlag);
        //notification.putExtra("weather_results",weather_results);
        Log.d("reciver",String.valueOf(rainFlag));
        //Log.d("reciver Hash",weather_results.toString());

        notification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(notification);
    }

}
