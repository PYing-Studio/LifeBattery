package io.github.zhaomy6.lifebattery;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

//  打卡时间表记录的是完成任务的DDL，而不是完成任务的系统当前时间
public class CalendarActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        //  从持久化存储中读出打卡记录
        //  存储日期格式yyyy-mm-dd
        ArrayList<String> dates = new ArrayList<>();
        SharedPreferences sp = getSharedPreferences("LifeBatteryPre", MODE_PRIVATE);
        String finishList = sp.getString("finishList", "");
        if (!"".equals(finishList)) {
            String[] list = finishList.split(";");
            Log.d("Cal", finishList);
            Collections.addAll(dates, list);
        }

        final MaterialCalendarView mcv = (MaterialCalendarView) findViewById(R.id.calendar);
        mcv.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
        for (String date : dates) {
            String[] ymd = date.split("-");
            CalendarDay cd = CalendarDay.from(Integer.parseInt(ymd[0]),
                    Integer.parseInt(ymd[1]) - 1,
                    Integer.parseInt(ymd[2]));  //  2016-12-23
            mcv.setDateSelected(cd, true);
        }
    }
}
