package com.fantasik.tscuser.tscuser;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.location.LocationListener;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.fantasik.tscuser.tscuser.Util.Driverloc;
import com.fantasik.tscuser.tscuser.Util.GsonRequest;
import com.fantasik.tscuser.tscuser.Util.RideDetails;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import static com.fantasik.tscuser.tscuser.Util.Utils.MY_PREFS_NAME;

public class ArrivingDriverActivity extends AppCompatActivity implements OnMapReadyCallback,LocationListener {
    GoogleMap googleMap;
    double startlat, startlng;
    Handler handlerDriverLocation;
    Handler handlerDriverStart;
    String driverid, rideid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arriving_driver);

        setTitle("Driver Arriving");
      //  getActionBar().setDisplayHomeAsUpEnabled(true);
        ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        handlerDriverLocation = new Handler();
        scheduleGetDriverLocation();
        scheduleCheckDriverStartConfirm ();
         startlat = Double.parseDouble(getIntent().getStringExtra("startlat"));
         startlng = Double.parseDouble( getIntent().getStringExtra("startlng"));
         driverid = getIntent().getStringExtra("driverid");
        rideid = getIntent().getStringExtra("rideid");
    }

    private final int FIVE_SECONDS = 5000;
    public void scheduleGetDriverLocation() {
        handlerDriverLocation.postDelayed(new Runnable() {
            public void run() {
                GetDriverLocation();          // this method will contain your almost-finished HTTP calls
                handlerDriverLocation.postDelayed(this, FIVE_SECONDS);
            }
        }, FIVE_SECONDS);
    }

    private final int TEN_SECONDS = 10000;
    public void scheduleCheckDriverStartConfirm() {
        handlerDriverStart.postDelayed(new Runnable() {
            public void run() {
                CheckDriverStartConfirm();          // this method will contain your almost-finished HTTP calls
                handlerDriverStart.postDelayed(this, TEN_SECONDS);
            }

        }, TEN_SECONDS);
    }


    private void CheckDriverStartConfirm() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://10.0.2.2:8076/Service1.svc/IsRideStartedbyDriver";
        final JSONObject GH = new JSONObject();
        try {
            GH.put("rideid", rideid );

        } catch (JSONException e) {
            e.printStackTrace();
        }

        GsonRequest<String> getRequest = new GsonRequest<String>(Request.Method.POST, url,String.class, null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {

                if(response == "true") {
                    final ProgressDialog pd = new ProgressDialog(ArrivingDriverActivity.this);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage("Your ride is started..");
                    pd.setCancelable(false);
                    pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
                    pd.setIndeterminate(true);
                    pd.show();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pd.dismiss();
                    Intent intent = new Intent(ArrivingDriverActivity.this, RateTripActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }, GH);

        getRequest.setShouldCache(false);
        requestQueue.add(getRequest);

    }

    Marker mr = null;
    private void GetDriverLocation()
{
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    String url = "http://10.0.2.2:8076/Service1.svc/GetDriverloc";
    final JSONObject GH = new JSONObject();
    try {
        GH.put("driverid", driverid );

    } catch (JSONException e) {
        e.printStackTrace();
    }

    GsonRequest<Driverloc> getRequest = new GsonRequest<Driverloc>(Request.Method.POST, url,Driverloc.class, null, new Response.Listener<Driverloc>() {
        @Override
        public void onResponse(Driverloc response)
        {

            if(response != null) {
                Driverloc dd = response;
                if (mr == null) {
                    int height = 100;
                    int width = 100;
                    BitmapDrawable bitmapdraw = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.car_top_view, null);
                    if (bitmapdraw != null) {
                        Bitmap b = bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                        mr = googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(dd.lat), Double.parseDouble(dd.lng)))
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                    }
                } else {
                    mr.setPosition(new LatLng(Double.parseDouble(dd.lat), Double.parseDouble(dd.lng)));
                }
            }
        }
    }, new com.android.volley.Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {


        }
    }, GH);

    getRequest.setShouldCache(false);
    requestQueue.add(getRequest);
}

    @Override
    protected void onResume() {
        scheduleGetDriverLocation();
        scheduleCheckDriverStartConfirm();
        super.onResume();
    }

    @Override
    protected void onPause() {
        handlerDriverLocation.removeCallbacksAndMessages(null);
        handlerDriverStart.removeCallbacksAndMessages(null);
        super.onPause();
    }

    @Override
    protected void onStop() {
        handlerDriverLocation.removeCallbacksAndMessages(null);
        handlerDriverStart.removeCallbacksAndMessages(null);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onMapReady(GoogleMap gogleMap) {
        googleMap = gogleMap;

        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);

        MarkerOptions startLocMarker = new MarkerOptions();
        startLocMarker.position(new LatLng(startlat, startlng));
        googleMap.addMarker(startLocMarker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(startlat, startlng)));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
       // scheduleGetDriverLocation();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

/*    private final int FIVE_SECONDS = 5000;
    public void scheduleGetDriverLocation() {
        handler.postDelayed(new Runnable() {
            public void run() {
                GetDriverLocation();          // this method will contain your almost-finished HTTP calls
                handler.postDelayed(this, FIVE_SECONDS);
            }
        }, FIVE_SECONDS);
    }*/


}
