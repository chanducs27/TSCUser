package com.fantasik.tscuser.tscuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fantasik.tscuser.tscuser.Util.GsonRequest;
import com.fantasik.tscuser.tscuser.Util.SPreferences;
import com.fantasik.tscuser.tscuser.Util.SessionManager;
import com.fantasik.tscuser.tscuser.Util.UserDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fantasik.tscuser.tscuser.Util.Utils.Base_URL;
import static com.fantasik.tscuser.tscuser.Util.Utils.MY_PREFS_NAME;


public class Login1Activity extends AppCompatActivity {

 //   @BindView(R.id.glogin)
 //   ImageButton glogin;
    @BindView(R.id.flogin)
    LoginButton flogin;
    @BindView(R.id.txtusername)
    EditText txtusername;
    @BindView(R.id.tPass)
    EditText tPass;
    @BindView(R.id.butNext)
    Button butNext;
    @BindView(R.id.txtForgetPass)
    TextView txtForgetPass;
    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;
    SessionManager session;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            setTitle("Login");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login1);
            ButterKnife.bind(this);
            SPreferences.ClearPreferences(this);
            awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
            // Session class instance
            session = new SessionManager(getApplicationContext());

            awesomeValidation.addValidation(this, R.id.txtusername, Patterns.EMAIL_ADDRESS, R.string.usernameerror);
            awesomeValidation.addValidation(this, R.id.tPass, "[a-zA-Z0-9_-]+", R.string.passerror);

            //Facebook
            //check if already logged in, go to main activity
            CheckFacebookLogin();

            callbackManager = CallbackManager.Factory.create();
            flogin.setReadPermissions(Arrays.asList(
                    "public_profile", "email"));
            flogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    // App code
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    Log.v("LoginActivity", response.toString());

                                    // Application code
                                    try {
                                        String email = object.getString("email");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    // 01/31/1980 format
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "name,email");
                    request.setParameters(parameters);
                    request.executeAsync();


                }

                @Override
                public void onCancel() {
                    // App code
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                }


            });
        }
        catch (Exception ex) {
            Toast.makeText(Login1Activity.this, "Login failed.", Toast.LENGTH_LONG).show();
        }

    }

    private void CheckFacebookLogin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null) {
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.v("LoginActivity", response.toString());

                            // Application code
                            try {
                                String email = object.getString("email");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // 01/31/1980 format
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "name,email");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

    @OnClick({ R.id.flogin, R.id.butNext})   //add glogin here if added
    public void onViewClicked(View view) {
        switch (view.getId()) {
          //  case R.id.glogin:
          //      break;
            case R.id.flogin:
                break;
            case R.id.butNext:
                if (awesomeValidation.validate()) {
                    final ProgressDialog pd = new ProgressDialog(Login1Activity.this);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage("Loading.........");
                    pd.setCancelable(false);
                    pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
                    pd.setIndeterminate(true);
                    pd.show();

                    RequestQueue requestQueue = Volley.newRequestQueue(this);
                    String url = Base_URL + "/userlogin";
                    final JSONObject GH = new JSONObject();
                    try {
                        GH.put("username", txtusername.getText());
                        GH.put("pass", tPass.getText());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    GsonRequest<UserDetails> getRequest = new GsonRequest<UserDetails>(Request.Method.POST, url, UserDetails.class, null, new Response.Listener<UserDetails>() {
                        @Override
                        public void onResponse(UserDetails response) {
                            pd.dismiss();
                            if (response != null) {
                                UserDetails dd = response;


                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("userid", dd.userid);
                                editor.putString("mobile", dd.mobile);
                                editor.putString("name", dd.name);
                                editor.putString("username", String.valueOf(txtusername.getText()));
                                editor.putString("pass", String.valueOf(tPass.getText()));

                                editor.apply();

                                session.createLoginSession(dd.userid,dd.name, dd.username,dd.mobile,dd.pass,dd.imguser);

                                Intent intent = new Intent(Login1Activity.this, UserMActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            } else {
                                Toast.makeText(Login1Activity.this, "Wrong credentials.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.dismiss();
                            Toast.makeText(Login1Activity.this, "Login failed.", Toast.LENGTH_LONG).show();
                            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                            editor.clear();

                            editor.apply();
                        }
                    }, GH);

                    getRequest.setShouldCache(false);
                    requestQueue.add(getRequest);
                }
                break;
        }
    }
}
