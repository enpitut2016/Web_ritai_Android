package com.example.kasasasu;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends FragmentActivity {

	private ViewPager viewPager;
	private int MY_PERMISSION_REQUEST_MULTI = 3;
	private PowerManager.WakeLock wakeLock;
	private KeyguardManager.KeyguardLock keyguardLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	wakeLock =((PowerManager)getSystemService(Context.POWER_SERVICE))
				.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
						|PowerManager.PARTIAL_WAKE_LOCK
						|PowerManager.ON_AFTER_RELEASE,"disableLock");

		wakeLock.acquire();
		/*//画面ロックを解除
		KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
		keyguardLock = keyguardManager.newKeyguardLock("disableLock");
		keyguardLock.disableKeyguard();
*/

		//位置情報のアプリ権限が許可されていない場合は許可を促す
		if (PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED){
			new AlertDialog.Builder(this)
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
					.create().show();
			return ;
		}

		ActionBar actionBar = getActionBar();
		actionBar.setLogo(R.drawable.logo);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		Log.d("Tag","test");
		//MediaPlayerの音量調整を端末でできるようにする
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter( new KasasasuFragmentStatePagerAdapter( getSupportFragmentManager()));

		wakeLock.release();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void permission(){

	}
}
