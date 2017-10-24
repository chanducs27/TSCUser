package com.fantasik.tscuser.tscuser;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.fantasik.tscuser.tscuser.Util.DataParser;
import com.fantasik.tscuser.tscuser.Util.DriverDetails;
import com.fantasik.tscuser.tscuser.Util.GsonRequest;
import com.fantasik.tscuser.tscuser.Util.RideDetails;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.fantasik.tscuser.tscuser.Util.Utils.Base_URL;
import static com.fantasik.tscuser.tscuser.Util.Utils.GetVehicleTypeName;
import static com.fantasik.tscuser.tscuser.Util.Utils.MY_PREFS_NAME;

public class MapConfirmActivity extends AppCompatActivity implements OnMapReadyCallback {
    Handler handlerDriverAccept;
    GoogleMap googleMap;

    Unbinder unbinder;
    @BindView(R.id.finalSearch)
    RelativeLayout finalSearch;
    @BindView(R.id.txtPickup)
    TextView txtPickup;
    @BindView(R.id.txtDrop)
    TextView txtDrop;

    LatLng pickupLocation, dropLocation;


    @BindView(R.id.txtchange)
    TextView txtchange;
    @BindView(R.id.frmFairEstimate)
    LinearLayout frmFairEstimate;
    @BindView(R.id.frmPromoCoed)
    LinearLayout frmPromoCoed;
    @BindView(R.id.butNext)
    Button butNext;
    ProgressDialog pdWaitingdriver = null;

    float totaldistanceinkm = 0.0f;
    double totaldist = 0, totalcost = 0;
    String promocode = "";
    String mode = "Cash";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Confirmation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        handlerDriverAccept = new Handler();
        setContentView(R.layout.activity_map_confirm);
        ButterKnife.bind(this);
        ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        txtPickup.setText(getIntent().getExtras().getString("picaddress"));
        txtDrop.setText(getIntent().getExtras().getString("dropaddress"));
        Bundle bundle = getIntent().getParcelableExtra("locations");
        pickupLocation = bundle.getParcelable("from_position");
        dropLocation = bundle.getParcelable("to_position");
        finalSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int eid = motionEvent.getAction();
                switch (eid) {
                    case MotionEvent.ACTION_DOWN: {
                        finalSearch.setBackgroundColor(Color.GRAY);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        finalSearch.setBackgroundColor(Color.WHITE);

                        break;
                    }
                }
                return true;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return(super.onOptionsItemSelected(item));
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    Dialog dlg;
    @OnClick({R.id.txtchange, R.id.frmFairEstimate, R.id.frmPromoCoed, R.id.relMain, R.id.butNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.txtchange:
                break;
            case R.id.frmFairEstimate:

                break;
            case R.id.frmPromoCoed:

                break;
            case R.id.relMain:
                break;
            case R.id.butNext:
                final ProgressDialog pd = new ProgressDialog(MapConfirmActivity.this);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage("Loading.........");
                pd.setCancelable(false);
                pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
                pd.setIndeterminate(true);
                pd.show();
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String url = Base_URL + "/InsertRouteDetailsAfterUserRequest";
                final JSONObject GH = new JSONObject();
                try {
                    GH.put("userid", prefs.getString("userid", ""));
                    GH.put("startlat", String.format("%.6f", pickupLocation.latitude));
                    GH.put("startlng", String.format("%.6f", pickupLocation.longitude));
                    GH.put("endlat", String.format("%.6f", dropLocation.latitude));
                    GH.put("endlng", String.format("%.6f", dropLocation.longitude));

                    GH.put("fare", String.valueOf(totalcost));
                    GH.put("promocode", String.valueOf(promocode));
                    GH.put("distance", String.valueOf(totaldist));
                    GH.put("mode", String.valueOf(mode));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                GsonRequest<RideDetails> getRequest = new GsonRequest<RideDetails>(Request.Method.POST, url, RideDetails.class, null, new Response.Listener<RideDetails>() {
                    @Override
                    public void onResponse(RideDetails response) {
                        pd.dismiss();
                        if (response != null) {
                            RideDetails dd = response;


                            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                            editor.putString("rideid", dd.rideid);
                            editor.putString("routeid", dd.routeid);
                            editor.apply();
                            pdWaitingdriver = new ProgressDialog(MapConfirmActivity.this);
                            pdWaitingdriver.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pdWaitingdriver.setMessage("Waiting for driver Confirmation....");
                            pdWaitingdriver.setCancelable(false);
                            pdWaitingdriver.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
                            pdWaitingdriver.setIndeterminate(true);
                            pdWaitingdriver.show();
                            scheduleGetAcceptedDriver();

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("rideid", "");
                        editor.putString("routeid", "");
                        editor.apply();
                    }
                }, GH);

                getRequest.setShouldCache(false);
                requestQueue.add(getRequest);
                break;
        }
    }

    private final int FIVE_SECONDS = 5000;

    public void scheduleGetAcceptedDriver() {
        handlerDriverAccept.postDelayed(new Runnable() {
            public void run() {
                GetAcceptedDriver();          // this method will contain your almost-finished HTTP calls
                handlerDriverAccept.postDelayed(this, FIVE_SECONDS);
            }
        }, FIVE_SECONDS);
    }

    private void GetAcceptedDriver() {

        final SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Base_URL + "/GetDriverDetailsWhoAcceptedRequest";
        final JSONObject GH = new JSONObject();
        try {
            GH.put("rideid", prefs.getString("rideid", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GsonRequest<DriverDetails> getRequest = new GsonRequest<DriverDetails>(Request.Method.POST, url, DriverDetails.class, null, new Response.Listener<DriverDetails>() {
            @Override
            public void onResponse(DriverDetails response) {
                if (response != null) {
                    final DriverDetails dd = response;
                    pdWaitingdriver.dismiss();
                    handlerDriverAccept.removeCallbacksAndMessages(null);

                    final Dialog dialog = new Dialog(MapConfirmActivity.this);
                    dialog.setContentView(R.layout.booking_confirm);

                    //  TextView text = (TextView) dialog.findViewById(R.id.text);
                    //  text.setText("Android custom dialog example!");

                    TextView txtAccept = (TextView) dialog.findViewById(R.id.btnDone);
                    // if button is clicked, close the custom dialog
                    txtAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Intent intent = new Intent(MapConfirmActivity.this, ArrivingDriverActivity.class);
                            intent.putExtra("startlat", String.format("%.6f", pickupLocation.latitude));
                            intent.putExtra("startlng", String.format("%.6f", pickupLocation.longitude));
                            intent.putExtra("driverid", dd.driverid);
                            intent.putExtra("dmobile", dd.mobile);
                            intent.putExtra("dname", dd.name);
                            intent.putExtra("vehdetails", GetVehicleTypeName(dd.vehtypeid) + "-" + dd.vehbrand + "-" + dd.vehcolor + "-" + dd.vehplateno);
                            intent.putExtra("rideid", prefs.getString("rideid", ""));
                            intent.putExtra("imgdriver", dd.imgdriver);
                            intent.putExtra("fare", String.valueOf(totalcost));
                            intent.putExtra("promocode", String.valueOf(promocode));
                            intent.putExtra("distance", String.valueOf(totaldist));
                            intent.putExtra("mode", String.valueOf(mode));
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                        }
                    });


                    dialog.show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdWaitingdriver.dismiss();

            }
        }, GH);

        getRequest.setShouldCache(false);
        requestQueue.add(getRequest);
    }

    @Override
    public void onMapReady(GoogleMap gogleMap) {
        googleMap = gogleMap;

        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        // Getting URL to the Google Directions API
        String url = getUrl(pickupLocation, dropLocation);
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);

        googleMap.addMarker(new MarkerOptions().position(pickupLocation));
        googleMap.addMarker(new MarkerOptions().position(dropLocation));
        //move map camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(pickupLocation));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        frmFairEstimate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int eid = motionEvent.getAction();
                switch (eid) {
                    case MotionEvent.ACTION_DOWN: {
                        frmFairEstimate.setBackgroundColor(Color.parseColor(getString(R.string.lightgraycolor)));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        frmFairEstimate.setBackgroundColor(Color.WHITE);
                        Intent intent = new Intent(getBaseContext(), FairEstimateActivity.class);
                        intent.putExtra("fromaddr", getIntent().getExtras().getString("picaddress"));
                        intent.putExtra("toaddr", getIntent().getExtras().getString("dropaddress"));
                        intent.putExtra("totaldist", String.valueOf(totaldist));
                        intent.putExtra("totalcost", String.valueOf(totalcost));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                        break;
                    }
                }
                return true;
            }
        });

