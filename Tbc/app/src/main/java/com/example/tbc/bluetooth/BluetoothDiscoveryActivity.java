package com.example.tbc.bluetooth;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
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
import android.os.Message;
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
import com.example.tbc.common.DataSave;
import com.example.tbc.menu.TbUtils;

public class BluetoothDiscoveryActivity extends ListActivity {

    public final static String TAG = "BluetoothDiscover";


    public final static int BLUETOOTH_MSG_NEW_LE_FOUND = 90;
    public final static int BLUETOOTH_MSG_BINDING = 109;
    public final static int BLUETOOTH_MSG_BIND_OK = 100;
    public final static int BLUETOOTH_MSG_BIND_FAIL= 101;

    public final static int BLUETOOTH_MSG_CONNECT_OK = 102;
    public final static int BLUETOOTH_MSG_CONNECT_FAIL= 103;

    private static final int REQUEST_ENABLE_BT = 1;

    private static final long SCAN_PERIOD = 10000;// 10秒后停止查找搜索.


    private Button scanButton;
    private boolean isScanning = false;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    private static LeDeviceListAdapter leDeviceListAdapter;

    volatile static BluetoothDevice bluetoothDevice;
    volatile static Context ctx;
    Menu menu;

    LeScanCb leScanCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = this;

        getActionBar().setTitle(R.string.title_devices);

        setContentView(R.layout.activity_main);

        if (!initBluetooth()) {
            Toast.makeText(this, "该设备不支持蓝牙", Toast.LENGTH_SHORT).show();
        }

