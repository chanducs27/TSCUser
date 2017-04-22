package com.fantasik.tscuser.tscuser;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MapConfirmActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap googleMap;

    Unbinder unbinder;
    @BindView(R.id.finalSearch)
    RelativeLayout finalSearch;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Confirmation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_map_confirm);
        ButterKnife.bind(this);
        ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMapAsync(this);

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


    private String GetAddressfromLocation(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = "";
            for (int i = 0; i <= 1; i++) {
                address += addresses.get(0).getAddressLine(i) + ",";
            }

            //  String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            if (address != "")
                return address;
            else
                return city + "," + state;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    public void onMapReady(GoogleMap gogleMap) {
        googleMap = gogleMap;

    }


}
