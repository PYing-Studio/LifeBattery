package io.github.zhaomy6.lifebattery;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 *显示本周剩余电池量, 关注的计划(日期、标题)
 */
public class AppWidget extends AppWidgetProvider {
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setTextViewText(R.id.appwidget_Title, widgetText);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Intent clickIntent = new Intent(context, PlansActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, clickIntent, 0);

        Intent clickIntent2 = new Intent(context, StoreActivity.class);
        PendingIntent pi2 = PendingIntent.getActivity(context, 0, clickIntent2, 0);

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        rv.setOnClickPendingIntent(R.id.appwidget_Title, pi);
        rv.setOnClickPendingIntent(R.id.appwidget_battery, pi2);

        appWidgetManager.updateAppWidget(appWidgetIds, rv);
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}

