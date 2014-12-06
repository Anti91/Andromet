package hu.uniobuda.nik.andromet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;


/**
 * Implementation of App Widget functionality.
 */
public class AndroMetWidget extends AppWidgetProvider {

    private static PendingIntent service = null;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        /*ComponentName thisWidget = new ComponentName(context, AndroMetWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            //updateAppWidget(context, appWidgetManager, appWidgetIds[i]);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.andro_met_widget);
            remoteViews.setTextViewText(R.id.LocTime, new Date(System.currentTimeMillis()).toLocaleString());

            Intent intent = new Intent(context, AndroMetWidget.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.LocTime, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }*/

        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        final Calendar TIME = Calendar.getInstance();
        TIME.set(Calendar.MINUTE, 0);
        TIME.set(Calendar.SECOND, 0);
        TIME.set(Calendar.MILLISECOND, 0);

        final Intent i = new Intent(context, UpdateService.class);
        PendingIntent service = null;

        if (service == null) {
            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        SharedPreferences preferences = context.getSharedPreferences("hu.uniobuda.nik.andromet_preferences", Context.MODE_PRIVATE);
        int freq = Integer.parseInt(preferences.getString("sync_frequency", null));

        if (freq > 0)
            m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 60000 * freq, service);
        else if (service != null)
            m.cancel(service);

        ComponentName thisWidget = new ComponentName(context, AndroMetWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.andro_met_widget);
            Intent configIntent = new Intent(context, settings.class);

            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);

            remoteViews.setOnClickPendingIntent(R.id.WeatherIcon, configPendingIntent);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (service != null) {
            m.cancel(service);
        }
    }


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.andro_met_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


}


