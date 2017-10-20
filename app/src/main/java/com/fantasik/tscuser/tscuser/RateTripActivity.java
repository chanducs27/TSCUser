package com.fantasik.tscuser.tscuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.fantasik.tscuser.tscuser.Util.GsonRequest;
import com.fantasik.tscuser.tscuser.Util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fantasik.tscuser.tscuser.Util.Utils.Base_URL;

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

    String ratedValue;
    SessionManager sd ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_trip);
        ButterKnife.bind(this);
        sd = new SessionManager(getApplicationContext());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtFare.setText(getIntent().getStringExtra("fare") + " $");
        txtDistance.setText(getIntent().getStringExtra("distance") + " Km");

       /*  String img2driver = getIntent().getStringExtra("imgdriver");

       if (img2driver != null) {
            byte[] img = Base64.decode(img2driver,  Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            imgPickupUser.setImageBitmap(bitmap);

        }*/

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                ratedValue = String.valueOf(ratingBar.getRating());
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


    @OnClick({R.id.btnhelp, R.id.btnrate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnhelp:
                break;
            case R.id.btnrate:
             String userid =   sd.getUserDetails().userid;
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String url = Base_URL + "/RatingbyUser";
                final JSONObject GH = new JSONObject();
                try {
                    GH.put("userid", userid);
                    GH.put("driverid", getIntent().getStringExtra("driverid"));
                    GH.put("rating", ratedValue);
                    GH.put("comment", txtComment.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                GsonRequest<String> getRequest = new GsonRequest<String>(Request.Method.POST, url, String.class, null, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                            final ProgressDialog pd = new ProgressDialog(RateTripActivity.this);
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
                            Intent intent = new Intent(RateTripActivity.this, UserMActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                }, GH);

                getRequest.setShouldCache(false);
                requestQueue.add(getRequest);
                break;
        }
    }
}
