
package com.example.tbc.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.tbc.R;
import com.example.tbc.bluetooth.BluetoothSchemeActivity;
import com.example.tbc.protocal.TB3531;
import com.example.tbc.protocal.TbInputSource;
import com.example.tbc.protocal.TbInputSourceManager;
import com.example.tbc.protocal.TbScheme;
import com.example.tbc.setting.system.SettingSystemActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class SettingHotBakActivity extends ListActivity implements View.OnClickListener{

    public final static String TAG = "HotBak";

    Button set;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_hotbak);
    }


    @Override
    protected void onResume() {

        TB3531.setDataHandler(handler);
        set = (Button)findViewById(R.id.submit);

        inputSourceManager = new TbInputSourceManager( TB3531.tbInputSources );

        initList();
        getItemList();

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        TB3531.setDataHandler(null);
        super.onDestroy();
    }

    private void initList(){

        if(tbItemListAdapter == null) {

            tbItemListAdapter = new TbItemListAdapter();

            this.setListAdapter( tbItemListAdapter );

            this.getListView().setOnItemClickListener(
                    new AdapterView.OnItemClickListener(){

                        public void onItemClick(AdapterView<?> arg0, View arg1,int position,long arg3) {

                            tbItemListAdapter.notifyDataSetChanged();
                            doClickItem(position);
                        }

                        private void doClickItem(int position){

                        }
                    });
        }
    }

    void getItemList(){

        Log.i(TAG,"start getItemList");

        TB3531.writeAsync(TB3531.EVENT_GROUP_ID_HOTBAK,
                TB3531.EVENT_HOTBAK_ID_GET);
    }

    protected Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case TB3531.TB_BLUETOOTH_DATA_EVENT:
                    processData( (Byte[]) message.obj );
                    break;
                default:
                    Log.e( TAG, "Not support msg received" );
                    break;
            }
        }
    };

    public void processData(Byte[] data) {

        TB3531.logBuffer( data,data.length ,"read event data:");

        if(data[4] == (byte)TB3531.EVENT_GROUP_ID_HOTBAK
                && data[5] == (byte)TB3531.EVENT_HOTBAK_ID_GET){

            String ret;

            int err = data[TB3531.EVENT_MSG_OFFSET];
            if(err == 0){
                ret = "成功";
                parseHotBakList(data);
            }else{
                ret = "失败，未知错误,err=" + err;
            }

            Toast.makeText( this,"获取热备份：" + ret ,Toast.LENGTH_SHORT).show();

        }else if(data[4] == (byte)TB3531.EVENT_GROUP_ID_HOTBAK
                && data[5] == (byte)TB3531.EVENT_HOTBAK_ID_SET){

            String ret;

            int err = data[TB3531.EVENT_MSG_OFFSET];
            if(err == 0){
                ret = "成功";
                /*当前提交保存成功，放入显示列表*/
                TbItem item = getAndCheckCurHotBak();
                if(item != null) {
                    tbItemListAdapter.addItem( item );
                    tbItemListAdapter.notifyDataSetChanged();
                }

            }else{
                ret = "失败，未知错误,err=" + err;
            }

            Toast.makeText( this,"设置热备份：" + ret ,Toast.LENGTH_SHORT).show();
        }
    }

    void parseHotBakList(Byte[] data){
        int num = data[TB3531.EVENT_MSG_OFFSET + 1] & 0xff;

        Log.i(TAG,"获取热备份列表数量"+ num);

        if(num > 0){

            for(int i = 0; i < num;i++){

                int off = TB3531.EVENT_MSG_OFFSET + 2 + 4 * i;

                int boardId = data[off] & 0xff;
                int srcId = data[off + 1] & 0xff;
                int boardIdto = data[off + 2] & 0xff;
                int srcIdto = data[off + 3] & 0xff;

                TbItem item = new TbItem(boardId,srcId,boardIdto,srcIdto);

                tbItemListAdapter.addItem( item );
                tbItemListAdapter.notifyDataSetChanged();
            }
        }
    }

    private class CurButtonOnClick implements DialogInterface.OnClickListener{

        private int index;

        public CurButtonOnClick(int index){
            this.index = index;
        }

        public int getIndex(){
            return index;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {

            Log.e(TAG,"index=" + index);

            if (which >= 0){
                index = which;
            }else {

                if (which == DialogInterface.BUTTON_POSITIVE) {

                    TbInputSource[] noUseInputs = inputSourceManager.getNoUsedInputSource();

                    cur = noUseInputs[index];

                    inputSourceManager.setUsed(noUseInputs[index].boardId,noUseInputs[index].sourceId);

                    doCheckBakSource();

                }
            }
        }
    }

    private class BakButtonOnClick implements DialogInterface.OnClickListener{

        private int index;

        public BakButtonOnClick(int index){
            this.index = index;
        }

        public int getIndex(){
            return index;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {

            Log.e(TAG,"index=" + index);

            if (which >= 0){
                index = which;
            }else {

                if (which == DialogInterface.BUTTON_POSITIVE) {
                    bak = (inputSourceManager.getNoUsedInputSource())[index];
                    doSendHotBak();
                }
            }
        }
    }

    TbInputSourceManager inputSourceManager;

    TbInputSource cur;
    TbInputSource bak;

    CurButtonOnClick curButtonOnClick = new CurButtonOnClick(0);
    BakButtonOnClick bakButtonOnClick = new BakButtonOnClick(0);

    void doCheckCurSource(){

        String[] items = inputSourceManager.getNoUsedInputSourceNames();

        new AlertDialog.Builder(this)
                .setTitle("请选择在播源")
                .setSingleChoiceItems(items, 0, curButtonOnClick)
                .setPositiveButton( "确定", curButtonOnClick)
                .setNegativeButton("取消", null).show();

    }

    void doCheckBakSource(){

        Toast.makeText( this,"在播源选择好了，请继续选择备份源",Toast.LENGTH_SHORT ).show();

        String[] items = inputSourceManager.getNoUsedInputSourceNames();

        new AlertDialog.Builder(this)
                .setTitle("在播源选择好了,请选择备份源")
                .setSingleChoiceItems(items, 0, bakButtonOnClick)
                .setPositiveButton( "确定", bakButtonOnClick)
                .setNegativeButton("取消", null).show();
    }

    void doSendHotBak(){

        if(cur != null && bak != null){

            Byte[] param = new Byte[5];

            param[0] = 1;
            param[1] = (byte)(cur.boardId & 0xff);
            param[2] = (byte)(cur.sourceId & 0xff);
            param[3] = (byte)(bak.boardId & 0xff);
            param[4] = (byte)(bak.sourceId & 0xff);

            TB3531.writeAsync(TB3531.EVENT_GROUP_ID_HOTBAK,
                    TB3531.EVENT_HOTBAK_ID_SET,param);
        }
    }

    TbItem getAndCheckCurHotBak(){

        TbItem item = null;

        if(cur != null && bak != null){
            item = new TbItem(cur.boardId,cur.sourceId,bak.boardId,bak.sourceId);
        }

        return item;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                Log.e(TAG,"add hotbak");
                doCheckCurSource();
                break;

            default:
                break;
        }
    }


    TbItemListAdapter tbItemListAdapter;

    private class TbItemListAdapter extends BaseAdapter {
        private ArrayList<TbItem> itemList;
        private LayoutInflater inflator;

        public TbItemListAdapter() {
            super();
            itemList = new ArrayList<TbItem>();
            inflator = SettingHotBakActivity.this.getLayoutInflater();
        }

        public void addItem(TbItem timer) {
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

            TbItem item = itemList.get(i);


            String itemInfo = "在播:" + inputSourceManager.getInputSource(item.boardId,item.srcId).getItemName()
                    + "      备份:" + inputSourceManager.getInputSource(item.boardIdto,item.srcIdto).getItemName();


            viewHolder.info.setText( itemInfo );

            return view;
        }
    }

    static class ViewHolder {
        TextView info;
    }

    private class TbItem{

        int boardId;
        int srcId;

        int boardIdto;
        int srcIdto;

        public TbItem(int boardId,int srcId,int boardIdto,int srcIdto){
            this.boardId = boardId;
            this.srcId = srcId;
            this.boardIdto = boardIdto;
            this.srcIdto = srcIdto;
        }
    }
}
