package com.fantasik.tscuser.tscuser;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fantasik.tscuser.tscuser.Util.Utils.MY_PREFS_NAME;

public class ChangeCashTypeActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Change payment Mode");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_change_cash_type);
        ButterKnife.bind(this);


    }      @Override
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
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("mode", "Debit");
                editor.apply();

                break;
            case R.id.crdCash:
                SharedPreferences.Editor editor2 = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor2.putString("mode", "Cash");
                editor2.apply();
                break;
            case R.id.butNext:
                finish();
                break;
        }
    }
}
