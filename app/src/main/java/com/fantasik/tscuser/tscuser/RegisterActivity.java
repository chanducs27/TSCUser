package com.fantasik.tscuser.tscuser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.fantasik.tscuser.tscuser.Util.SPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fantasik.tscuser.tscuser.Util.Utils.MY_PREFS_NAME;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.gregister)
    ImageButton gregister;
    @BindView(R.id.fregister)
    ImageButton fregister;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        SPreferences.ClearPreferences(this);
        setTitle("Register");
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @OnClick({R.id.gregister, R.id.fregister, R.id.butNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.gregister:
                break;
            case R.id.fregister:
                break;
            case R.id.butNext:

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

                editor.putString("fname", tFname.getText().toString());
                editor.putString("lname", tLname.getText().toString());
                editor.putString("email", tEmail.getText().toString());
                editor.putString("phone", tphone.getText().toString());
                editor.putString("passw", tPass.getText().toString());
                editor.apply();

                Intent intent = new Intent(RegisterActivity.this, PaymentModeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;
        }
    }
}
