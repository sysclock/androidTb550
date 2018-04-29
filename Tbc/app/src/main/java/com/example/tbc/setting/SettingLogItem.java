
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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.tbc.R;
import com.example.tbc.bluetooth.BluetoothSchemeActivity;
import com.example.tbc.protocal.TB3531;
import com.example.tbc.protocal.TbInputSource;
import com.example.tbc.protocal.TbInputSourceManager;
import com.example.tbc.protocal.TbScheme;

import java.util.ArrayList;
import java.util.Calendar;

public class SettingLogItem extends ListActivity implements View.OnClickListener{

    public final static String TAG = "SettingLogItem";

    private Switch switcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_log_item);

        switcher = (Switch)findViewById( R.id.switch2 );
        switcher.setChecked( true );

        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                Log.e(TAG,"onCheckedChanged " + isChecked);

                byte type;

                if(logOpen != isChecked) {

                    if (isChecked) {
                        type = (byte) 1;
                    } else {
                        type = (byte) 0;
                    }

                    TB3531.writeAsync( TB3531.EVENT_GROUP_ID_LOG,
                            TB3531.EVENT_LOG_SET, type );
                }
            }
        });
    }

    @Override
    protected void onResume() {

        TB3531.setDataHandler(handler);

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

    void getItemList(){

        Log.i(TAG,"start getItemList");


        TB3531.writeAsync( TB3531.EVENT_GROUP_ID_LOG,
                TB3531.EVENT_LOG_GET_ITEM,(byte)0,(byte)255);

    }

    boolean logOpen = false;

    void setStatus(boolean flag){
        switcher.setChecked( flag );
    }
    public void processData(Byte[] data) {

        TB3531.logBuffer( data,data.length ,"read event data:");

        if(data[4] == (byte)TB3531.EVENT_GROUP_ID_LOG
                && data[5] == (byte)TB3531.EVENT_LOG_GET_ITEM){

            String ret;

            int err = data[TB3531.EVENT_MSG_OFFSET];
            if(err == 0){
                ret = "成功";
                logOpen = true;
                setStatus( true );

                try {
                    parseItemList( data );
                    Log.i(TAG,"日志获取成功");
                    //Toast.makeText( this, "日志获取成功", Toast.LENGTH_SHORT ).show();
                }catch (Exception e){
                    Log.i(TAG,"日志获取成功,但解析异常");
                    //Toast.makeText( this, "日志获取成功,但解析异常", Toast.LENGTH_SHORT ).show();
                }
            }else if(err == 1){
                Toast.makeText( this,"日志处于关闭状态",Toast.LENGTH_SHORT).show();
                logOpen = false;
                setStatus( false );
                Log.i(TAG,"日志处于关闭状态");
            }else{
                ret = "失败，未知错误,err=" + err;

                Log.i(TAG,"日志获取" + ret);
                logOpen = false;
                setStatus( false );
                Toast.makeText( this,"日志获取:" + ret,Toast.LENGTH_SHORT).show();
            }

        }else if(data[4] == (byte)TB3531.EVENT_GROUP_ID_LOG
                && data[5] == (byte)TB3531.EVENT_LOG_SET){

            boolean flag = switcher.isChecked();

            int err = data[TB3531.EVENT_MSG_OFFSET];

            if(err == 0) {
                logOpen = flag;

                if(flag && tbItemListAdapter.isEmpty()){
                    getItemList();
                }
            }

            Toast.makeText( this, (flag?"打开日志":"关闭日志") + (err== 0?"成功":"失败"), Toast.LENGTH_SHORT ).show();

        }else if(data[4] == (byte)TB3531.EVENT_GROUP_ID_LOG
                && data[5] == (byte)TB3531.EVENT_LOG_CLAER){

            int err = data[TB3531.EVENT_MSG_OFFSET];

            Toast.makeText( this, "清除日志" + (err== 0?"成功":"失败"), Toast.LENGTH_SHORT ).show();

            if(err == 0) {
                tbItemListAdapter.clear();
                tbItemListAdapter.notifyDataSetChanged();
            }
        }
    }

    int logNum = -1;

    void parseItemList(Byte[] data){

        logNum = data[TB3531.EVENT_MSG_OFFSET + 1] & 0xff;
        int start = data[TB3531.EVENT_MSG_OFFSET + 2] & 0xff;
        int count = data[TB3531.EVENT_MSG_OFFSET + 3] & 0xff;

        Log.i(TAG,"获取LOG =["+ logNum + "," + start + "," + count + "]");

        if(count > 0){

            int off = TB3531.EVENT_MSG_OFFSET + 4;

            for(int i = 0; i < count;i++){

                int len = data[off] & 0xff;

                Log.e(TAG,"log[" + i + "] len=" + len);

                String item = TB3531.getStringFromBytes( data,off + 1,len );

                off += len + 1;

                tbItemListAdapter.addItem( item );
                tbItemListAdapter.notifyDataSetChanged();
            }

            if(start + count < logNum){
                TB3531.writeAsync( TB3531.EVENT_GROUP_ID_LOG,
                    TB3531.EVENT_LOG_GET_ITEM,(byte)(start + count),(byte)255);
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clearLog:
                Log.e(TAG,"clear log.");
                TB3531.writeAsync( TB3531.EVENT_GROUP_ID_LOG,
                        TB3531.EVENT_LOG_CLAER);
                break;

            default:
                break;
        }
    }




    TbItemListAdapter tbItemListAdapter;


    private class TbItemListAdapter extends BaseAdapter {
        private ArrayList<String> itemList;
        private LayoutInflater inflator;

        public TbItemListAdapter() {
            super();
            itemList = new ArrayList<>();
            inflator = SettingLogItem.this.getLayoutInflater();
        }

        public void addItem(String item) {
             itemList.add(item);
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

            String item = itemList.get(i);

            viewHolder.info.setText( "记录" + (i + 1) + ":" + item );

            return view;
        }
    }

    static class ViewHolder {
        TextView info;
    }

}
