package com.example.tbc.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
//import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ScrollView;

import com.example.tbc.protocal.TB3531;
import com.hmsoft.bluetooth.le.BluetoothLeService;


public class BluetoothDataService extends Service {
    private static final String TAG = "BluetoothDataService";
    public static Intent intent = new Intent("com.example.tbc.bluetooth.BluetoothDataService");

    public static String mDeviceName;
    public static String mDeviceAddress;

    public static BluetoothDevice bluetoothDevice;
    private static BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;

    //
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "--------->onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "--------->onStartCommand: ");

        start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "--------->onDestroy: ");
        super.onDestroy();
    }

    public static void setDeviceInfo(BluetoothDevice bt,String dn,String da){
        bluetoothDevice = bt;
        mDeviceName = dn;
        mDeviceAddress = da;
    }

    private void start(){
        Log.e(TAG,"start");



        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);

        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    public static int write(byte[] data){

        int len = mBluetoothLeService.WriteValue(data);
        TB3531.logTime("write end" );
        return len;
    }



    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }else {

                Log.e(TAG, "mBluetoothLeService is okay");
                // Automatically connects to the device upon successful start-up initialization.
                //mBluetoothLeService.connect(mDeviceAddress);
                new Handler().postDelayed(new Runnable(){

                    public void run() {
                        //execute the task

                        mBluetoothLeService.connect(mDeviceAddress);
                    }
                }, 100);//1500
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.e(TAG, "Only gatt, just wait");

                new Handler().postDelayed(new Runnable(){

                    public void run() {
                        //execute the task

                        final Intent it = new Intent(BluetoothDataService.this, BluetoothSchemeActivity.class);
                        it.putExtra(BluetoothSchemeActivity.BLUETOOTH_DEVICE, bluetoothDevice);
                        BluetoothDataService.this.startActivity(it);
                    }
                }, 1500);



            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                //invalidateOptionsMenu();
                //btnSend.setEnabled(false);
                //clearUI();
            }else if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action))
            {
                mConnected = true;
                //mDataField.setText("");
                //ShowDialog();
               // btnSend.setEnabled(true);
                Log.e(TAG, "In what we need");
                //invalidateOptionsMenu();
            }else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.e(TAG, "RECV DATA");
                //String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                if (data != null) {
                    Log.i(TAG,"read len=" + data.length);
                    TB3531.pushData( data, data.length);
                }
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }



}