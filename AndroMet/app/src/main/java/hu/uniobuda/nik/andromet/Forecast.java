package hu.uniobuda.nik.andromet;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RemoteViews;
import android.widget.TextView;
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
import java.util.List;


public class Forecast extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        new Retrievedata().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forecast, menu);
        return true;
    }



    class Retrievedata extends AsyncTask<Void, Void, Integer> {

        HttpResponse response;
        String result = "";

        @Override
        protected Integer doInBackground(Void... params) {
            // Create a new HttpClient and Post Header

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.amsz.hu/android.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("resp", "forecast"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                response = httpclient.execute(httppost);
                if (response.getStatusLine().getStatusCode() == 200) {
                    result = EntityUtils.toString(response.getEntity(), "iso-8859-2");
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
            try {
                if (result != "") {
                    String[] sp = result.split("#");
                    for (int i = 0; i < 5; i++) {
                        String[] sp2 = sp[i].split(";");

                        String idstr = "fc_minmax" + (i + 1);
                        int resID = getResources().getIdentifier(idstr, "id", "hu.uniobuda.nik.andromet");
                        TextView tw = (TextView) findViewById(resID);
                        tw.setText("Min: " + sp2[3] + "°C Max: " + sp2[4] + "°C");

                        idstr = "fc_sign" + (i + 1);
                        resID = getResources().getIdentifier(idstr, "id", "hu.uniobuda.nik.andromet");
                        tw = (TextView) findViewById(resID);
                        tw.setText(DecodeSign(sp2[0]));

                        idstr = "fc_wind" + (i + 1);
                        resID = getResources().getIdentifier(idstr, "id", "hu.uniobuda.nik.andromet");
                        tw = (TextView) findViewById(resID);
                        tw.setText(DecodeWind(sp2[1], sp2[2]));

                    }

                    TextView textView = (TextView) findViewById(R.id.fc_date);
                    textView.setText("Előrejelzés: " + sp[5]);
                    textView = (TextView) findViewById(R.id.fc_progno);
                    textView.setText(sp[6]);
                }
            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(), "Hiba a feldolgozás során!",Toast.LENGTH_LONG).show();
            }

        }
    }

    String DecodeSign(String v)
    {
        v = v.trim();

        if (v.equals("01")) return "Derült";
        if (v.equals("02")) return "Gyengén felhős";
        if (v.equals("03")) return "Közepesen felhős";
        if (v.equals("04")) return "Erősen felhős";
        if (v.equals("05")) return "Borult";
        if (v.equals("06")) return "Gyenge eső";
        if (v.equals("07")) return "Eső";
        if (v.equals("08")) return "Ónos eső";
        if (v.equals("09")) return "Zápor";
        if (v.equals("11")) return "Száraz zivatar";
        if (v.equals("12")) return "Zivatar";
        if (v.equals("13")) return "Jégeső";
        if (v.equals("14")) return "Köd";
        if (v.equals("15")) return "Havas eső";
        if (v.equals("16")) return "Hódara";
        if (v.equals("17")) return "Hószállingózás";
        if (v.equals("18")) return "Havazás";
        if (v.equals("19")) return "Hózápor";
        if (v.equals("20")) return "Intenzív havazás";
        if (v.equals("21")) return "Hózivatar";

        return "";
    }

    String DecodeWind(String ws, String wd)
    {
        String wsn = "", wdn = "";
        ws = ws.trim(); wd = wd.trim();

        if (ws == "" || ws.equals("00")) return "nincs adat";
        if (ws.equals("01")) return "szélcsend";

        if (ws.equals("02")) wsn = "gyenge";
        else if (ws.equals("03")) wsn = "mérsékelt";
        else if (ws.equals("04")) wsn = "élénk";
        else if (ws.equals("05")) wsn = "erős";
        else if (ws.equals("06")) wsn = "igen erős";
        else if (ws.equals("07")) wsn = "viharos";
        else if (ws.equals("08")) wsn = "erősen viharos";
        else if (ws.equals("09")) wsn = "erős vihar";
        else if (ws.equals("10")) wsn = "heves vihar";
        else if (ws.equals("11")) wsn = "orkán";

        if (wd.equals("01")) wdn = "É";
        else if (wd.equals("02")) wdn = "ÉK";
        else if (wd.equals("03")) wdn = "K";
        else if (wd.equals("04")) wdn = "DK";
        else if (wd.equals("05")) wdn = "D";
        else if (wd.equals("06")) wdn = "DNy";
        else if (wd.equals("07")) wdn = "Ny";
        else if (wd.equals("08")) wdn = "ÉNy";
        else if (wd.equals("09")) wdn = "VR";

        return wsn + " " + wdn;
    }
}
