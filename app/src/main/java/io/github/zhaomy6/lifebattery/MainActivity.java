package io.github.zhaomy6.lifebattery;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 主界面:
 */
public class MainActivity extends AppCompatActivity
    implements View.OnClickListener {
    private MyDB myDB;
    private TextView latestTitle, latestDDL, latestProgress;
    private Button planButton, storeButton, summaryButton;
    private ListView lv;

    private PlanRecorder planRecorder;

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            planRecorder = ((PlanRecorder.MyBinder)service).getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            planRecorder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latestTitle = (TextView) findViewById(R.id.m_plan_title);
        latestDDL = (TextView) findViewById(R.id.m_plan_DDL);
        latestProgress = (TextView) findViewById(R.id.m_plan_progress);

        myDB = new MyDB(this);
        Cursor cursor = myDB.getLatestPlan();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String title = cursor.getString(cursor.getColumnIndex("title"));
            latestTitle.setText(title);
            String DDL = cursor.getString(cursor.getColumnIndex("DDL"));
            latestDDL.setText(DDL);
            if (title != null || DDL != null) {
                String minuteFormat = "";
                if (DateFormat.is24HourFormat(getApplicationContext())) {
                    minuteFormat += "k:mm";
                } else {
                    minuteFormat += "HH:mm a";
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd " + minuteFormat, Locale.CHINA);
                String[] frag = DDL.split("\n");
                String dstr = frag[0] + " " + frag[1];
                Date date = null;
                try {
                    date = sdf.parse(dstr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long s1 = date.getTime();
                long s2 = System.currentTimeMillis();
                long day = (s1 - s2) / 1000 / 60 / 60 / 24 + 1;
                latestProgress.setText("剩余 " + day + " 天");
            }
        }

        // 绑定服务
        bindServiceConnection();
        planRecordHandleStart();

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
    }

    // 初始化
    private void init() {
        latestTitle = (TextView) findViewById(R.id.m_plan_title);
        latestDDL = (TextView) findViewById(R.id.m_plan_DDL);
        latestProgress = (TextView) findViewById(R.id.m_plan_progress);
        myDB = new MyDB(this);

        findViewById(R.id.m_plan_button).setOnClickListener(this);
        findViewById(R.id.m_store_button).setOnClickListener(this);
        findViewById(R.id.m_summary_button).setOnClickListener(this);
        findViewById(R.id.main_battery_info).setOnClickListener(this);
    }

    // 获取最近任务
    private void fillWithLatestPlan() {
        Cursor cursor = myDB.getLatestPlan();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String title = cursor.getString(cursor.getColumnIndex("title"));
            latestTitle.setText(title);
            String DDL = cursor.getString(cursor.getColumnIndex("DDL"));
            latestDDL.setText(DDL);
            if (title != null || DDL != null) {
                String minuteFormat = "";
                if (DateFormat.is24HourFormat(getApplicationContext())) {
                    minuteFormat += "k:mm";
                } else {
                    minuteFormat += "HH:mm a";
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd " + minuteFormat, Locale.CHINA);
                String[] frag = DDL.split("\n");
                String dstr = frag[0] + " " + frag[1];
                Date date = null;
                try {
                    date = sdf.parse(dstr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long s1 = date.getTime();
                long s2 = System.currentTimeMillis();
                long day = (s1 - s2) / 1000 / 60 / 60 / 24 + 1;
                latestProgress.setText("剩余 " + day + " 天");
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(sc);
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
                popLongPlanDialog();
                break;
            default:
                Toast.makeText(this, "点击逻辑未处理", Toast.LENGTH_SHORT).show();
        }
    }

    private void popLongPlanDialog() {
        //  列举所有无DDL的任务
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View v = factory.inflate(R.layout.dialog_long_plan, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(v);
        //  对话框属性

//        ArrayList<Plan> plans = new ArrayList<>();
//        Cursor cursor =  myDB.getLongTimePlan();
//        if (cursor != null && cursor.getCount() > 0) {
//            while (cursor.moveToNext()) {
//                String title = cursor.getString(cursor.getColumnIndex("title"));
//                if ("".equals(title)) continue;
//                String DDL = cursor.getString(cursor.getColumnIndex("DDL"));
//                String type = cursor.getString(cursor.getColumnIndex("type"));
//                if ("".equals(type)) continue;
//                String detail = cursor.getString(cursor.getColumnIndex("detail"));
//                String finished = cursor.getString(cursor.getColumnIndex("finished"));
//                plans.add(new Plan(title, DDL, type, detail, finished));
//            }
//        }

        final PlanAdapter adapter = getPlansAdapter(v);
        lv = (ListView) v.findViewById(R.id.long_plan_list);
        lv.setAdapter(adapter);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "test", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //  绑定对话框中事件
        String dialogTitle = "长远任务（共" + lv.getCount() + "项）";
        builder.setTitle(dialogTitle);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //  do nothing
            }
        });
        builder.setPositiveButton("完成任务", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //  将已选中的任务从数据库中删除
                //  selectedPlans中存有所有选中的Plan
                ArrayList<Plan> selectedPlans = adapter.getSelectedPlans();
                for (int k = 0; k < selectedPlans.size(); k++) {
                    Plan tem = selectedPlans.get(k);
                    myDB.updateFinished(tem.getTitle(), "完成");
                }
                lv.setAdapter(getPlansAdapter(v));
            }
        });

        //  自定义对话框大小，显示对话框
        Dialog dialog = builder.create();
//        Window window = dialog.getWindow();
        dialog.show();
//        if (window != null) {
//            WindowManager.LayoutParams p = window.getAttributes();
//            p.height = 1000;
//            window.setAttributes(p);
//        }
    }

    private void updateDBImmediately() {
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
        myDB.updateTimeout(d_string + "\n" + m_string);
    }

    private PlanAdapter getPlansAdapter(View v) {
        ArrayList<Plan> plans = new ArrayList<>();
        Cursor cursor =  myDB.getLongTimePlan();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                if ("".equals(title)) continue;
                String DDL = cursor.getString(cursor.getColumnIndex("DDL"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                if ("".equals(type)) continue;
                String detail = cursor.getString(cursor.getColumnIndex("detail"));
                String finished = cursor.getString(cursor.getColumnIndex("finished"));
                plans.add(new Plan(title, DDL, type, detail, finished));
            }
        }
        return new PlanAdapter(v.getContext(), plans, true);
    }

    private void bindServiceConnection() {
        Intent intent = new Intent(this, PlanRecorder.class);
        bindService(intent, sc, this.BIND_AUTO_CREATE);
    }

    private void planRecordHandleStart() {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                planRecorder.startSendNotification();
            }
        }, 30 * 1000);
    }
}
