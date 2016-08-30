package com.example.kasasasu;

/**
 * Created by shunpei on 16/08/29.
 */
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
//////////////////////////////////
//      by Katsumune
//////////////////////////////////
public class Schedule extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // private OnFragmentInteractionListener mListener;
    private View v;

    private Calendar calendar = Calendar.getInstance();
    private int month = calendar.get(Calendar.MONTH);
    private int year  = calendar.get(Calendar.YEAR);

    private ArrayList<Integer> day = new ArrayList<Integer>();
    private ArrayList<TextView> textViews = new ArrayList<TextView>();


    private int sentakuFlag = 0;
    private String ymd="";
    private ArrayList<Setting> settingArrayList;
    private boolean swIsChecked;
    private SettingAdapter adapter;
    private String address;


    public Schedule() {
        // Required empty public constructor
    }


    public static Schedule newInstance(String param1, String param2) {
        Schedule fragment = new Schedule();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Button button1, button2, button3, button4;

        v = inflater.inflate(R.layout.fragment_schedule, null);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        button1 = (Button) v.findViewById(R.id.nextButton);
        button1.setOnClickListener(this);

        button2 = (Button) v.findViewById(R.id.beforeButton);
        button2.setOnClickListener(this);

        button3 = (Button) v.findViewById(R.id.Button);
        button3.setOnClickListener(this);

        button4 = (Button) v.findViewById(R.id.delButton);
        button4.setOnClickListener(this);


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
        return v;
    }

    public void getToday(){
        String today = "";
        KasasasuSQLiteOpenHelper goalDB = new KasasasuSQLiteOpenHelper(getActivity());
        final SQLiteDatabase db = goalDB.getWritableDatabase();
        Drawable icon = getResources().getDrawable(R.drawable.info);



        for (int i : day) {
            TextView tv = (TextView) v.findViewById(i);
            Calendar cal = Calendar.getInstance();
            today = String.valueOf(year) + String.valueOf(month) + String.valueOf(tv.getText());
            if (today.equals(String.valueOf(cal.get(Calendar.YEAR)) + String.valueOf(cal.get(Calendar.MONTH)) + String.valueOf(cal.get(Calendar.DATE)))) {

                tv.setTextColor(Color.GREEN);
            }

        }
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




        tv = (TextView)v.findViewById(R.id.YearMonth);
        tv.setText(String.valueOf((int)year)+"年"+String.valueOf((int)month+1)+"月");

        //カレンダー初期化

        for(int i=0; i<day.size(); i++){
            tv = (TextView)v.findViewById(day.get(i));
            tv.setText("");
            tv.setClickable(false);
        }
        textViews.clear();

        int lastday = first.getActualMaximum(Calendar.DATE)+start-1;
        //カレンダー描画
        for(int i=start-1; i<=lastday-1; i++){
            tv = (TextView)v.findViewById(day.get(i));
            tv.setText(String.valueOf(date));
            textViews.add(tv);
            textViews.get(date-1).setOnClickListener(this);
            tv.setOnClickListener(this);
            tv.setTextColor(Color.BLACK);
            if(i % 7 == 0){
                tv.setTextColor(Color.RED);
            }else if(i % 7 == 6){
                tv.setTextColor(Color.BLUE);
            }
            date++;
        }
        getToday();
    }


    @Override
    public void onClick(View v){
        KasasasuSQLiteOpenHelper goalDB = new KasasasuSQLiteOpenHelper(getActivity());
        final SQLiteDatabase db = goalDB.getWritableDatabase();

        if(v.getId() == R.id.beforeButton) {


            month = month-1;
            if(month == -1){
                month = 11;
                year--;
            }
            CreateCalendar();
        }else if(v.getId() == R.id.nextButton){


            month = (month+1) % 12;
            if(month == 0){
                year++;
            }
            CreateCalendar();
        }else if(v.getId() == R.id.Button){
            if(sentakuFlag == 0){
                Toast.makeText(getActivity(),"設定する日付を選択してください",Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent(getActivity(), AreaActivity2.class);
                intent.putExtra("YMD", ymd);
                startActivity(intent);
                //CreateGoal(ymd);

                sentakuFlag = 0;

            }
        }else if(v.getId() == R.id.delButton){
            if(sentakuFlag == 0){
                Toast.makeText(getActivity(),"削除する日付を選択してください",Toast.LENGTH_SHORT).show();
            }else{
                db.delete("mokutekichi", "date = '"+ymd+"'", null);
                Toast.makeText(getActivity(),"目的地を削除しました",Toast.LENGTH_SHORT).show();
                sentakuFlag = 0;
            }
        }
        else{
            TextView tv =(TextView)v.findViewById(v.getId());

            ymd = String.valueOf(year) + String.valueOf(month+1) + tv.getText();
            sentakuFlag = 1;

            Cursor c = db.rawQuery("select goalpref from mokutekichi where date == '" + ymd + "'", null);
            String resultPref = "";
            boolean f = c.moveToFirst();
            while (f) {
                resultPref += String.format("%s", c.getString(0));
                f = c.moveToNext();
            }


            String GOAL = String.valueOf(year) +"年"+ String.valueOf(month+1) +"月"+ tv.getText()+"日";

            c = db.rawQuery("select goalcity from mokutekichi where date == '" + ymd + "'", null);
            String resultCity = "";
            f = c.moveToFirst();
            while (f) {
                resultCity += String.format("%s", c.getString(0));
                f = c.moveToNext();
            }
            Toast.makeText(getActivity(),GOAL+resultPref+resultCity,Toast.LENGTH_SHORT).show();
        }
    }
}
