package com.example.asset_detection;

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
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


public class Device_Status_Monitoring extends Service
{
    //BluetoothAdapter mBluetoothAdapter;
    public static final String TAG = "TAG";
    boolean status=false;
    boolean flag=false;
    String device_mac_address;
    BluetoothAdapter mBluetoothAdapter;
    BroadcastReceiver broadcastReceiver;
    BluetoothDevice mBTDevice;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public Device_Status_Monitoring()
    {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
     //   Log.d(TAG, "control inside the service");
        final Thread thread=new Thread(new Runnable() {
            @Override
            public void run()
            {
                Looper.prepare();

                Log.d(TAG,"In the loop");
                status = search_status();
                Log.d(TAG,"The status is "+status);
                return;
            }
        });
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public boolean search_status()
    {
        Log.d(TAG, "control inside the bluetooth service");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // myLabel.setText("No bluetooth adapter available");
        }

        if (!mBluetoothAdapter.isEnabled()) {

            mBluetoothAdapter.enable();
        }

     //   Log.d(TAG, "control inside the tag devices");
        mBluetoothAdapter.startDiscovery();
        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Log.d(TAG, "control inside the broadcast receiver of blutooth");
                String action=intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                    mBTDevices.add(device);

                    if(mBTDevices.size()>0) {
                        for (int i = 0; i < mBTDevices.size(); i++) {
                            BluetoothDevice device1 = mBTDevices.get(i);
                            Log.d(TAG, "control inside the loop");
                                Log.d(TAG, device1.getName());
                                Log.d(TAG,device1.getAddress());
                                device_mac_address=device1.getAddress().toString();
                                String s="ESP32";
                                if(device1.getName().equals(s))
                                {
                                        status=true;
                                    Log.d(TAG,"control inside the intent service");
                                    Intent done = new Intent("com.local.receiver");
                                    Bundle b = new Bundle();
                                    b.putBoolean("status", status);
                                    b.putString("MAC_ADDRESS",device_mac_address);
                                    done.putExtras(b);
                                    sendBroadcast(done);
                                    Log.d(TAG, "destroying the thread");
                                      //  return true;
                                }
                        }
                    }
                }
            }
        };
        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, discoverDevicesIntent);
        return status;
    }


}
