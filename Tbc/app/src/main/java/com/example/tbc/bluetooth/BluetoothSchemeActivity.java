package com.example.tbc.bluetooth;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tbc.R;
import com.example.tbc.menu.TbUtils;
import com.example.tbc.protocal.TB3531;
import com.example.tbc.protocal.TbInputSource;
import com.example.tbc.protocal.TbScheme;
import com.example.tbc.protocal.TbSchemeManager;
import com.example.tbc.setting.SettingHomeActivity;

import java.util.ArrayList;

public class BluetoothSchemeActivity extends ListActivity{

    public final static String TAG = "BtDeviceActivity";
    public final static String BLUETOOTH_DEVICE = "BLUETOOTH_DEVICE";

    public final static int BLUETOOTH_MSG_BIND_OK = 100;
    public final static int BLUETOOTH_MSG_BIND_FAIL= 101;

    public final static int BLUETOOTH_MSG_CONNECT_OK = 102;
    public final static int BLUETOOTH_MSG_CONNECT_FAIL= 103;

    static Context ctx;

    static BluetoothDevice bluetoothDevice;



    void goToOtherPage(Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(BluetoothSchemeActivity.this, cls);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();

        bluetoothDevice = intent.getParcelableExtra(BLUETOOTH_DEVICE);

        getActionBar().setTitle(bluetoothDevice.getName());

        setContentView(R.layout.activity_page_scheme);

        ctx = this;

        TB3531.tbSchemeManager = null;
        TB3531.tbInputSources = null;

        initList();

        try {
            doGetInfoStart();
        }catch (Exception e){
            e.printStackTrace();
        }

        doInputPinDialog();
    }

    void setStatusString(String info){
       // TextView status  = (TextView) findViewById(R.id.status);
       // status.setText( info );
    }

    @Override
    protected void onResume(){

        TB3531.setDataHandler(handler);

        super.onResume();
    }

    @Override
    protected void onDestroy(){

        TB3531.setDataHandler(null);
        BluetoothCom.disConnect();
        super.onDestroy();
    }

    void doGetInfoStart(){
        //todo
       TB3531.writeAsync(TB3531.EVENT_GROUP_ID_SCHEME,
                TB3531.EVENT_SCHEME_ID_GET_SRC_LIS );
    }

    void doGetInfo(){
        Byte[] ret = TB3531.write(TB3531.EVENT_GROUP_ID_SCHEME,
                TB3531.EVENT_SCHEME_ID_GET_SRC_LIS );
        if(ret != null){
            setStatusString("获取方案中 ...");

            TB3531.tbInputSources = TB3531.parseSrcList(ret);

            ret = TB3531.write(TB3531.EVENT_GROUP_ID_SCHEME,
                    TB3531.EVENT_SCHEME_ID_GET_SCHEME_DES );
            if(ret != null){
                TB3531.tbSchemeManager = TB3531.parseSchemeDes(ret);

                if(TB3531.tbSchemeManager != null
                        && TB3531.tbSchemeManager.ids != null
                        && TB3531.tbSchemeManager.ids.length > 0){
                    for(int i = 0; i < TB3531.tbSchemeManager.ids.length ; i ++){
                        ret = TB3531.write(TB3531.EVENT_GROUP_ID_SCHEME,
                                TB3531.EVENT_SCHEME_ID_GET_SCHEME,(byte)TB3531.tbSchemeManager.ids[i]);
                        if(ret != null){
                            Log.i(TAG,"Get scheme " + TB3531.tbSchemeManager.ids[i] + " ok.");

                            boolean br = TB3531.parseSchemeInfo(ret,TB3531.tbSchemeManager,TB3531.tbSchemeManager.ids[i]);
                            if(!br){
                                Log.i(TAG,"parse scheme info fail.");
                            }else{
                                Log.i(TAG,"parse scheme info ok. id =" + ret[9]);

                                TbScheme scheme = TB3531.tbSchemeManager.getTbSchemeBySubscript(i);
                                if(scheme != null) {
                                    Log.i(TAG,"scheme = " + scheme.name);
                                    tbItemListAdapter.addItem( scheme );
                                    tbItemListAdapter.notifyDataSetChanged();
                                }else{
                                    Log.e(TAG,"getTbSchemeBySubscript fail.");
                                }
                            }
                        }else{
                            Log.i(TAG,"Get scheme " + TB3531.tbSchemeManager.ids[i] + " fail.");
                        }
                    }

                    setStatusString("获取方案完毕");
                }else{
                    Log.i(TAG,"parseSchemeDes fail.");
                }
            }else{
                Log.i(TAG,"get SchemeDes fail.");
            }
        }else{
            Log.i(TAG,"get SchemeDes fail.");
        }
    }


