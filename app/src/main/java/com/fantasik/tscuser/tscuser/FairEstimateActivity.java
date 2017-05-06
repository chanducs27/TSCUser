package com.fantasik.tscuser.tscuser;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FairEstimateActivity extends AppCompatActivity {

    @BindView(R.id.txtFare)
    TextView txtFare;
    @BindView(R.id.txtDistance)
    TextView txtDistance;
    @BindView(R.id.txtFrom)
    TextView txtFrom;
    @BindView(R.id.txtToAddre)
    TextView txtToAddre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fair_estimate);
        ButterKnife.bind(this);

        setTitle("Fair Estimate");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String pickad = getIntent().getStringExtra("fromaddr");
        String toaddr = getIntent().getStringExtra("toaddr");
        String totaldist = getIntent().getStringExtra("totaldist") + " km";
        String totalcost = getIntent().getStringExtra("totalcost") + " Rs.";

txtFrom.setText(pickad);
        txtToAddre.setText(toaddr);
        txtDistance.setText(totaldist);
        txtFare.setText(totalcost);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
