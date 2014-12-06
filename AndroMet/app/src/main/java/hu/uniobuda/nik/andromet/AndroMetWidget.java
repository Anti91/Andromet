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
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;


/**
 * Implementation of App Widget functionality.
 */
public class AndroMetWidget extends AppWidgetProvider {

    private PendingIntent service = null;

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


        // ha rákattintunk, akkor a beállítások activity előjön
        ComponentName thisWidget = new ComponentName(context, AndroMetWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.andro_met_widget);
            Intent configIntent = new Intent(context, settings.class);
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget, configPendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }

    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);



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


