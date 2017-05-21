package com.fantasik.tscuser.tscuser;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.fantasik.tscuser.tscuser.Util.Driverloc;
import com.fantasik.tscuser.tscuser.Util.GsonRequest;
import com.fantasik.tscuser.tscuser.Util.LatLngInterpolator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.fantasik.tscuser.tscuser.Util.Utils.Base_URL;
import static com.fantasik.tscuser.tscuser.Util.Utils.GetVehicleTypeName;

public class ArrivingDriverActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    GoogleMap googleMap;
    double startlat, startlng;
    Handler handlerDriverLocation;
    Handler handlerDriverStart;
    String driverid, rideid, vehdetials;

    @BindView(R.id.txtDriverName)
    TextView txtDriverName;
    @BindView(R.id.ratDriver)
    RatingBar ratDriver;
    @BindView(R.id.txtRatingSr)
    TextView txtRatingSr;
    @BindView(R.id.txtCarInfo)
    TextView txtCarInfo;
    @BindView(R.id.lnrdriver)
    LinearLayout lnrdriver;
    @BindView(R.id.txtDriverLocationAddress)
    TextView txtDriverLocationAddress;
    @BindView(R.id.lnr234)
    LinearLayout lnr234;
    @BindView(R.id.btnCancelBooking)
    Button btnCancelBooking;
    @BindView(R.id.reldriver)
    RelativeLayout reldriver;
    @BindView(R.id.imgdriver)
    CircleImageView imgdriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arriving_driver);
        ButterKnife.bind(this);

        setTitle("Driver Arriving");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        handlerDriverLocation = new Handler();
        handlerDriverStart = new Handler();

        //       scheduleGetDriverLocation();
        //     scheduleCheckDriverStartConfirm ();


        startlat = Double.parseDouble(getIntent().getStringExtra("startlat"));
        startlng = Double.parseDouble(getIntent().getStringExtra("startlng"));
        driverid = getIntent().getStringExtra("driverid");
        rideid = getIntent().getStringExtra("rideid");
        txtDriverName.setText(getIntent().getStringExtra("dname"));
        vehdetials = getIntent().getStringExtra("vehdetails");
        txtCarInfo.setText(vehdetials);

        String img2driver = getIntent().getStringExtra("imgdriver");
        if (img2driver != null) {
            byte[] img = Base64.decode(img2driver,  Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            imgdriver.setImageBitmap(bitmap);

        }


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
        String url = Base_URL + "/IsRideStartedbyDriver";
        final JSONObject GH = new JSONObject();
        try {
            GH.put("rideid", rideid);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        GsonRequest<String> getRequest = new GsonRequest<String>(Request.Method.POST, url, String.class, null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.substring(1,response.length()- 1).equals("true")) {
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
                    intent.putExtra("driverid", driverid);
                    intent.putExtra("rideid", rideid);
                    intent.putExtra("dname",getIntent().getStringExtra("dname"));
                    intent.putExtra("vehdetails",vehdetials);
                    intent.putExtra("imgdriver", getIntent().getStringExtra("imgdriver"));
                    intent.putExtra("fare", getIntent().getStringExtra("fare"));
                    intent.putExtra("promocode", getIntent().getStringExtra("promocode"));
                    intent.putExtra("distance", getIntent().getStringExtra("distance"));
                    intent.putExtra("mode", getIntent().getStringExtra("mode"));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }, GH);

        getRequest.setShouldCache(false);
        requestQueue.add(getRequest);

    }

    Marker mr = null;

    LatLng oldloc, newloc;

    private void GetDriverLocation() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Base_URL + "/GetDriverloc";
        final JSONObject GH = new JSONObject();
        try {
            GH.put("driverid", driverid);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        GsonRequest<Driverloc> getRequest = new GsonRequest<Driverloc>(Request.Method.POST, url, Driverloc.class, null, new Response.Listener<Driverloc>() {
            @Override
            public void onResponse(Driverloc response) {

                if (response != null && response.lat != null) {
                    Driverloc dd = response;

                    if (newloc != null) {
                        oldloc = newloc;
                    }
                    newloc = new LatLng(Double.parseDouble(dd.lat), Double.parseDouble(dd.lng));

                    if (mr == null) {
                        int height = 100;
                        int width = 100;
                        BitmapDrawable bitmapdraw = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.car_top_view, null);
                        if (bitmapdraw != null) {
                            Bitmap b = bitmapdraw.getBitmap();
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                            mr = googleMap.addMarker(new MarkerOptions()
                                    .position(newloc)
                                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                        }
                    } else {
                        float rotation = (float) SphericalUtil.computeHeading(oldloc, newloc);
                        rotateMarker(mr, newloc, rotation);
                        // mr.setPosition(new LatLng(Double.parseDouble(dd.lat), Double.parseDouble(dd.lng)));
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }, GH);

        getRequest.setShouldCache(false);
        requestQueue.add(getRequest);
    }


    private void rotateMarker(final Marker marker, final LatLng destination, final float rotation) {

        if (marker != null) {

            final LatLng startPosition = marker.getPosition();
            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, destination);
                        float bearing = computeRotation(v, startRotation, rotation);

                        marker.setRotation(bearing);
                        marker.setPosition(newPosition);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            valueAnimator.start();
        }
    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
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
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
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
        locationManager.requestLocationUpdates(bestProvider, 5000, 0, this);

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
