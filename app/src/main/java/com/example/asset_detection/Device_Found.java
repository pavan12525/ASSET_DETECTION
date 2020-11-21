package com.example.asset_detection;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Device_Found  extends AppCompatActivity
{
    protected void onCreate(Bundle savedInstanceState)
    {
        TextView t1,t2;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_found);
        t1=(TextView)findViewById(R.id.textView2);
        t2=(TextView)findViewById(R.id.textView3);
        String latitude=getIntent().getStringExtra("latitude");
        latitude.trim();
        String longitude=getIntent().getStringExtra("longitude");
        longitude.trim();
        t1.setText(latitude);
        t2.setText(longitude);
        Toast.makeText(Device_Found.this, "The coordinates are"+latitude+" "+longitude, Toast.LENGTH_LONG).show();
    }

}