    private void initList(){

        if(tbItemListAdapter == null) {

            tbItemListAdapter = new TbItemListAdapter();

            this.setListAdapter(tbItemListAdapter);

            this.getListView().setOnItemClickListener(
                    new AdapterView.OnItemClickListener(){

                public void onItemClick(AdapterView<?> arg0, View arg1,int position,long arg3) {

                    tbItemListAdapter.notifyDataSetChanged();
                    doClickItem(position);
                }

                private void doClickItem(int position){

                    final TbScheme scheme = (TbScheme)tbItemListAdapter.getItem( position );

                    final AlertDialog builder = new AlertDialog.Builder( ctx )
                            .setTitle( "方案选择" )
                            .setIcon( android.R.drawable.ic_dialog_info )
                            .setMessage( "真的要切换的方案'" + scheme.name + "'吗？" )
                            .setPositiveButton( "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    hintStatus( "正在切换到方案" + scheme.name);

                                    TB3531.writeAsync( TB3531.EVENT_GROUP_ID_SCHEME,
                                            TB3531.EVENT_SCHEME_ID_SWITCH_SCHEME,
                                            (byte) scheme.id);
                                }
                            } )
                            .setNegativeButton( "取消", null )
                            .show();
                }
            });
        }
    }

    private int schemeIndex = 0;

    private void hintStatus(String status){
        Log.i(TAG,"hintStatus:" + status);
        setStatusString(status);
    }

    protected Handler handler = new Handler(){
        public void handleMessage(Message message){
            switch (message.what) {
                case TB3531.TB_BLUETOOTH_DATA_EVENT:
                    processData((Byte[])message.obj);
                    break;
                default:
                    Log.e(TAG,"Not support msg received");
                    break;
            }
        }

        private void processGetSrcList(Byte[] data){

            TB3531.tbInputSources = TB3531.parseSrcList(data);

            if(TB3531.tbInputSources != null) {

                TB3531.writeAsync( TB3531.EVENT_GROUP_ID_SCHEME,
                        TB3531.EVENT_SCHEME_ID_GET_SCHEME_DES );

                hintStatus( "正在获取方案描述 ..." );
            }else{
                hintStatus( "获取源信息失败." );
            }
        }

        private void processGetSchemeDes(Byte[] data){

            TB3531.tbSchemeManager = TB3531.parseSchemeDes(data);

            if(TB3531.tbSchemeManager != null
                    && TB3531.tbSchemeManager.ids != null
                    && TB3531.tbSchemeManager.ids.length > 0){

                schemeIndex = 0;

                TB3531.writeAsync(TB3531.EVENT_GROUP_ID_SCHEME,
                        TB3531.EVENT_SCHEME_ID_GET_SCHEME,(byte)TB3531.tbSchemeManager.ids[schemeIndex]);

                hintStatus( "开始获取方案 " + TB3531.tbSchemeManager.ids[schemeIndex]);

            }else{
                hintStatus( "正在获取方案描述失败" );
            }
        }

        private void processGetScheme(Byte[] data){

            int curId = TB3531.tbSchemeManager.ids[schemeIndex];

            hintStatus("获取方案 " + curId + " ok.");

            boolean br = TB3531.parseSchemeInfo(data,TB3531.tbSchemeManager,curId);
            if(!br){
                hintStatus( "解析方案 " + curId + " fail.");
            }else{
                hintStatus( "解析方案 " + curId + " OK.");

                TbScheme scheme = TB3531.tbSchemeManager.getTbSchemeBySubscript(schemeIndex);
                if(scheme != null) {
                    tbItemListAdapter.addItem( scheme );
                    tbItemListAdapter.notifyDataSetChanged();
                }else{
                    Log.e(TAG,"添加方案到视图失败.");
                }
            }

            schemeIndex ++;

            if(schemeIndex < TB3531.tbSchemeManager.ids.length){
                hintStatus( "开始获取获取方案 " + TB3531.tbSchemeManager.ids[schemeIndex]);
                TB3531.writeAsync(TB3531.EVENT_GROUP_ID_SCHEME,
                        TB3531.EVENT_SCHEME_ID_GET_SCHEME,
                        (byte)(TB3531.tbSchemeManager.ids[schemeIndex] & 0xff));

            }else{
                hintStatus("所有方案获取完毕,选择可以切换方案.");
            }
        }

        private void processSchemeSwitch(Byte[] data){

            String str;
            if(data[TB3531.EVENT_MSG_OFFSET] == 0){

                str = "方案切换成功";
            }else{
                str = "方案切换失败";
            }

            hintStatus(str);

            Toast.makeText( BluetoothSchemeActivity.this,str,Toast.LENGTH_SHORT ).show();
        }

        private void processSystemCheckPin(Byte[] data){

            if(data[TB3531.EVENT_MSG_OFFSET] == 0){

                hintStatus("密码校验正确");

                goToOtherPage(SettingHomeActivity.class);

            }else{
                hintStatus("密码输入不正确");
            }
        }

        public void processData(Byte[] data) {

            TB3531.logBuffer( data,data.length ,"read event data:");

            if(data[4] == (byte)TB3531.EVENT_GROUP_ID_SCHEME
                    && data[5] == (byte)TB3531.EVENT_SCHEME_ID_GET_SRC_LIS){
                processGetSrcList(data);

            }else if(data[4] == (byte)TB3531.EVENT_GROUP_ID_SCHEME
                    && data[5] == (byte)TB3531.EVENT_SCHEME_ID_GET_SCHEME_DES){
                processGetSchemeDes(data);

            }else if(data[4] == (byte)TB3531.EVENT_GROUP_ID_SCHEME
                    && data[5] == (byte)TB3531.EVENT_SCHEME_ID_GET_SCHEME){
                processGetScheme(data);

            }else if(data[4] == (byte)TB3531.EVENT_GROUP_ID_SCHEME
                    && data[5] == (byte)TB3531.EVENT_SCHEME_ID_SWITCH_SCHEME){
                processSchemeSwitch(data);
            }else if(data[4] == (byte)TB3531.EVENT_GROUP_ID_SYSTEM
                    && data[5] == (byte)TB3531.EVENT_SYSTEM_ID_CHECK_PIN){
                processSystemCheckPin(data);
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.schememenu, menu);

        return true;
    }


    private void doInputPinDialog(){

        final EditText editText = new EditText(this);

        editText.setMaxEms( 6 );
        editText.setInputType( InputType.TYPE_CLASS_NUMBER  );
        editText.setTransformationMethod( PasswordTransformationMethod.getInstance());

        goToOtherPage(SettingHomeActivity.class);

        /*no use password for test.*/
        /*
        if(false)
        {
            goToOtherPage(SettingHomeActivity.class);
        }
        else
        {
            final AlertDialog builder = new AlertDialog.Builder( this )
                    .setTitle( "请输入设置密码" )
                    .setIcon( android.R.drawable.ic_dialog_info )
                    .setView( editText )
                    .setPositiveButton( "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            hintStatus( "正在校验密码..." );

                            String ps = editText.getText().toString();

                            byte[] psbase = ps.getBytes();
                            Byte[] psbyte = new Byte[10];

                            psbyte[0] = 0;
                            psbyte[1] = (byte)8;

                            for(int i = 0; i < 8; i ++){
                                if(i < psbase.length){
                                    psbyte[i  + 2] = psbase[i];
                                }else{
                                    psbyte[i  + 2] = (byte)0;
                                }
                            }

                            TB3531.writeAsync( TB3531.EVENT_GROUP_ID_SYSTEM,
                                    TB3531.EVENT_SYSTEM_ID_CHECK_PIN,psbyte);
                        }
                    } )
                    .setNegativeButton( "取消", null )
                    .show();
        }
        */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                doInputPinDialog();
                break;
            case R.id.about:
                new AlertDialog.Builder(this)
                        .setMessage("蓝牙控制终端　version " + TbUtils.getVersionName(this))
                        .setCancelable(false)
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();

                break;
        }
        return true;
    }


    TbItemListAdapter tbItemListAdapter;


    private class TbItemListAdapter extends BaseAdapter {
        private ArrayList<TbScheme> itemList;
        private LayoutInflater inflator;

        public TbItemListAdapter() {
            super();
            itemList = new ArrayList<TbScheme>();
            inflator = BluetoothSchemeActivity.this.getLayoutInflater();
        }

        public void addItem(TbScheme timer) {
            if (!itemList.contains(timer)) {
                itemList.add(timer);
            }
        }

        public void clear() {
            itemList.clear();
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int i) {
            return itemList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;

            if (view == null) {
                view = inflator.inflate(R.layout.list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.info = (TextView) view.findViewById(R.id.info);

                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            TbScheme item = itemList.get(i);

            String itemInfo = "方案:" + item.name + "       编号:" + item.id;

            viewHolder.info.setText( itemInfo );

            return view;
        }
    }

    static class ViewHolder {
        TextView info;
    }
}