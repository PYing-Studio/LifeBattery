package io.github.zhaomy6.lifebattery;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        //  TODO: 从数据库读取打卡日期
        ArrayList<String> dates = new ArrayList<>();
        //  存储日期格式yyyy-mm-dd
        dates.add("2016-12-21");
        dates.add("2016-12-22");
        dates.add("2016-12-23");

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
