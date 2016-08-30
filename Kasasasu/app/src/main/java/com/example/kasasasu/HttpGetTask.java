package com.example.kasasasu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by 真史 on 2016/04/23.
 */
public class HttpGetTask extends AsyncTask<Void, Void, HashMap<String, String>>{
	//private TextView mTextView;
	private JSONObject jsonObject;
	private Activity mParentActivity;
	private ProgressDialog mDialog = null;
	public static final int CNT = 8;
	//public static final int RAIN_PROB = 50;
	private String encodedString_admin, encodedString_admin2;
	private String encodedString_local, encodedString_local2;
	private String encodedString_feature, encodedString_feature2;
    private HashMap<String, String> weather_results;

    private String hour, hour2;
    private String prob, prob2;
    private String temperature, temperature2;
    //private MediaPlayer mediaPlayer;

	private String mUri, mUri2;
    private KasasasuSQLiteOpenHelper DBHelper;

	public HttpGetTask(Activity parentActivity, HashMap<String, String> weather_results, HashMap<String, String> location, String resultPref, String resultCity, String resultFeature) throws UnsupportedEncodingException {
		mParentActivity = parentActivity;
		//mTextView = textView;
		this.weather_results = weather_results;


        try {
            encodedString_admin = URLEncoder.encode(location.get("admin"), "UTF-8");
            encodedString_local = URLEncoder.encode(location.get("local"), "UTF-8");
            encodedString_feature = URLEncoder.encode(location.get("feature"), "UTF-8");
            mUri = "https://shrouded-forest-60165.herokuapp.com/?pref=" + encodedString_admin + "&city=" +encodedString_local;
            if(location.get("feature").matches(".*"+"区")){
                mUri = mUri + encodedString_feature;
            }
			/////////////////////////////////////   Tera   //////////////////////////////////////

			encodedString_admin2 = URLEncoder.encode(resultPref, "UTF-8");
			encodedString_local2 = URLEncoder.encode(resultCity, "UTF-8");
			encodedString_feature2 = URLEncoder.encode(resultFeature, "UTF-8");
			mUri2 = "https://shrouded-forest-60165.herokuapp.com/?pref=" + encodedString_admin2 + "&city=" +encodedString_local2;
			if(location.get("feature").matches(".*"+"区")){
				mUri2 = mUri2 + encodedString_feature;
			}
			/////////////////////////////////////////////////////////////////////////////////////
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //mUri = "http://api.openweathermap.org/data/2.5/forecast?lat=" + latlon.get("lat") + /*36.111165*/ "&lon=" + latlon.get("lon") + /*140.099988*/"&APPID=ee1e260c476e8337f1f07ebc11b8f32c&cnt=" + CNT;

		Log.d("admin / local", location.get("admin") + "/" + location.get("local"));
	}

	@Override
	protected void onPreExecute(){
		/*mDialog = new ProgressDialog(mParentActivity);
		mDialog.setMessage("通信中・・・");
		mDialog.show();*/
	}

	@Override
	protected HashMap doInBackground(Void... arg0){
		return exec_get();
	}

	@Override
	protected void onPostExecute(HashMap<String, String> weather_results) {
        //mDialog.dismiss();

        //Log.d("size", String.valueOf(weather_results.size()));
       /* for (String key : weather_results.keySet()) {
            String str = weather_results.get(key);
            int prob = Integer.parseInt(String.valueOf(str.split("/")[0]));
            double temperature = Double.parseDouble(String.valueOf(str.split("/")[1]));
            Log.d("split test", str.split(" / ")[0]);

            if (prob > RAIN_PROB) {
                //if (false) {
                //this.mTextView.setText("傘が必要です。");
                break;
            } else {
                //this.mTextView.setText("傘は不必要です。");
            }
<<<<<<< HEAD
        }
=======
        }*/
//>>>>>>> MVP
        //if(this.mTextView.getText().equals("傘が必要です。")) audioPlay();
	}

	private HashMap exec_get(){
		HttpURLConnection http = null;
		InputStream in = null;

        //weather_results = new HashMap<>();
		//String src ="";
		//ArrayList<Integer> weatherIds = new ArrayList<>();
		try{
			URL url = new URL(mUri);
            Log.d("http url", mUri);
			InputStream is = url.openConnection().getInputStream();

			// JSON形式で結果が返るためパースのためにStringに変換する
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line;
			while (null != (line = reader.readLine())) {
				sb.append(line);
			}
			String data = sb.toString();
            Log.d("response",data);

			JSONObject rootObj = new JSONObject(data);
			JSONArray listArray = rootObj.getJSONArray("weathers");
            Log.d("json list" ,listArray.toString());

			//////////////////tera//////////////////////////////////////
			/*URL url2 = new URL(mUri2);
			InputStream is2 = url2.openConnection().getInputStream();

			BufferedReader reader2 = new BufferedReader(new InputStreamReader(is2, "UTF-8"));
			StringBuilder sb2 = new StringBuilder();
			String line2;

			while (null != (line2 = reader2.readLine())) {
				sb2.append(line2);
			}
			String data2 = sb2.toString();

			Log.d("response",data2);



			JSONObject rootObj2 = new JSONObject(data2);
			JSONArray listArray2 = rootObj2.getJSONArray("weathers");
			Log.d("json list" ,listArray2.toString());*/
/////////////////////////////////////////////////////////////////////////////////////////

			DBHelper = new KasasasuSQLiteOpenHelper(mParentActivity);
			for (int i=0; i<CNT; i++) {
				JSONObject obj = listArray.getJSONObject(i);

                hour = obj.getString("hour");
                prob = obj.getString("prob");
                temperature = obj.getString("temperature");
                Log.d("json result", "hour:"+hour+ " prob:"+prob+ " temperature:"+temperature);
                DBHelper.add(hour,prob+"/"+temperature);

				////////////////////////////////////////   Tera   ////////////////////////////////////////////////
				/*JSONObject obj2 = listArray2.getJSONObject(i);
				hour2 = obj2.getString("hour");
				prob2 = obj2.getString("prob");
				temperature2 = obj2.getString("temperature");
				Log.d("json result", "hour:"+hour2+ " prob:"+prob2+ " temperature:"+temperature2);
				//DBHelper = new KasasasuSQLiteOpenHelper(mParentActivity);
				DBHelper.add(hour2+"2",prob2+"/"+temperature2);*/
				//weather_results.put(hour2+"2",prob2+"/"+temperature2);
				//////////////////////////////////////////////////////////////////////////////////////////////////
			}
            weather_results.put("finished","0/0");

		}catch (Exception e){
			e.printStackTrace();
		}finally {
			try{
				if (http != null){
					http.disconnect();
				}
				if (in != null){
					in.close();
				}
			}catch (Exception ignored){

			}
		}
		return weather_results;
	}


   /* private void audioPlay() {
>>>>>>> MVP
        mediaPlayer = new MediaPlayer();
        String filePath = "rain.mp3";

        try {
            AssetFileDescriptor afdescripter = mParentActivity.getAssets().openFd(filePath);

            mediaPlayer.setDataSource(afdescripter.getFileDescriptor(),
                    afdescripter.getStartOffset(),
                    afdescripter.getLength());

            mParentActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        mediaPlayer.start();
    }*/
}
