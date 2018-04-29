package com.example.tbc.bluetooth;

import java.util.ArrayList;

import android.bluetooth.BluetoothManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tbc.R;
import com.example.tbc.menu.TbUtils;

public class BluetoothScanActivity extends ListActivity {
    private String TAG = "BluetoothScanActivity";

    private static final int REQUEST_ENABLE_BT = 1;

    private static final long SCAN_PERIOD = 10000;// 10秒后停止查找搜索.


    private Button scanButton;
    private boolean isScanning = true;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    LeDeviceListAdapter leDeviceListAdapter;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setTitle(R.string.title_devices);

        setContentView(R.layout.activity_main);

        if (initBluetooth()) {
            initScan();
        } else {
            Toast.makeText(this, "不支持蓝牙", Toast.LENGTH_SHORT).show();
        }
    }

    boolean initBluetooth() {

        boolean ret = false;

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

            if (bluetoothManager != null) {
                bluetoothAdapter = bluetoothManager.getAdapter();
                if (bluetoothAdapter != null) {
                    ret = true;
                } else {
                    Log.i(TAG, "bluetoothManager.getAdapter() fail.");
                }
            } else {
                Log.i(TAG, "getSystemService BLUETOOTH_SERVICE fail.");
            }
        } else {
            Log.i(TAG, "hasSystemFeature fail.");
        }

        return ret;
    }

    void initScan() {

        scanButton = (Button) findViewById(R.id.scanBt);

        scanButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isScanning) {
                    scanButton.setText("开始扫描");
                } else {
                    scanButton.setText("停止扫描");
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main1, menu);
        if (!isScanning) {
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_us:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("蓝牙控制终端　version " + TbUtils.getVersionName(this))
                        .setCancelable(false)
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (!bluetoothAdapter.isEnabled()) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Initializes list view adapter.
        leDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(leDeviceListAdapter);
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        leDeviceListAdapter.clear();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        System.out.println("==position==" + position);

        final BluetoothDevice device = leDeviceListAdapter.getDevice(position);
        if (device == null){
            return;
        }

        final Intent intent = new Intent(this, BluetoothSchemeActivity.class);

        intent.putExtra(BluetoothSchemeActivity.BLUETOOTH_DEVICE, device);

        //intent.putExtra(BluetoothDeviceActivity.BLUETOOTH_DEVICE_NAME, device.getName());
        //intent.putExtra(BluetoothDeviceActivity.BLUETOOTH_DEVICE_ADDRESS, device.getAddress());

        if (isScanning) {
            bluetoothAdapter.stopLeScan(mLeScanCallback);
            isScanning = false;
        }

        startActivity(intent);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScanning = false;
                    bluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            isScanning = true;

            bluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            isScanning = false;
            bluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> leDevices;
        private LayoutInflater inflator;

        public LeDeviceListAdapter() {
            super();
            leDevices = new ArrayList<BluetoothDevice>();
            inflator = BluetoothScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!leDevices.contains(device)) {
                leDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return leDevices.get(position);
        }

        public void clear() {
            leDevices.clear();
        }

        @Override
        public int getCount() {
            return leDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return leDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = inflator.inflate(R.layout.list, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = leDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    leDeviceListAdapter.addDevice(device);
                    leDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }


    /*
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        final BluetoothDevice device = leDeviceListAdapter.getDevice(position);
        if (device == null) return;

        final Intent intent = new Intent(this, BluetoothControlAcitvity.class);
        intent.putExtra(BluetoothControlAcitvity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(BluetoothControlAcitvity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        if (isScanning)
        {
            mble.stopLeScan();
            isScanning = false;
        }

        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //打开蓝牙结果
        if(resultCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED)
        {
            finish();
            return ;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }






    Runnable cancelScan = new Runnable()
    {
        @Override
        public void run()
        {
            mble.stopLeScan();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mble.startLeScan();
            mHandler.postDelayed(cancelScan,SCAN_PERIOD);
            invalidateOptionsMenu();
        }
    };
    */
}
