package io.github.zhaomy6.lifebattery;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class PlanRecorder extends Service {
    public final IBinder binder = new MyBinder();
    private MyDB myDB;
    private Timer timer;
    private TimerTask timerTask;
    private Timer notification;
    private TimerTask notificationTask;

    @Override
    public void onCreate() {
        super.onCreate();
        myDB = new MyDB(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class MyBinder extends Binder {
        PlanRecorder getService() {
            return PlanRecorder.this;
        }
    }

    public void updatePlanDB() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                start();
            }
        };
        timer.schedule(timerTask, 0, 60 * 1000);

        notification = new Timer();
        notificationTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    sendNotification();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        notification.schedule(notificationTask, 0, 8 * 60 * 60 * 1000);
    }

    private void start() {
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

    private void sendNotification() throws ParseException {
        Cursor cursor = myDB.getLatestPlan();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String DDL = cursor.getString(cursor.getColumnIndex("DDL"));

            String minuteFormat = "";
            if (DateFormat.is24HourFormat(getApplicationContext())) {
                minuteFormat += "k:mm";
            } else {
                minuteFormat += "HH:mm a";
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd " + minuteFormat);
            String[] frag = DDL.split("\n");
            String dstr = frag[0] + " " + frag[1];
            Date date = sdf.parse(dstr);
            long s1 = date.getTime();
            long s2 = System.currentTimeMillis();
            long day = (s1 - s2) / 1000 / 60 / 60 / 24;
            long hour = (s1 - s2) / 1000 / 60 / 60;

            Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.icon);
            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            builder.setContentTitle("任务日程提醒")
                    .setContentText(title + " 还有" + day + "天 " + hour + "小时")
                    .setTicker("最近任务安排")
                    .setLargeIcon(bitmap)
                    .setSmallIcon(R.mipmap.icon)
                    .setAutoCancel(false);
            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            Intent myIntent = new Intent(getApplicationContext(), PlansActivity.class);
            PendingIntent myPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, myIntent, 0);
            builder.setContentIntent(myPendingIntent);

            Notification notification = builder.build();
            manager.notify(0, notification);
        }
    }
}
