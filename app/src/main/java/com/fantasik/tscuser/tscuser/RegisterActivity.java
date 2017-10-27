package com.fantasik.tscuser.tscuser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fantasik.tscuser.tscuser.Util.SPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fantasik.tscuser.tscuser.Util.Utils.MY_PREFS_NAME;

public class RegisterActivity extends AppCompatActivity {


    @BindView(R.id.fregister)
    LoginButton fregister;
    @BindView(R.id.tFname)
    EditText tFname;
    @BindView(R.id.tLname)
    EditText tLname;
    @BindView(R.id.tEmail)
    EditText tEmail;
    @BindView(R.id.tphone)
    EditText tphone;
    @BindView(R.id.tPass)
    EditText tPass;
    @BindView(R.id.butNext)
    Button butNext;
    private AwesomeValidation awesomeValidation;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

        ButterKnife.bind(this);
        SPreferences.ClearPreferences(this);
        setTitle("Register");
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(this, R.id.tPass, "[a-zA-Z0-9_-]+", R.string.passerror);
        awesomeValidation.addValidation(this, R.id.tFname, "[a-zA-Z-]+", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.tEmail, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(this, R.id.tphone, Patterns.PHONE, R.string.mobileerror);

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("profileimage", null);
        editor.apply();

        callbackManager = CallbackManager.Factory.create();
        fregister.setReadPermissions(Arrays.asList(
                "public_profile", "email"));


        //if facebook login has no email, prompt user to enter email in the fields and put image in the cache to get used in profilepictureactivity
            //if facebook login has email, procced to profile picture
        fregister.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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
                                    if(object.getString("email") != null)
                                    {

                                        String name = object.getString("name");

                                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

                                        editor.putString("fname", name.split(" ")[0]);
                                        editor.putString("lname", name.split(" ")[1]);
                                        editor.putString("email", object.getString("email"));
                                        editor.putString("phone", "");
                                        editor.putString("passw", "facebook");


                                        JSONObject data = response.getJSONObject();
                                        if (data.has("picture")) {
                                            String profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                            URL url1;
                                            try {
                                                url1 = new URL(profilePicUrl);
                                                try {
                                                    Bitmap profilePic= BitmapFactory.decodeStream(url1.openConnection().getInputStream());

                                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                    profilePic.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                                    byte[] byteArray = byteArrayOutputStream .toByteArray();

                                                    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                                                    editor.putString("profileimage", encoded);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                        editor.apply();

                                        Intent intent = new Intent(RegisterActivity.this, ProfilePictureActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                    }
                                    else {
                                        String name = object.getString("name");
                                        tFname.setText(name.split(" ")[0]);
                                        tLname.setText(name.split(" ")[1]);

                                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

                                        JSONObject data = response.getJSONObject();
                                        if (data.has("picture")) {
                                            String profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                            URL url1;
                                            try {
                                                url1 = new URL(profilePicUrl);
                                                try {
                                                    Bitmap profilePic= BitmapFactory.decodeStream(url1.openConnection().getInputStream());

                                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                    profilePic.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                                                    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);


                                                    editor.putString("profileimage", encoded);

                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                        editor.apply();

                                        Toast.makeText(RegisterActivity.this, "Please Enter Email.", Toast.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                // 01/31/1980 format
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture.type(large)");
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
            Toast.makeText(RegisterActivity.this, "Login failed.", Toast.LENGTH_LONG).show();
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


    @OnClick({  R.id.butNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {


            case R.id.butNext:

                if(isValidText()) {

                    GoToPictureActivity();
                }
                 break;
        }
    }

    private void GoToPictureActivity() {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        editor.putString("fname", tFname.getText().toString());
        editor.putString("lname", tLname.getText().toString());
        editor.putString("email", tEmail.getText().toString());
        editor.putString("phone", tphone.getText().toString());
        editor.putString("passw", tPass.getText().toString());
        editor.apply();

        Intent intent = new Intent(RegisterActivity.this, ProfilePictureActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private boolean isValidText() {

        return awesomeValidation.validate();
    }
}
