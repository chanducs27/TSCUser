package com.fantasik.tscuser.tscuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.fantasik.tscuser.tscuser.Util.GsonRequest;
import com.fantasik.tscuser.tscuser.Util.SPreferences;
import com.fantasik.tscuser.tscuser.Util.SessionManager;
import com.fantasik.tscuser.tscuser.Util.UserDetails;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fantasik.tscuser.tscuser.Util.Utils.Base_URL;
import static com.fantasik.tscuser.tscuser.Util.Utils.MY_PREFS_NAME;

public class Login1Activity extends AppCompatActivity {

    @BindView(R.id.glogin)
    ImageButton glogin;
    @BindView(R.id.flogin)
    ImageButton flogin;
    @BindView(R.id.txtusername)
    EditText txtusername;
    @BindView(R.id.tPass)
    EditText tPass;
    @BindView(R.id.butNext)
    Button butNext;
    @BindView(R.id.txtForgetPass)
    TextView txtForgetPass;

    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        ButterKnife.bind(this);
        SPreferences.ClearPreferences(this);

        // Session class instance
        session = new SessionManager(getApplicationContext());
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

    @OnClick({R.id.glogin, R.id.flogin, R.id.butNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.glogin:
                break;
            case R.id.flogin:
                break;
            case R.id.butNext:
                final ProgressDialog pd = new ProgressDialog(Login1Activity.this);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage("Loading.........");
                pd.setCancelable(false);
                pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
                pd.setIndeterminate(true);
                pd.show();

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String url = Base_URL + "/userlogin";
                final JSONObject GH =new JSONObject();
                try {
                    GH.put("username",txtusername.getText());
                    GH.put("pass",tPass.getText());

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
                            editor.putString("username", String.valueOf(txtusername.getText()));
                            editor.putString("pass", String.valueOf(tPass.getText()));

                            editor.apply();

                            session.createLoginSession(dd.userid, dd.username);

                            Intent intent = new Intent(Login1Activity.this, UserMActivity.class);
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
                break;
        }
    }
}
