package hu.uniobuda.nik.andromet;

import android.app.AlarmManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class UpdateService extends Service {
    public UpdateService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        buildUpdate();

        return super.onStartCommand(intent, flags, startId);
    }

    private void buildUpdate()
    {
        /*String lastUpdated = DateFormat.format("hh:mm:ss", new Date()).toString();

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.andro_met_widget);
        view.setTextViewText(R.id.LocTime, lastUpdated);



        ComponentName thisWidget = new ComponentName(this, AndroMetWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(thisWidget, view);*/

        new Retrievedata().execute(this);

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    class Retrievedata extends AsyncTask<Context, Void, Integer> {
        @Override
        protected Integer doInBackground(Context... params) {
            // Create a new HttpClient and Post Header
            HttpResponse response;
            String result;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.amsz.hu/android.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("resp", "widget"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                response = httpclient.execute(httppost);
                if (response.getStatusLine().getStatusCode() == 200) {
                    result = EntityUtils.toString(response.getEntity(), "iso-8859-2");

                    RemoteViews view = new RemoteViews(getPackageName(), R.layout.andro_met_widget);
                    view.setTextViewText(R.id.LocTime, result);



                    ComponentName thisWidget = new ComponentName(params[0], AndroMetWidget.class);
                    AppWidgetManager manager = AppWidgetManager.getInstance(params[0]);
                    manager.updateAppWidget(thisWidget, view);
                }


            } catch (ClientProtocolException e) {
                Toast.makeText(getApplicationContext(), "Hiba a kapcsolódás során!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Váratlan hiba",Toast.LENGTH_LONG).show();
            }

            return 1;
        }
        @Override
        protected void onPostExecute(Integer h) {


        }
    }



}
