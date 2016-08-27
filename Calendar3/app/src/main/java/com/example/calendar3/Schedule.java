package com.example.calendar3;

import android.content.ContentValues;
import android.content.Intent;
//import android.icu.util.Calendar;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Schedule extends AppCompatActivity implements View.OnClickListener {
    private Calendar calendar = Calendar.getInstance();
    private int month = calendar.get(Calendar.MONTH);
    private int year  = calendar.get(Calendar.YEAR);

    private ArrayList<Integer> day = new ArrayList<Integer>();
    private ArrayList<TextView> textViews = new ArrayList<TextView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);


        day.add(R.id.sunday1);
        day.add(R.id.monday1);
        day.add(R.id.tuesday1);
        day.add(R.id.wednesday1);
        day.add(R.id.thursday1);
        day.add(R.id.friday1);
        day.add(R.id.saturday1);

        day.add(R.id.sunday2);
        day.add(R.id.monday2);
        day.add(R.id.tuesday2);
        day.add(R.id.wednesday2);
        day.add(R.id.thursday2);
        day.add(R.id.friday2);
        day.add(R.id.saturday2);

        day.add(R.id.sunday3);
        day.add(R.id.monday3);
        day.add(R.id.tuesday3);
        day.add(R.id.wednesday3);
        day.add(R.id.thursday3);
        day.add(R.id.friday3);
        day.add(R.id.saturday3);

        day.add(R.id.sunday4);
        day.add(R.id.monday4);
        day.add(R.id.tuesday4);
        day.add(R.id.wednesday4);
        day.add(R.id.thursday4);
        day.add(R.id.friday4);
        day.add(R.id.saturday4);

        day.add(R.id.sunday5);
        day.add(R.id.monday5);
        day.add(R.id.tuesday5);
        day.add(R.id.wednesday5);
        day.add(R.id.thursday5);
        day.add(R.id.friday5);
        day.add(R.id.saturday5);

        day.add(R.id.sunday6);
        day.add(R.id.monday6);
        day.add(R.id.tuesday6);
        day.add(R.id.wednesday6);
        day.add(R.id.thursday6);
        day.add(R.id.friday6);
        day.add(R.id.saturday6);

        CreateCalendar();

        Button button1, button2;

        button1 = (Button) findViewById(R.id.nextButton);
        button1.setOnClickListener(this);

        button2 = (Button) findViewById(R.id.beforeButton);
        button2.setOnClickListener(this);
    }

    //カレンダーを作成
    public void CreateCalendar(){
        int youbiI;
        TextView tv;
        int start, date=1, count=0;

        Calendar first = Calendar.getInstance();
        first.set(year, month, 1);


        youbiI = first.get(Calendar.DAY_OF_WEEK);
        start = youbiI;




        tv = (TextView)findViewById(R.id.YearMonth);
        tv.setText(String.valueOf((int)year)+"年"+String.valueOf((int)month+1)+"月");

        //カレンダー初期化

        for(int i=0; i<day.size(); i++){
            tv = (TextView)findViewById(day.get(i));
            tv.setText("");
            tv.setClickable(false);
        }
        textViews.clear();

        int lastday = first.getActualMaximum(Calendar.DATE)+start-1;
        //カレンダー描画
        for(int i=start-1; i<=lastday-1; i++){
            tv = (TextView)findViewById(day.get(i));
            tv.setText(String.valueOf(date));
            textViews.add(tv);
            textViews.get(date-1).setOnClickListener(this);
            tv.setOnClickListener(this);
            date++;
        }


    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.beforeButton) {
            TextView tv;

            month = month-1;
            if(month == -1){
                month = 11;
                year--;
            }
            CreateCalendar();
        }else if(v.getId() == R.id.nextButton){
            TextView tv;

            month = (month+1) % 12;
            if(month == 0){
                year++;
            }
            CreateCalendar();
        }else{
            String ymd = String.valueOf(year) + String.valueOf(month) + v.getId();
            Log.v("日付",""+ymd);

            Intent intent = new Intent(this, ScheduleInput.class);
            intent.putExtra("YMD", ymd);
            startActivity(intent);
        }



    }
}
