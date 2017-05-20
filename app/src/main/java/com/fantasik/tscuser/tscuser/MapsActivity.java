package com.fantasik.tscuser.tscuser;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.fantasik.tscuser.tscuser.Util.Driverloc;
import com.fantasik.tscuser.tscuser.Util.GsonRequest;
import com.fantasik.tscuser.tscuser.Util.ResizeWidthAnimation;
import com.fantasik.tscuser.tscuser.Util.Utils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.fantasik.tscuser.tscuser.Util.Utils.Base_URL;
import static com.fantasik.tscuser.tscuser.Util.Utils.MY_PREFS_NAME;

public class MapsActivity extends Fragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener {

    GoogleMap googleMap;

    Unbinder unbinder;


    @BindView(R.id.imgPickup)
    ImageView imgPickup;

    @BindView(R.id.txtPickAddress)
    TextView txtPickAddress;
    @BindView(R.id.pickuplocSearch)
    RelativeLayout pickuplocSearch;
    @BindView(R.id.imgDrop)
    ImageView imgDrop;
    @BindView(R.id.textView5)
    TextView textView5;
    @BindView(R.id.txtDropAddress)
    TextView txtDropAddress;
    @BindView(R.id.destinationSearch)
    RelativeLayout destinationSearch;
    @BindView(R.id.imageView4)
    ImageView imageView4;
    @BindView(R.id.markerlayout)
    RelativeLayout markerlayout;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.imageView2)
    ImageView imageView2;
    @BindView(R.id.startlocpoupMain)
    LinearLayout startlocpoupMain;
    @BindView(R.id.startlocpopup)
    RelativeLayout startlocpopup;
    private boolean mMapIsTouched = false;
    private TouchableWrapper mTouchView;
    boolean isPickupSelected = false;
    boolean isDropSelected = false;
    LatLng pickupLocation, dropLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        unbinder = ButterKnife.bind(this, view);


        mTouchView = new TouchableWrapper(getActivity());
        mTouchView.addView(view);

        return mTouchView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //show error dialog if GoolglePlayServices not available
        startlocpopup.setVisibility(View.GONE);
        //setContentView(R.layout.content_user_map);
        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

   pickuplocSearch.setOnTouchListener(new View.OnTouchListener() {
       @Override
       public boolean onTouch(View view, MotionEvent motionEvent) {
           int eid = motionEvent.getAction();
           switch (eid) {
               case MotionEvent.ACTION_DOWN:
               {
                   pickuplocSearch.setBackgroundColor(Color.parseColor(getString(R.string.lightgraycolor)));
               break;
               }
               case MotionEvent.ACTION_UP:
               {
                   pickuplocSearch.setBackgroundColor(Color.WHITE);
                   CallPlacePicker();
                   break;
               }
           }
           return true;
       }
   });

        destinationSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int eid = motionEvent.getAction();
                switch (eid) {
                    case MotionEvent.ACTION_DOWN:
                    {
                        destinationSearch.setBackgroundColor(Color.parseColor(getString(R.string.lightgraycolor)));
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        destinationSearch.setBackgroundColor(Color.WHITE);
                        CallPlacePicker();
                        break;
                    }
                }
                return true;
            }
        });
    }

    private void CallPlacePicker()
    {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    // GPS location change event
    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        if (!isPickupSelected) {
            googleMap.addMarker(new MarkerOptions().position(latLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            txtPickAddress.setText(GetAddressfromLocation(latitude, longitude));
        }
    }

    private String GetAddressfromLocation(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = "";
               address += addresses.get(0).getAddressLine(0);
            if(addresses.get(0).getMaxAddressLineIndex() > 1)
            {
                for (int i = 0; i <= 1; i++) {
                    address += addresses.get(0).getAddressLine(i) + " ";
                }
            }

            //  String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            if (!address.equals(""))
                return address;
            else
                return city + "," + state;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void StartPicupAnimation(int width) {
        ResizeWidthAnimation anim = new ResizeWidthAnimation(startlocpopup, Math.round(Utils.convertDpToPixel(width)));
        anim.setDuration(500);
        startlocpopup.startAnimation(anim);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }


    @Override
    public void onMapReady(GoogleMap gogleMap) {
        googleMap = gogleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    200);
        }
        else {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            View locationButton = ((View) fragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.setMargins(0, 0, 30, 30);

            googleMap.setOnCameraIdleListener(this);
            googleMap.setOnCameraMoveStartedListener(this);
            googleMap.setOnCameraMoveListener(this);
            googleMap.setOnCameraMoveCanceledListener(this);

            StartPicupAnimation(130);
            startlocpopup.setVisibility(View.VISIBLE);
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCameraIdle() {
        double latitude = googleMap.getCameraPosition().target.latitude;
        double longitude = googleMap.getCameraPosition().target.longitude;
        if (latitude != 0.0) {
            if (!isPickupSelected) {
                txtPickAddress.setText(GetAddressfromLocation(latitude, longitude));

                StartPicupAnimation(130);
                startlocpopup.setVisibility(View.VISIBLE);
            }
        }

        GetNearbyVehicles(latitude, longitude );

    }

    private void GetNearbyVehicles(double latitude, double longitude) {

        Driverloc[] locs = null;
        RequestQueue requestQueue = Volley.newRequestQueue(this.getActivity());
        String url = Base_URL + "/GetNearbyDriverloc";
        final SharedPreferences editorread = this.getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        final JSONObject GH =new JSONObject();
        try {
            GH.put("userid",editorread.getString("userid", ""));
            GH.put("userlat", String.format("%.6f", latitude));
            GH.put("userlng", String.format("%.6f", longitude));



        } catch (JSONException e) {
            e.printStackTrace();
        }
        googleMap.clear();
        GsonRequest<Driverloc[]> getRequest = new GsonRequest<Driverloc[]>(Request.Method.POST, url,Driverloc[].class, null, new Response.Listener<Driverloc[]>() {
            @Override
            public void onResponse(Driverloc[] response)
            {

                Driverloc[] ghk = response;
                for (int i=0;i<ghk.length;i++)
                {
                    int height = 100;
                    int width = 100;
                    BitmapDrawable bitmapdraw=(BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.car_top_view, null);
                    if(bitmapdraw != null) {
                        Bitmap b = bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(ghk[i].lat), Double.parseDouble(ghk[i].lng)))
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                    }
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {

            }
        }, GH);

        getRequest.setShouldCache(false);
        requestQueue.add(getRequest);
    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {
        if (!isPickupSelected)
            txtPickAddress.setText("Getting Address...");
    }
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    @OnClick({R.id.startlocpopup, R.id.imgDrop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.destinationSearch:

                break;
            case R.id.imageView2:
                break;
            case R.id.startlocpopup:
                isPickupSelected = true;
                pickupLocation = googleMap.getCameraPosition().target;
                startlocpopup.setVisibility(View.GONE);
                destinationSearch.setVisibility(View.VISIBLE);
                break;
            case R.id.imgDrop:



                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                if (isPickupSelected) {
                    txtDropAddress.setText(place.getName());
                    isDropSelected = true;
                    dropLocation = place.getLatLng();

                    Bundle args = new Bundle();
                    args.putParcelable("from_position", pickupLocation);
                    args.putParcelable("to_position", dropLocation);

                    Intent intent = new Intent(getActivity(), MapConfirmActivity.class);
                    intent.putExtra("picaddress",txtPickAddress.getText());
                    intent.putExtra("dropaddress",txtDropAddress.getText());
                    intent.putExtra("locations",args);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                } else {
                    txtPickAddress.setText(place.getName());
                    pickupLocation = place.getLatLng();
                    isPickupSelected = true;

                        googleMap.addMarker(new MarkerOptions().position(place.getLatLng()));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private class TouchableWrapper extends FrameLayout {
        public TouchableWrapper(@NonNull Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mMapIsTouched = true;
                    if (!isPickupSelected) {
                        StartPicupAnimation(0);
                        startlocpopup.setVisibility(View.GONE);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    mMapIsTouched = false;

                    if (!isPickupSelected) {
                        startlocpopup.setVisibility(View.VISIBLE);
                        StartPicupAnimation(130);
                    }
                    break;
            }

            return super.dispatchTouchEvent(ev);
        }
    }

}
