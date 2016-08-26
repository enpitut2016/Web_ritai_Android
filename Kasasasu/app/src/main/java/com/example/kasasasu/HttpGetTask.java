package com.example.kasasasu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
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
public class HttpGetTask extends AsyncTask<Void, Void, Integer>{
	private TextView mTextView;
	private Activity mParentActivity;
	private ProgressDialog mDialog = null;
	public static final int CNT = 7;
	public static final int RAIN_ID = 500;
    private String encodedString_ibaraki;
    private String encodedString_tsukuba;

	private String mUri;

	public HttpGetTask(Activity parentActivity, TextView textView, HashMap<String, String> location) throws UnsupportedEncodingException {
		mParentActivity = parentActivity;
		mTextView = textView;

        try {
            encodedString_ibaraki = URLEncoder.encode(location.get("admin"), "UTF-8");
            encodedString_tsukuba = URLEncoder.encode(location.get("local"), "UTF-8");
            mUri = "https://shrouded-forest-60165.herokuapp.com/?pref=" + encodedString_ibaraki + "&city=" +encodedString_tsukuba;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //mUri = "http://api.openweathermap.org/data/2.5/forecast?lat=" + latlon.get("lat") + /*36.111165*/ "&lon=" + latlon.get("lon") + /*140.099988*/"&APPID=ee1e260c476e8337f1f07ebc11b8f32c&cnt=" + CNT;

		Log.d("admin / local", location.get("admin") + "/" + location.get("local"));
	}

	@Override
	protected void onPreExecute(){
		mDialog = new ProgressDialog(mParentActivity);
		mDialog.setMessage("通信中・・・");
		mDialog.show();
	}

	@Override
	protected Integer doInBackground(Void... arg0){
		return exec_get();
	}

	@Override
	protected void onPostExecute(Integer weatherId){
		mDialog.dismiss();
		//if (weatherId.equals(new Integer(RAIN_ID))) {
        if (false) {
			this.mTextView.setText("傘が必要です。");
		}
        else{
            this.mTextView.setText("test");
        }
	}

	private Integer exec_get(){
		HttpURLConnection http = null;
		InputStream in = null;
		//String src ="";
		//ArrayList<Integer> weatherIds = new ArrayList<>();
		try{
			URL url = new URL(mUri);
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

			/*
            for (int i=0; i<CNT; i++) {
				JSONObject obj = listArray.getJSONObject(i);
				// 地点名
				//String cityName = obj.getString("name");

				// 気温(Kから℃に変換)
				JSONObject mainObj = obj.getJSONObject("main");
				//float currentTemp = (float) (mainObj.getDouble("temp") - 273.15f);
				//float minTemp = (float) (mainObj.getDouble("temp_min") - 273.15f);
				//float maxTemp = (float) (mainObj.getDouble("temp_max") - 273.15f);

				// 湿度
				if (mainObj.has("humidity")) {
					int humidity = mainObj.getInt("humidity");
				}

				// 取得時間
				//long time = obj.getLong("dt");

				// 天気
				JSONArray weatherArray = obj.getJSONArray("weather");
				JSONObject weatherObj = weatherArray.getJSONObject(0);
				String iconId = weatherObj.getString("icon");
				//weatherIds.add(new Integer(weatherObj.getInt("id")));

				Integer weatherId = new Integer(weatherObj.getInt("id"));
				//Log.d("time", obj.getString("dt_txt"));
				if (weatherId == 500) {
					return weatherId;
				}
			}*/

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
		return new Integer(0);
	}
}
