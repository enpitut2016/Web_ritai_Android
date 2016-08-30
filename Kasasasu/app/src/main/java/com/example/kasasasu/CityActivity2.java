package com.example.kasasasu;

/**
 * Created by shunpei on 16/08/29.
 */
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class CityActivity2 extends Activity {

    private KasasasuSQLiteOpenHelper DBHelper;
    private String pref;
    private String prefRoman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        Intent intent = getIntent();
        pref = intent.getStringExtra("pref");
        final String ymd = intent.getStringExtra("YMD");
        Log.e("おおおおおおおおおお",ymd);
        Geocoder geocoder = new Geocoder(this, Locale.US);
        try{
            List<Address> addressList = geocoder.getFromLocationName(pref, 1);
            Address address = addressList.get(0);
            Log.d("geocode", address.toString());
            prefRoman = address.getAdminArea().split(" ")[0].toLowerCase();
        }catch(IOException e){
            e.printStackTrace();
        }

        Log.d("city id", pref);
        String[] cities = getResources().getStringArray(getResources().getIdentifier(prefRoman + "_cities", "array", getPackageName()));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, cities);

        // リストビューにアイテム (adapter) を追加
        ListView cityListView = (ListView)findViewById(R.id.cityListView);
        cityListView.setAdapter(adapter);

        // アイテムクリック時ののイベントを追加
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View view, int pos, long id) {
                // 選択アイテムを取得
                ListView listView = (ListView)parent;
                String city = (String)listView.getItemAtPosition(pos);

                final SQLiteDatabase db = DBHelper.getWritableDatabase();

                db.delete("mokutekichi", "date = '"+ymd+"'", null);


                ContentValues values = new ContentValues();
                values.put("date", ymd);
                values.put("goalpref", pref);
                values.put("goalcity", city);
                db.insert("mokutekichi", null, values);
                //DBHelper.add("prefecture", pref);
                //DBHelper.add("city", city);
                Log.e("aaaaaaaaaa  "+ymd,"bbbbbbbbbb  "+pref+city);
                Intent intent = new Intent(CityActivity2.this, MainActivity.class);
                intent.putExtra("fragment", 2);
                startActivity(intent);

                // 通知ダイアログを表示
                Toast.makeText(CityActivity2.this, city, Toast.LENGTH_LONG).show();
            }
        });

        DBHelper = new KasasasuSQLiteOpenHelper(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_city, menu);
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
}

