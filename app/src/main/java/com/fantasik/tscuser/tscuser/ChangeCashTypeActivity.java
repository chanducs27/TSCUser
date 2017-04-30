package com.fantasik.tscuser.tscuser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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


    }

    @OnClick({R.id.crdDebit, R.id.crdCash, R.id.butNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.crdDebit:
                break;
            case R.id.crdCash:
                break;
            case R.id.butNext:
                break;
        }
    }
}
