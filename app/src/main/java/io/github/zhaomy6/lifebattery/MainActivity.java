package io.github.zhaomy6.lifebattery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener {
    private MyDB myDB;
    private TextView title, DDL, progress;
    private Button planButton, storeButton, summaryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  更新剩余周以及百分比
        SharedPreferences sp = getSharedPreferences("LifeBatteryPre", MODE_PRIVATE);
        int totalWeeks = sp.getInt("totalWeeks", 9999);
        String[] birthday = sp.getString("birthday", "0/0/0").split("/");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

        //  生日日期获取
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(birthday[0]),
                Integer.parseInt(birthday[1]) - 1,
                Integer.parseInt(birthday[2]));
        Date birthDate = calendar.getTime();

        //  当前日期获取
        String[] curTime = df.format(new Date()).split("-");
        calendar.set(Integer.parseInt(curTime[0]),
                Integer.parseInt(curTime[1]) - 1,
                Integer.parseInt(curTime[2]));
        Date curDate = calendar.getTime();

        int usedWeeks = (int) ((curDate.getTime() - birthDate.getTime()) / 1000 / 60 / 60 / 24 / 7);
        int leftWeeks = totalWeeks - usedWeeks;
        int percent = 100 - (int) Math.floor((double) usedWeeks / (double) totalWeeks * 100);
        String showMes = leftWeeks + "\n" + percent + "%";
        TextView m_display = (TextView) findViewById(R.id.m_left_weeks);
        m_display.setText(showMes);

        findViewById(R.id.m_plan_button).setOnClickListener(this);
        findViewById(R.id.m_store_button).setOnClickListener(this);
        findViewById(R.id.m_summary_button).setOnClickListener(this);

        findViewById(R.id.main_battery_info).setOnClickListener(this);
//        storeBotton= (Button) findViewById(R.id.m_store_button);
//        summaryButton = (Button) findViewById(R.id.m_summary_button);
//        myDB = new MyDB(this);
//        title = (TextView)findViewById(R.id.m_plan_title);
//        DDL = (TextView)findViewById(R.id.m_plan_ddl);
//        progress = (TextView)findViewById(R.id.m_plan_progress);
//
//        Cursor cursor = myDB.getAll();
//        String titleContent = "", DDLContent= "", progressContent = "";
//        while (cursor.moveToNext()) {
//            String tmp = cursor.getString(cursor.getColumnIndex("DDL"));
//            if (tmp.compareTo(DDLContent) > 0) {
//                titleContent = cursor.getString(cursor.getColumnIndex("title"));
//                DDLContent = tmp;
//                progressContent = cursor.getString(cursor.getColumnIndex("progress"));
//            }
//        }
//        title.setText(titleContent);
//        DDL.setText(DDLContent);
//        progress.setText(progressContent);
    }

    // 界面底部导航逻辑跳转
    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.m_store_button:
                intent = new Intent(MainActivity.this, StoreActivity.class);
                startActivity(intent);
                break;
            case R.id.m_summary_button:
                intent = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(intent);
                break;
            case R.id.m_plan_button:
                intent = new Intent(MainActivity.this, PlansActivity.class);
                startActivity(intent);
                break;
            case R.id.main_battery_info:
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                String str = df.format(new Date());
                Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "点击逻辑未处理", Toast.LENGTH_SHORT).show();
        }
    }
}
