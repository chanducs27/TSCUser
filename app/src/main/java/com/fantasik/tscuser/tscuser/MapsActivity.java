package com.fantasik.tscuser.tscuser;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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


    }

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
            for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {
                address += addresses.get(0).getAddressLine(i) + ",";
            }

            //  String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            if (address != "")
                return address + city + "," + state;
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
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

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
            } else {
                txtDropAddress.setText(GetAddressfromLocation(latitude, longitude));
            }
        }
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
        else
            txtDropAddress.setText("Getting Address...");
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


                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.i(TAG, "Place: " + place.getName());
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
