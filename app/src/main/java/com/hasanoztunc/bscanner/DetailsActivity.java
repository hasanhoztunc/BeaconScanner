package com.hasanoztunc.bscanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

    TextView txUUID;
    TextView txName;
    TextView txTx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        txUUID=(TextView)findViewById(R.id.tvUUIDExtra);
        txName=(TextView)findViewById(R.id.tvNameExtra);
        txTx=(TextView)findViewById(R.id.tvTxExtra);
        Intent intent=getIntent();
        txUUID.setText(intent.getStringExtra("uuid"));
        txName.setText(intent.getStringExtra("name"));
        txTx.setText(intent.getStringExtra("tx")+" meters away");
    }
}