        frmPromoCoed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int eid = motionEvent.getAction();
                switch (eid) {
                    case MotionEvent.ACTION_DOWN: {
                        frmPromoCoed.setBackgroundColor(Color.parseColor(getString(R.string.lightgraycolor)));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        frmPromoCoed.setBackgroundColor(Color.WHITE);
                        dlg = new Dialog(MapConfirmActivity.this);
                        dlg.setContentView(R.layout.rel_promocode);

                        TextView txtCancel = (TextView) dlg.findViewById(R.id.btnCancel_promo);
                        // if button is clicked, close the custom dialog
                        txtCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dlg.dismiss();

                            }
                        });


                        TextView txtApply = (TextView) dlg.findViewById(R.id.btnApply_promo);
                        // if button is clicked, close the custom dialog
                        txtApply.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText txtPromo = (EditText) dlg.findViewById(R.id.txtPromo);
                                promocode = txtPromo.getText().toString();
                                dlg.dismiss();

                            }
                        });

                        dlg.show();
                        break;
                    }
                }
                return true;
            }
        });
    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        LatLng prevloc = null, newloc = null;

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;


            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (newloc != null) {
                        prevloc = newloc;
                    }
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    newloc = position;

                    if (prevloc != null) {
                        Location crntLocation = new Location("crntlocation");
                        crntLocation.setLatitude(prevloc.latitude);
                        crntLocation.setLongitude(prevloc.longitude);

                        Location newLocation = new Location("newlocation");
                        newLocation.setLatitude(newloc.latitude);
                        newLocation.setLongitude(newloc.longitude);

                        totaldistanceinkm += crntLocation.distanceTo(newLocation);
                        points.add(position);
                    }

                }

                totaldistanceinkm = totaldistanceinkm / 1000.0f;
                totaldist = Math.round(totaldistanceinkm * 100.0) / 100.0;
                totalcost = Math.round(totaldistanceinkm * 10 * 100.0) / 100.0;

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                googleMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }
}