        initScan();
    }

    void getMapedDevs(BluetoothAdapter bluetoothAdapter){

        Set<BluetoothDevice> bondedDevSet = bluetoothAdapter.getBondedDevices();

        if(bondedDevSet.size() > 0){
            for(BluetoothDevice it:bondedDevSet){
                leDeviceListAdapter.addDevice(it);
                Log.e(TAG,"it=" + it.getName());

            }
            leDeviceListAdapter.notifyDataSetChanged();
        }
    }

    boolean initBluetooth() {

        boolean ret = false;

        leDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(leDeviceListAdapter);

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

            if (bluetoothManager != null) {
                bluetoothAdapter = bluetoothManager.getAdapter();
                if (bluetoothAdapter != null) {
                    ret = true;

                    getMapedDevs(bluetoothAdapter);

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

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            Log.i(TAG,"BroadcastReceiver action=" + action);

            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent

                // Add the name and address to an array adapter to show in a ListView
                //mArrayAdapter.add(device.getName() + "n" + device.getAddress());
                Log.w(TAG,"found device " + device.getName() + "," + device.getAddress());

                leDeviceListAdapter.addDevice(device);
                leDeviceListAdapter.notifyDataSetChanged();

            }else if(action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)){
                try {

                    /*配对第二阶段*/

                    String pin = DataSave.getData(ctx, device.getName(), "1234");

                    Log.e(TAG,"receive BluetoothDevice.ACTION_PAIRING_REQUEST pin=" + pin);

                    //1.确认配对
                    ClsUtils.setPairingConfirmation(device.getClass(), device, true);
                    //2.终止有序广播
                    Log.i("order...", "isOrderedBroadcast:"+isOrderedBroadcast()+",isInitialStickyBroadcast:"+isInitialStickyBroadcast());
                    abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                    //3.调用setPin方法进行配对...
                    boolean ret = ClsUtils.setPin(device.getClass(), device, pin);
                    if(ret == true){
                        DataSave.setData(ctx,device.getName(),pin);
                        notidyMsg(BLUETOOTH_MSG_BIND_OK,device);
                    }else{
                        notidyMsg(BLUETOOTH_MSG_BIND_FAIL,device);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    notidyMsg(BLUETOOTH_MSG_BIND_FAIL,device);
                }
            }else if(action.equals( BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                doStopScan();
                Toast.makeText( ctx, "搜索完毕.", Toast.LENGTH_SHORT ).show();
            }
        }
    };

    class LeScanCb implements BluetoothAdapter.LeScanCallback {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            /*do not call ui at other thread*/

            notidyMsg(BLUETOOTH_MSG_NEW_LE_FOUND,device);
        }
    }

    void doStartScan(){
        Log.i(TAG,"doStartScan");
        isScanning = true;
        scanButton.setText( "停止扫描" );
        menu.findItem(R.id.menu_refresh).setActionView(
                R.layout.actionbar_indeterminate_progress);
        invalidateOptionsMenu();

        bluetoothAdapter.startDiscovery();
        bluetoothAdapter.startLeScan(leScanCb);
    }

    void doStopScan(){
        Log.i(TAG,"doStopScan");
        bluetoothAdapter.cancelDiscovery();
        bluetoothAdapter.stopLeScan(leScanCb  );
        isScanning = false;
        scanButton.setText( "开始扫描" );
        menu.findItem(R.id.menu_refresh).setActionView(null);
        invalidateOptionsMenu();
    }

    void initScan() {

        leScanCb = new LeScanCb();

        scanButton = (Button) findViewById( R.id.scanBt );

        scanButton.setText( "开始扫描" );

        scanButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (bluetoothAdapter != null) {
                    if (isScanning) {
                        doStopScan();
                    } else {
                        doStartScan();
                    }
                } else {
                    Toast.makeText( ctx, "该设备不支持蓝牙.", Toast.LENGTH_SHORT ).show();
                    Log.e( TAG, "该设备不支持蓝牙." );
                }
            }
        } );

        // Register the BroadcastReceiver

        registerReceiver( broadcastReceiver, new IntentFilter( BluetoothDevice.ACTION_FOUND ) );
        registerReceiver( broadcastReceiver, new IntentFilter( BluetoothDevice.ACTION_PAIRING_REQUEST ) );
        registerReceiver( broadcastReceiver, new IntentFilter( BluetoothAdapter.ACTION_DISCOVERY_FINISHED ) );

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;

        getMenuInflater().inflate(R.menu.main1, menu);

        if (!isScanning) {
            //menu.findItem(R.id.menu_stop).setVisible(false);
            //menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else{
            //menu.findItem(R.id.menu_stop).setVisible(true);
            //menu.findItem(R.id.menu_scan).setVisible(false);
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

        if (bluetoothAdapter!=null && bluetoothAdapter.isEnabled()) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        BluetoothCom.stopRead();
        BluetoothCom.disConnect();
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        Log.i(TAG,"==position==" + position);

        final BluetoothDevice device = leDeviceListAdapter.getDevice(position);

        if (device == null){
            return;
        }

        if (isScanning) {
            doStopScan();
        }

        bondStart(device);
    }



    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> leDevices;
        private LayoutInflater inflator;

        public LeDeviceListAdapter() {
            super();
            leDevices = new ArrayList<BluetoothDevice>();
            inflator = BluetoothDiscoveryActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!leDevices.contains(device)) {
                leDevices.add(device);
            }else{
                //Log.e(TAG,"not add " + device.getName() + "," + device.getAddress());
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

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }


    protected static Handler handler = new Handler(){

        ProgressDialog dialog;

        private void hint(String text){
            if(dialog != null){
                dialog.dismiss();
            }

            if(text != null) {
                dialog = android.app.ProgressDialog.show( ctx, "蓝牙连接", text ,true,true);
            }
        }

        private int connectFailNum = 0;

        public void handleMessage(Message message){
            switch (message.what) {
                case BLUETOOTH_MSG_NEW_LE_FOUND:
                    BluetoothDevice device = (BluetoothDevice)message.obj;
                    leDeviceListAdapter.addDevice(device);
                    leDeviceListAdapter.notifyDataSetChanged();
                    break;
                case BLUETOOTH_MSG_BINDING:
                    hint("正在检测蓝牙配对 ...");
                    Log.e(TAG,"正在检测蓝牙配对");
                    break;

                case BLUETOOTH_MSG_BIND_OK:
                    hint("蓝牙配对成功,正在连接 ...");
                    Log.e(TAG,"配对成功 ");
                    connectFailNum = 0;
                    doConnect((BluetoothDevice)(message.obj));
                    break;

                case BLUETOOTH_MSG_BIND_FAIL:
                    hint("蓝牙配对失败");
                    Log.e(TAG,"配对失败 ");
                    break;

                case BLUETOOTH_MSG_CONNECT_OK:
                    Log.e(TAG,"蓝牙连接成功 ");
                    hint(null);

                    BluetoothCom.startRead();

                    final Intent intent = new Intent(ctx, BluetoothSchemeActivity.class);
                    intent.putExtra(BluetoothSchemeActivity.BLUETOOTH_DEVICE, bluetoothDevice);
                    ctx.startActivity(intent);

                    break;
                case BLUETOOTH_MSG_CONNECT_FAIL:
                    Log.e(TAG,"连接失败,继续连接　次数 " + (connectFailNum + 1));
                    //hint("蓝牙连接失败");
                    if(connectFailNum < 3){
                        connectFailNum ++;

                        try {
                            BluetoothCom.disConnect();
                            Thread.sleep( 400 );
                            doConnect( (BluetoothDevice)(message.obj) );
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }else{
                        hint("蓝牙连接失败");
                    }
                    break;
                default:
                    Log.e(TAG,"未知到消息.");
                    break;
            }
        }
    };

    private static void notidyMsg(int msgWhat,Object obj){

        Message msg = handler.obtainMessage();

        msg.what = msgWhat;
        msg.obj = obj;

        handler.sendMessage(msg);

        Log.i(TAG,"notidy msg " + msgWhat);
    }

    static void doConnect(BluetoothDevice dev){

        bluetoothDevice = dev;

        Log.i(TAG,"doConnect ...");

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                if(BluetoothCom.connect(bluetoothDevice)) {
                    notidyMsg(BLUETOOTH_MSG_CONNECT_OK,bluetoothDevice);
                }else{
                    notidyMsg(BLUETOOTH_MSG_CONNECT_FAIL,bluetoothDevice);
                }
            }
        });

        thread.start();
    }

    static void bondStart(BluetoothDevice dev){

        bluetoothDevice = dev;

        Log.i(TAG,"bondStart ...");

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                doBondStart(bluetoothDevice);

                Log.i(TAG,"bondStart 1 ...");
            }
        });

        thread.start();
    }


    private static void doBondStart(BluetoothDevice device){

        BluetoothCom.disConnect();

        if (device.getBondState() == BluetoothDevice.BOND_NONE) {

            Log.i(TAG, "开始配对:"+"["+device.getName()+"]");

            try {
                //通过工具类ClsUtils,调用createBond方法
                ClsUtils.createBond(device.getClass(), device);
                notidyMsg(BLUETOOTH_MSG_BINDING,device);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                notidyMsg(BLUETOOTH_MSG_BIND_FAIL,device);
            }
        }else if(device.getBondState() == BluetoothDevice.BOND_BONDED){
            Log.i(TAG,"之前已经配对.配对成功");
            notidyMsg(BLUETOOTH_MSG_BIND_OK,device);
        }else if(device.getBondState() == BluetoothDevice.BOND_BONDING){
            Log.i(TAG,"正在配对.");
            notidyMsg(BLUETOOTH_MSG_BINDING,device);
        }

        return ;
    }

    private boolean doBond(BluetoothDevice device,String pin){

        boolean ret = false;

        if(device.getBondState() == BluetoothDevice.BOND_NONE){
            try{
                if(pin == null) {
                    pin = DataSave.getData(this, device.getName(), "1234");
                }

                if(autoBond(device.getClass(), device,pin )){
                    ret = true;
                    DataSave.setData(this,device.getName(),pin);
                }else {
                    Log.e(TAG,"配对失败.");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG,"配对异常.");
            }
        }
        else if(device.getBondState() == BluetoothDevice.BOND_BONDED){
            Log.i(TAG,"之前已经配对.");
            ret = true;
        }else if(device.getBondState() == BluetoothDevice.BOND_BONDING){
            Log.i(TAG,"正在配对.");
        }

        return ret;
    }

    //自动配对设置Pin值
    static public boolean autoBond(Class btClass,BluetoothDevice device,String strPin) throws Exception {
        Method autoBondMethod = btClass.getMethod("setPin",new Class[]{byte[].class});
        Boolean result = (Boolean)autoBondMethod.invoke(device,new Object[]{strPin.getBytes()});
        return result;
    }

    //开始配对
    static public boolean createBond(Class btClass,BluetoothDevice device) throws Exception {
        Method createBondMethod = btClass.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }

    synchronized boolean attemptAutoPair(String address) {

        /*device.getBondState();

        if (!mBondState.hasAutoPairingFailed(address) &&
                !mBondState.isAutoPairingBlacklisted(address)) {
            mBondState.attempt(address);
            setPin(address, BluetoothDevice.convertPinToBytes("0000"));
            return true;
        }*/
        return false;
    }
}
