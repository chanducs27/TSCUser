package com.fantasik.tscuser.tscuser;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fantasik.tscuser.tscuser.Util.ResizeWidthAnimation;
import com.fantasik.tscuser.tscuser.Util.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.content.Context.LOCATION_SERVICE;

public class MapsActivity extends Fragment implements OnMapReadyCallback, LocationListener {

    GoogleMap googleMap;
    @BindView(R.id.startlocpopup)
    RelativeLayout startlocpopup;
    Unbinder unbinder;
    private boolean mMapIsTouched = false;
    private TouchableWrapper mTouchView;

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
        googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

      //  startlocpopup.setVisibility(View.VISIBLE);

        StartPicupAnimation(130);
        startlocpopup.setVisibility(View.VISIBLE);
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

    @OnClick(R.id.startlocpopup)
    public void onViewClicked() {
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
                    StartPicupAnimation(0);
                    startlocpopup.setVisibility(View.GONE);
                    break;

                case MotionEvent.ACTION_UP:
                    mMapIsTouched = false;
                    StartPicupAnimation(130);
                    startlocpopup.setVisibility(View.VISIBLE);
                    break;
            }

            return super.dispatchTouchEvent(ev);
        }
    }

}
