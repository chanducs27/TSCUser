package com.fantasik.tscuser.tscuser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RateTripActivity extends AppCompatActivity {

    @BindView(R.id.txtFare)
    TextView txtFare;
    @BindView(R.id.txtDistance)
    TextView txtDistance;
    @BindView(R.id.imgPickupUser)
    ImageView imgPickupUser;
    @BindView(R.id.txtDriverName)
    TextView txtDriverName;
    @BindView(R.id.txttotalCost)
    TextView txttotalCost;
    @BindView(R.id.txtPresenttimme)
    TextView txtPresenttimme;
    @BindView(R.id.txtPickModeCash)
    TextView txtPickModeCash;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.txtComment)
    EditText txtComment;
    @BindView(R.id.btnhelp)
    ImageButton btnhelp;
    @BindView(R.id.btnrate)
    ImageButton btnrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_trip);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btnhelp, R.id.btnrate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnhelp:
                break;
            case R.id.btnrate:

                break;
        }
    }
}
