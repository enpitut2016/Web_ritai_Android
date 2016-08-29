package com.example.kasasasu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
        Intent notification = new Intent(context, MainActivity.class);
        notification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(notification);
    }

}
