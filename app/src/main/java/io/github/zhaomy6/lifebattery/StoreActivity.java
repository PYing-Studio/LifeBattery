package io.github.zhaomy6.lifebattery;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StoreActivity extends AppCompatActivity {
    private TableLayout pastWeekTable;
    private TextView pastWeeksCount;
    private MyDB myDB = new MyDB(StoreActivity.this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        pastWeekTable = (TableLayout)findViewById(R.id.pastWeeksTable);
        pastWeeksCount = (TextView)findViewById(R.id.pastWeeksCount);

        // 获取过去周数量
        SharedPreferences sp = getSharedPreferences("LifeBatteryPre", MODE_PRIVATE);
        String[] birthday = sp.getString("birthday", "0/0/0").split("/");
        int totalWeeks = sp.getInt("totalWeeks", 9999);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(birthday[0]),
                Integer.parseInt(birthday[1]) - 1,
                Integer.parseInt(birthday[2]));
        Date birthDate = calendar.getTime();
        String[] curTime = df.format(new Date()).split("-");
        calendar.set(Integer.parseInt(curTime[0]),
                Integer.parseInt(curTime[1]) - 1,
                Integer.parseInt(curTime[2]));
        Date curDate = calendar.getTime();
        int usedWeeks = (int) ((curDate.getTime() - birthDate.getTime()) / 1000 / 60 / 60 / 24 / 7);
        int count = 100 - (int) Math.floor((double) usedWeeks / (double) totalWeeks * 100);
        pastWeeksCount.setText("已经过去 " + usedWeeks + " 周");
        while (count > 0) {
            if (count >= 3) {
                addTableRow(pastWeekTable);
                count -= 3;
            } else if (count == 1) {
                addColumn1(pastWeekTable);
                count = 0;
            } else if (count == 2) {
                addColumn2(pastWeekTable);
                count = 0;
            }
        }

        Calendar c = Calendar.getInstance();
        int left_of_week = 7 - c.get(Calendar.DAY_OF_WEEK);
        TextView left_day = (TextView)findViewById(R.id.left_day);
        Date date = new Date();
        String dateFormat = "yyyy-MM-dd";
        String minuteFormat = "";
        if (DateFormat.is24HourFormat(getApplicationContext())) {
            minuteFormat += "k:mm";
        } else {
            minuteFormat += "HH:mm a";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.CHINA);
        String d_string = simpleDateFormat.format(date);
        simpleDateFormat = new SimpleDateFormat(minuteFormat, Locale.CHINA);
        String m_string = simpleDateFormat.format(date);
        String timeCurrent = d_string + "\n" + m_string;

        // 获取周末日期
        c.set(Calendar.DAY_OF_WEEK, 1);
        c.add(Calendar.DAY_OF_WEEK, 6);
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(dateFormat, Locale.CHINA);
        String w_string = simpleDateFormat2.format(c.getTime());
        if (DateFormat.is24HourFormat(getApplicationContext())) {
            minuteFormat = "23:59";
        } else {
            minuteFormat = "11:59 下午";
        }
        String endOfWeek = w_string + "\n" + minuteFormat;

        // 对计划进行范围查询
        Cursor cursor = myDB.queryTimeInterval(timeCurrent, endOfWeek);
        String planText = "\n";
        int planTodo = cursor.getCount();
        if (planTodo <= 0) {
            planText += "本周未安排计划";
        } else {
            planText += planTodo + " 个任务待完成: ";
            while (cursor.moveToNext()) {
                planText += cursor.getString(cursor.getColumnIndex("title")) + " ";
            }
        }

        left_day.setText("本周还剩 " + left_of_week + " 天" + planText);
        ImageView left_battery = (ImageView)findViewById(R.id.left_battery);
        if (left_of_week > 5) {
            left_battery.setImageResource(R.drawable.state1);
        } else if (left_of_week > 3) {
            left_battery.setImageResource(R.drawable.state2);
        } else if (left_of_week > 1) {
            left_battery.setImageResource(R.drawable.state3);
        } else {
            left_battery.setImageResource(R.drawable.state4);
        }
    }

    // 动态添加行
    private void addTableRow(TableLayout pastWeekTable) {
        View viewXml = getLayoutInflater().inflate(R.layout.table_row, pastWeekTable, false);
        TableRow tableRow = (TableRow) viewXml.findViewById(R.id.table_row);
        View viewXml1 = getLayoutInflater().inflate(R.layout.cloum1, tableRow, false);
        ImageView column1 = (ImageView)viewXml1.findViewById(R.id.store_battery0);
        tableRow.addView(column1);
        View viewXml2 = getLayoutInflater().inflate(R.layout.colum2, tableRow, false);
        ImageView column2 = (ImageView)viewXml2.findViewById(R.id.store_battery1);
        tableRow.addView(column2);
        View viewXml3 = getLayoutInflater().inflate(R.layout.colum3, tableRow, false);
        ImageView column3 = (ImageView)viewXml3.findViewById(R.id.store_battery2);
        tableRow.addView(column3);
        pastWeekTable.addView(tableRow);
    }

    private void addColumn1(TableLayout pastWeekTable) {
        View viewXml = getLayoutInflater().inflate(R.layout.table_row, pastWeekTable, false);
        TableRow tableRow = (TableRow) viewXml.findViewById(R.id.table_row);
        View viewXml1 = getLayoutInflater().inflate(R.layout.cloum1, tableRow, false);
        ImageView column1 = (ImageView)viewXml1.findViewById(R.id.store_battery0);
        tableRow.addView(column1);
        View viewXml2 = getLayoutInflater().inflate(R.layout.column22, tableRow, false);
        ImageView column2 = (ImageView)viewXml2.findViewById(R.id.store_battery11);
        tableRow.addView(column2);
        View viewXml3 = getLayoutInflater().inflate(R.layout.column33, tableRow, false);
        ImageView column3 = (ImageView)viewXml3.findViewById(R.id.store_battery22);
        tableRow.addView(column3);
        pastWeekTable.addView(tableRow);
    }

    private void addColumn2(TableLayout pastWeekTable) {
        View viewXml = getLayoutInflater().inflate(R.layout.table_row, pastWeekTable, false);
        TableRow tableRow = (TableRow) viewXml.findViewById(R.id.table_row);
        View viewXml1 = getLayoutInflater().inflate(R.layout.cloum1, tableRow, false);
        ImageView column1 = (ImageView)viewXml1.findViewById(R.id.store_battery0);
        tableRow.addView(column1);
        View viewXml2 = getLayoutInflater().inflate(R.layout.colum2, tableRow, false);
        ImageView column2 = (ImageView)viewXml2.findViewById(R.id.store_battery1);
        tableRow.addView(column2);
        View viewXml3 = getLayoutInflater().inflate(R.layout.column33, tableRow, false);
        ImageView column3 = (ImageView)viewXml3.findViewById(R.id.store_battery22);
        tableRow.addView(column3);
        pastWeekTable.addView(tableRow);
    }
}
