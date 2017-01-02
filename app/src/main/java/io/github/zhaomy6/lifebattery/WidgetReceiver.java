package io.github.zhaomy6.lifebattery;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class WidgetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        if (intent.getAction().equals("widgetReceiver")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String DDL = bundle.getString("DDL");
                String ddlStr = DDL.split("\n")[0];
                int days = timeDistance(new Date(), ddlStr);
                if (days > 5) {
                    rv.setImageViewResource(R.id.appwidget_battery, R.drawable.state1);
                } else if (days > 3) {
                    rv.setImageViewResource(R.id.appwidget_battery, R.drawable.state2);
                } else if (days > 1) {
                    rv.setImageViewResource(R.id.appwidget_battery, R.drawable.state3);
                } else {
                    rv.setImageViewResource(R.id.appwidget_battery, R.drawable.state4);
                }
                String title = bundle.getString("title");
                rv.setTextViewText(R.id.appwidget_DDL, DDL);
                rv.setTextViewText(R.id.appwidget_Title, title);
                AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, AppWidget.class), rv);
            }
        }
    }

    private int timeDistance(Date fromWhen, String to) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        int days = 7;
//        String dstr="2008-4-24";  demo
        try {
            Date toWhen = sdf.parse(to);
            long diff = toWhen.getTime() - fromWhen.getTime();
            days = (int) diff / 1000 / 60 / 60 / 24;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return days;
    }
}
