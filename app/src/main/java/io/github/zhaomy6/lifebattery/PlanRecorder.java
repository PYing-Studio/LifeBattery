package io.github.zhaomy6.lifebattery;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PlanRecorder extends Service {
    public final IBinder binder = new MyBinder();
    private MyDB myDB;

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
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        Date date = new Date();
        String dateFormat = "yyyy-MM-dd";
        String minuteFomat = "";
        if (DateFormat.is24HourFormat(getApplicationContext())) {
            minuteFomat += "k:mm";
        } else {
            minuteFomat += "HH:mm a";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.CHINA);
        String d_string = simpleDateFormat.format(date);

        simpleDateFormat = new SimpleDateFormat(minuteFomat, Locale.CHINA);
        String m_string = simpleDateFormat.format(date);
        myDB.updateTimeout(d_string + "\n" + m_string);
    }
}
