package com.example.asset_detection;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Set;

public class Console extends AppCompatActivity
{
    public static final String TAG = "TAG";
    EditText uuid;
    String device_mac_address;
    Button report;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth fAuth;
    IntentFilter filter;
    String device_name_1;
    String latitude="",longitude="";
    BroadcastReceiver broadcastReceiver;
    Intent intent,intent1;
    boolean status=false;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);
        uuid=findViewById(R.id.Email);
        report=findViewById(R.id.button);
        mDatabase=FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();
        mDatabaseReference = mDatabase.getReference();
        // mLoginBtn=findViewById(R.id.createText);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String user_id=fAuth.getCurrentUser().getUid();
               String mac_address = uuid.getText().toString().trim();
                User_details user = new User_details(mac_address,false,user_id,"","");
                mDatabaseReference = mDatabase.getReference().child(user_id);
                mDatabaseReference.setValue(user);
            }
        });

        broadcastReceiver=new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Log.d(TAG,"Broadcast received by the main thread.");//check point1

                status= intent.getExtras().getBoolean("status");
                device_mac_address=intent.getExtras().getString("MAC_ADDRESS");
                Log.d(TAG,"The device haha mac address is"+device_mac_address);
                Toast.makeText(Console.this,"The device has been detected "+status,Toast.LENGTH_LONG).show();
                 if(status==true)
                {
                    Log.d(TAG,"code inside the gps service");
                    intent1=new Intent(Console.this, GPS_SERVICE.class);
                    startService(intent1);
                    broadcastReceiver=new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent)
                        {
                            Bundle b = intent.getExtras();
                            double latitude_1 = b.getDouble("latitude");
                            double longitude_1= b.getDouble("longitude");
                            latitude=Double.toString(latitude_1);
                            longitude=Double.toString(longitude_1);
                            Toast.makeText(Console.this,"The latitudes and longitudes of device are "+latitude+longitude,
                                    Toast.LENGTH_LONG).show();//checkpoint2
                            update_firebase_location(latitude,longitude,device_mac_address);
                        }
                    };
                    filter = new IntentFilter();
                    filter.addAction("com.local.receiver1");
                    registerReceiver(broadcastReceiver,filter);
                    if(longitude!=""&&latitude!="")
                        stopService(intent1);
                }
            }

        };
        filter = new IntentFilter();
        filter.addAction("com.local.receiver");
        registerReceiver(broadcastReceiver,filter);
    }
    public void update_firebase_location(final String latitude, final String longitude, String device_mac_address)
    {
      //  mDatabaseReference=FirebaseDatabase.getInstance().getReference();
        Log.d(TAG,"checking the MAC address of lost devices");
        Log.d(TAG,"the device MAC address is "+device_mac_address);
     //  Query query = mDatabaseReference.orderByChild("mac_address").equalTo(device_mac_address);
        final DatabaseReference mDatabaseReference=FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.orderByChild("mac_address").equalTo(device_mac_address).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot datas : snapshot.getChildren()) {
                        String key=datas.getKey();
                        mDatabaseReference.child(key).child("latitude").setValue(latitude);
                        mDatabaseReference.child(key).child("longitude").setValue(longitude);
                        mDatabaseReference.child(key).child("status_device").setValue(true);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        FirebaseDatabase mDatabase=FirebaseDatabase.getInstance();;
  //      mDatabase.getReference("").setValue("");
     //   FirebaseDatabase.getInstance().getReference().child("")
      //  mDatabase=FirebaseDatabase.getInstance();
       // mDatabase.child();
      /*  mDatabaseReference.orderByChild("mac_address").equalTo(device_mac_address);
        HashMap<String,Object> map= new HashMap<>();
        map.put("latitude",latitude);
        map.put("longitude",longitude);
        mDatabaseReference.updateChildren(map);*/
    }
    public void onStart()
    {
        super.onStart();
       Toast.makeText(Console.this, "Acivity started.", Toast.LENGTH_SHORT).show();
       mDatabaseReference=FirebaseDatabase.getInstance().getReference();
        boolean status=true;
        fAuth = FirebaseAuth.getInstance();
        final String userid=fAuth.getCurrentUser().getUid();
        Log.d(TAG, "user id "+userid);
        Query query = mDatabaseReference.orderByChild("status_device").equalTo(status);


       // code to check whether users device is reported as missing

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Toast.makeText(Console.this, "Entry Found", Toast.LENGTH_LONG).show();
                    for (DataSnapshot dataSnapshots :snapshot.getChildren())
                    {
                        User_details user = dataSnapshots.getValue(User_details.class);
                        if(user.getUser_id().equals((userid)))
                        {
                            startActivity(new Intent(getApplicationContext(),Device_Found.class).
                                putExtra("latitude",user.getLatitude()).
                                putExtra("longitude",user.getLongitude()));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        intent=new Intent(Console.this, Device_Status_Monitoring.class);
        startService(intent);

        // Check if user is signed in (non-null) and update UI accordingly.
    }
    protected  void onDestroy()
    {
        super.onDestroy();
        if(broadcastReceiver!=null)
        {
            unregisterReceiver(broadcastReceiver);
        }
        stopService(intent);

    }
}
