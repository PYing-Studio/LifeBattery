package io.github.zhaomy6.lifebattery;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

/**
 * Created by zhangsht on 2016/12/22.
 */

public class WidgetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        if (intent.getAction().equals("widgetReceiver")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String DDL = bundle.getString("DDL");
                String title = bundle.getString("title");
                rv.setTextViewText(R.id.appwidget_DDL, DDL);
                rv.setTextViewText(R.id.appwidget_Title, title);
                AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, AppWidget.class), rv);
            }
        }
    }
}
