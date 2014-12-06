package hu.uniobuda.nik.andromet;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TempMap extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    HttpResponse response;
    String result;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_map);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    // térkép inicializálása
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(47.2,19.4),6.2f));
                new LoadData().execute();
            }
        }
    }

    // adatok letöltése háttérszálon
    class LoadData extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.amsz.hu/android.php");

            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("resp", "gmap"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // HTTP POST
                response = httpclient.execute(httppost);
                if (response.getStatusLine().getStatusCode() == 200)
                     result = EntityUtils.toString(response.getEntity(), "iso-8859-2");
                else result = "";

            } catch (ClientProtocolException e1) {
                e1.printStackTrace();
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return 1;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(TempMap.this);
            dialog.setMessage("Betöltés");
            dialog.setCancelable(false);
            dialog.setInverseBackgroundForced(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Integer h) {
            try {
                String[] sp = result.split("#");
                for (int i = 0; i < sp.length; i++) {
                    String[] sp2 = sp[i].split(";");
                    setUpMap(Float.parseFloat(sp2[2]), Float.parseFloat(sp2[1]), Float.parseFloat(sp2[0]));
                }
            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(), "Hiba a betöltés során!", Toast.LENGTH_LONG).show();
            }
            finally {
                dialog.hide();
                dialog.dismiss();
            }
        }
    }


    private void setUpMap(float value, float lat, float lon) {


        // értékek kirajzolása markerként, amihez bitmap van alkalmazva
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(100, 100, conf);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN);

        Paint paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(35);

        // length 1: width 40
        // length 2: width 63
        // length 3: width 20
        int drawWidth = 40;
        if (value <= -10) drawWidth = 20;
        else if (value >= 10) drawWidth = 63;

        canvas.drawBitmap(bmp, 0, 0, paint);
        canvas.drawCircle(50, 50, 25, paint);
        canvas.drawText(Integer.toString(Math.round(value)), drawWidth, 63, paintText);

        // marker hozzáadása a térképhez
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                .anchor(0.5f, 1));
    }
}

