package com.example.calendar3;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.String;


public class ScheduleInput extends AppCompatActivity {

    private EditText editText;
    private TextView tv;
    private Button setButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_input);

        Intent intent = getIntent();
        final String ymd = intent.getStringExtra("YMD");

        DBHelper goalDB = new DBHelper(this);
        final SQLiteDatabase db = goalDB.getWritableDatabase();
        editText = (EditText) findViewById(R.id.goalText);
        final String[] goal = {editText.getText().toString()};


        //データベース検索結果を表示
        String str = "select goal from 目的地 where date == '" + ymd + "'";
        Cursor c =db.rawQuery(str, null);

        tv = (TextView) findViewById(R.id.sql_result);
        String result = "";
        boolean f = c.moveToFirst();
        while (f) {
            result += String.format("%s", c.getString(0));
            f = c.moveToNext();
        }

        tv.setText(result);
        //Toast.makeText(getApplicationContext(),ymd,Toast.LENGTH_SHORT).show();

        //db.delete("目的地", "date = 2016823",null);

        setButton = (Button) findViewById(R.id.setButton);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //目的地を設定する
                db.delete("目的地", "date = '"+ymd+"'", null);
                goal[0] = editText.getText().toString();
                Log.v("目的地",":"+ goal[0]);
                ContentValues values = new ContentValues();
                values.put("date", ymd);
                values.put("goal", goal[0]);
                db.insert("目的地", null, values);
                Toast.makeText(getApplicationContext(),"目的地を設定しました",Toast.LENGTH_SHORT).show();

                //設定した後の目的地を表示
                String str = "select goal from 目的地 where date == '" + ymd + "'";
                Cursor c =db.rawQuery(str, null);

                tv = (TextView) findViewById(R.id.sql_result);
                String result = "";
                boolean f = c.moveToFirst();
                while (f) {
                    result += String.format("%s", c.getString(0));
                    f = c.moveToNext();
                }
                //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                tv.setText(result);
            }
        });
    }
}


