package hu.uniobuda.nik.andromet;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public class CurrentData extends Activity {

    Spinner spinner;
    Button button;
    HttpResponse response;
    String result;
    ArrayList<station> stations;
    ArrayAdapter<station> adapter;
    int selectedItem = 0;
    TextView textview1;
    TextView textview2;
    TextView textview3;

    ImageView img1;
    ImageView img2;
    ImageView img3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_data);

        textview1 = (TextView) findViewById(R.id.textView1);
        textview2 = (TextView) findViewById(R.id.textView2);
        textview3 = (TextView) findViewById(R.id.textView3);

        img1 = (ImageView) findViewById(R.id.imageView1);
        img2 = (ImageView) findViewById(R.id.imageView2);
        img3 = (ImageView) findViewById(R.id.imageView3);


       stations = new ArrayList<station>();
       spinner = (Spinner) findViewById(R.id.spinner);
               new Retrievedata().execute();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedItem = stations.get(position).getID();
                new Retrievedata().execute();
                img1.setVisibility(View.INVISIBLE);
                img2.setVisibility(View.INVISIBLE);
                img3.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // nothing happens
            }

        });


        Button bt = (Button) findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                String ptag = "&year=" + c.get(Calendar.YEAR) + "&month=" + (c.get(Calendar.MONTH) + 1) + "&day=" + c.get(Calendar.DAY_OF_MONTH);
                new LoadPictures().execute("http://www.amsz.hu/ws/includes/chart/chart.php?type=day&array=temperature&scale=24h&id=" + selectedItem + ptag,
                        img1);
                new LoadPictures().execute("http://www.amsz.hu/ws/includes/chart/chart.php?type=day&array=humidity&scale=24h&id=" + selectedItem + ptag,
                        img2);
                new LoadPictures().execute("http://www.amsz.hu/ws/includes/chart/chart.php?type=day&array=pressure&scale=24h&id=" + selectedItem + ptag,
                        img3);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.current_data, menu);
        return true;
    }


    // Adatok letöltése háttérszálon. HTTP POST segítségével elküldésre kerülnek az igényeink (resp), melyek hatására megkapjuk a nyers adatsort.
    // Az adatsor különböző szeparátorokat (; #) tartalmaz, melyeket split segítségével darabolunk.

    class Retrievedata extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.amsz.hu/android.php");

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                if (selectedItem == 0) nameValuePairs.add(new BasicNameValuePair("resp", "currlist"));
                else {
                    nameValuePairs.add(new BasicNameValuePair("resp", "getdata"));
                    nameValuePairs.add(new BasicNameValuePair("id", Integer.toString(selectedItem)));
                }
                if (selectedItem != -1) {
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    response = httpclient.execute(httppost);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        result = EntityUtils.toString(response.getEntity(), "iso-8859-2");
                    }
                } else result = "";


            } catch (ClientProtocolException e) {
                Toast.makeText(getApplicationContext(), "Hiba a kapcsolódás során!",Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Váratlan hiba",Toast.LENGTH_LONG).show();
            }
           return 1;
        }

        // Feldolgozás
        @Override
        protected void onPostExecute(Integer h) {

            try {
                if (selectedItem == 0 && result != "") {
                    String[] sp = result.split("#");
                    for (int i = 0; i < sp.length; i++) {
                        String[] sp2 = sp[i].split(";");
                        stations.add(new station(Integer.parseInt(sp2[0]), sp2[1]));
                        //stations.add(new station(1, "b"));
                    }

                    Collections.sort(stations, new Comparator<station>() {
                        @Override
                        public int compare(station station1, station station2) {
                            return station1.getLocation().compareToIgnoreCase(station2.getLocation());
                        }
                    });

                    stations.add(0, new station(-1, "kérem válasszon..."));

                    adapter = new ArrayAdapter<station>(CurrentData.this,
                            android.R.layout.simple_spinner_item, stations);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    if (result == "null" || result == "") result = "-;-;-";
                    String[] sp = result.split(";");
                    textview1.setText(sp[0] + " °C");
                    textview2.setText(sp[1] + " %");
                    textview3.setText(sp[2] + " hPa");

                }
            } catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(), "Hiba a feldolgozás során!", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Ha a gombra rákattintunk, akkor letöltésre kerülnek a grafikonok a szerverről
    class LoadPictures extends AsyncTask<Object, Void, Integer> {

        Drawable d;
        ImageView iv;
        @Override
        protected Integer doInBackground(Object... params) {
            try {

                URL url = new URL((String)params[0]);
                iv = (ImageView)params[1];
                InputStream content = (InputStream) url.getContent();
                d = Drawable.createFromStream(content, "src");

            } catch (IOException e) {
                e.printStackTrace();
            }

            return 1;
        }

        @Override
        protected void onPostExecute(Integer h) {
            iv.setImageDrawable(d);
            iv.setVisibility(View.VISIBLE);
        }
    }

}
