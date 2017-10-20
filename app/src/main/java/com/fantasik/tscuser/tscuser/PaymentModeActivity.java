package com.fantasik.tscuser.tscuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.fantasik.tscuser.tscuser.Util.GsonRequest;
import com.fantasik.tscuser.tscuser.Util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fantasik.tscuser.tscuser.Util.Utils.Base_URL;
import static com.fantasik.tscuser.tscuser.Util.Utils.MY_PREFS_NAME;

public class PaymentModeActivity extends AppCompatActivity {

    @BindView(R.id.imgDebit)
    ImageView imgDebit;
    @BindView(R.id.crdDebit)
    CardView crdDebit;
    @BindView(R.id.imgCash)
    ImageView imgCash;
    @BindView(R.id.crdCash)
    CardView crdCash;
    @BindView(R.id.butNext)
    Button butNext;

    String userid = "";
    boolean isDebit, isCash;

    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_mode);
        setTitle("Select Payment Mode");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        isDebit = true;
        isCash = false;
        imgCash.setImageResource(android.R.color.transparent);
        imgDebit.setImageResource(R.drawable.ok);
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


    @OnClick({R.id.crdDebit, R.id.crdCash, R.id.butNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.crdDebit:
                if(!isDebit)
                {
                    imgDebit.setImageResource(R.drawable.ok);
                    imgCash.setImageResource(android.R.color.transparent);
                    isCash = false;
                    isDebit = true;
                }
                break;
            case R.id.crdCash:
                if(!isCash)
                {
                    imgCash.setImageResource(R.drawable.ok);
                    imgDebit.setImageResource(android.R.color.transparent);
                    isCash = true;
                    isDebit = false;
                }
                break;
            case R.id.butNext:

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

                editor.putString("isdebit", String.valueOf(isDebit));
                editor.putString("iscash", String.valueOf(isCash));

                editor.apply();

                final ProgressDialog pd = new ProgressDialog(PaymentModeActivity.this);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage("Loading.........");
                pd.setCancelable(false);
                pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
                pd.setIndeterminate(true);
                pd.show();



                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String url = Base_URL + "/RegisterUser";

               final SharedPreferences editorread = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

                final JSONObject GH =new JSONObject();
                try {
                    GH.put("fname",editorread.getString("fname", ""));
                    GH.put("lname",editorread.getString("lname", ""));
                    GH.put("email",editorread.getString("email", ""));
                    GH.put("phone",editorread.getString("phone", ""));
                    GH.put("pass",editorread.getString("passw", ""));

                    GH.put("filenamewithext", "");
                    GH.put("profilebytes", editorread.getString("profileimage", ""));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                GsonRequest<String> getRequest = new GsonRequest<String>(Request.Method.POST, url,String.class, null, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        pd.dismiss();
                        if(response != null && !response.substring(1,response.length()- 1).equals("-1")) {
                            userid = response;

                            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                            editor.putString("userid", userid);
                            editor.putString("name", editorread.getString("fname", "") + editorread.getString("lname", ""));
                            editor.putString("username", editorread.getString("email", ""));
                            editor.putString("pass", editorread.getString("passw", ""));

                            editor.apply();

                            session.createLoginSession(userid,editorread.getString("fname", "") + editorread.getString("lname", ""), editorread.getString("email", ""),editorread.getString("phone", ""),editorread.getString("passw", ""),editorread.getString("profileimage", ""));

                            Intent intent = new Intent(PaymentModeActivity.this, UserMActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        pd.dismiss();
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                // Now you can use any deserializer to make sense of data
                                JSONObject obj = new JSONObject(res);
                            } catch (UnsupportedEncodingException e1) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace();
                            } catch (JSONException e2) {
                                // returned data is not JSONObject?
                                e2.printStackTrace();
                            }
                        }

                    }
                }, GH);

                getRequest.setShouldCache(false);
                requestQueue.add(getRequest);


                break;
        }
    }

}
