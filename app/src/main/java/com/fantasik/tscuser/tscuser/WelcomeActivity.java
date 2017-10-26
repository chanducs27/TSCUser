package com.fantasik.tscuser.tscuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.fantasik.tscuser.tscuser.Util.GsonRequest;
import com.fantasik.tscuser.tscuser.Util.SessionManager;
import com.fantasik.tscuser.tscuser.Util.UserDetails;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fantasik.tscuser.tscuser.Util.Utils.Base_URL;
import static com.fantasik.tscuser.tscuser.Util.Utils.MY_PREFS_NAME;

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.button)
    Button button;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.textView4)
    TextView textView4;
    @BindView(R.id.btnlogin)
    ImageButton btnlogin;
    @BindView(R.id.btnregister)
    ImageButton btnregister;
    @BindView(R.id.activity_welcome)
    LinearLayout activityWelcome;
    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        // Session class instance
        session = new SessionManager(getApplicationContext());

        if(session.isLoggedIn()) {
        RedirecttoMain_ifLogged();
        }

      }

    private void RedirecttoMain_ifLogged() {
        final ProgressDialog pd = new ProgressDialog(WelcomeActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading.........");
        pd.setCancelable(false);
        pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
        pd.setIndeterminate(true);
        pd.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Base_URL + "/GetUserDetailsById";
        final JSONObject GH =new JSONObject();
        try {
            GH.put("userid",session.getUserDetails().userid);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        GsonRequest<UserDetails> getRequest = new GsonRequest<UserDetails>(Request.Method.POST, url,UserDetails.class, null, new Response.Listener<UserDetails>() {
            @Override
            public void onResponse(UserDetails response)
            {
                pd.dismiss();
                if(response != null) {
                    UserDetails dd = response;

                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("userid", dd.userid);
                    editor.putString("mobile", dd.mobile);
                    editor.putString("name", dd.name);
                    editor.putString("username", dd.username);

                    editor.apply();

                    session.createLoginSession(dd.userid,dd.name, dd.username,dd.mobile,dd.pass,dd.imguser);

                    Intent intent = new Intent(WelcomeActivity.this, UserMActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.clear();

                editor.apply();
            }
        }, GH);

        getRequest.setShouldCache(false);
        requestQueue.add(getRequest);
    }

    @OnClick({R.id.btnlogin, R.id.btnregister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnlogin:
                Intent intent2 = new Intent(WelcomeActivity.this, Login1Activity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;
            case R.id.btnregister:
                Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;
        }
    }
}
