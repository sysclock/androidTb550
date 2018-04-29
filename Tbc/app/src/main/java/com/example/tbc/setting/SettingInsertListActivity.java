
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
import android.widget.SeekBar;
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

public class SettingInsertListActivity extends ListActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener {

    public final static String TAG = "insetPlay";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_insertlist);

    }

    SeekBar seekBar;

    @Override
    protected void onResume() {

        TB3531.setDataHandler(handler);

        inputSourceManager = new TbInputSourceManager( TB3531.tbInputSources );

        seekBar = (SeekBar)findViewById( R.id.seekBar );

        seekBar.setOnSeekBarChangeListener( this );

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


        TB3531.writeAsync( TB3531.EVENT_GROUP_ID_INSERT_PLAY,
                TB3531.EVENT_INSERT_ID_GET);

    }

    public void processData(Byte[] data) {

        TB3531.logBuffer( data,data.length ,"read event data:");

        if(data[4] == (byte)TB3531.EVENT_GROUP_ID_INSERT_PLAY
                && data[5] == (byte)TB3531.EVENT_INSERT_ID_GET){

            String ret;

            int err = data[TB3531.EVENT_MSG_OFFSET];
            if(err == 0){
                ret = "成功";
                parseItemList(data);
            }else{
                ret = "失败，未知错误,err=" + err;
            }

            Toast.makeText( this,"获取优先插播：" + ret ,Toast.LENGTH_SHORT).show();

        }else if(data[4] == (byte)TB3531.EVENT_GROUP_ID_INSERT_PLAY
                && data[5] == (byte)TB3531.EVENT_INSERT_ID_SET){

            String ret;

            int err = data[TB3531.EVENT_MSG_OFFSET];
            if(err == 0){
                ret = "成功";
                if(lastInsert != null) {
                    TbItem item = new TbItem( lastInsert.boardId,
                            lastInsert.sourceId, seekBar.getProgress() );

                    tbItemListAdapter.addItem( item );
                    tbItemListAdapter.notifyDataSetChanged();
                }
            }else{
                ret = "失败，未知错误,err=" + err;
            }

            Toast.makeText( this,"优先插播：" + ret ,Toast.LENGTH_SHORT).show();
        }
    }

    void parseItemList(Byte[] data){

        int num = data[TB3531.EVENT_MSG_OFFSET + 1] & 0xff;

        Log.i(TAG,"获取热备份列表数量"+ num);

        if(num > 0){

            for(int i = 0; i < num;i++){

                int off = TB3531.EVENT_MSG_OFFSET + 2 + 3 * i;

                int boardId = data[off] & 0xff;
                int srcId = data[off + 1] & 0xff;
                int weight = data[off + 2] & 0xff;

                TbItem item = new TbItem(boardId,srcId,weight);

                tbItemListAdapter.addItem( item );
                tbItemListAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                Log.e(TAG,"add insert play");
                doAddHotInsertPlay();
                break;

            default:
                break;
        }
    }


    void doAddHotInsertPlay(){

        String[] items = inputSourceManager.getNoUsedInputSourceNames();

        new AlertDialog.Builder(this)
                .setTitle("请选择在播源")
                .setSingleChoiceItems(items, 0, buttonOnClick)
                .setPositiveButton( "确定", buttonOnClick)
                .setNegativeButton("取消", null).show();
    }

    TbInputSource lastInsert;

    void doSendHotInsert(TbInputSource src){

        lastInsert = src;

        Byte[] param = new Byte[4];

        int weight = seekBar.getProgress();

        param[0] = 1;
        param[1] = (byte)(src.boardId & 0xff);
        param[2] = (byte)(src.sourceId & 0xff);
        param[3] = (byte)(weight & 0xff);

        TB3531.writeAsync(TB3531.EVENT_GROUP_ID_INSERT_PLAY,
                TB3531.EVENT_INSERT_ID_SET,param);
    }

    TbInputSourceManager inputSourceManager;

    ButtonOnClick buttonOnClick = new ButtonOnClick(0);

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        TextView t = (TextView)findViewById( R.id.seekBarText );
        t.setText( "" +  progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    private class ButtonOnClick implements DialogInterface.OnClickListener{

        private int index;

        public ButtonOnClick(int index){
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

                    TbInputSource src = noUseInputs[index];

                    doSendHotInsert(src);
                }
            }
        }
    }


    TbItemListAdapter tbItemListAdapter;


    private class TbItemListAdapter extends BaseAdapter {
        private ArrayList<TbItem> itemList;
        private LayoutInflater inflator;

        public TbItemListAdapter() {
            super();
            itemList = new ArrayList<TbItem>();
            inflator = SettingInsertListActivity.this.getLayoutInflater();
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

            TbInputSource source = inputSourceManager.getInputSource(item.boardId,item.srcId);

            String itemInfo = "输入源:" + (source != null?source.getItemName():"")
                    + "     优先级:" + item.weight ;


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
        int weight;

        public TbItem(int boardId,int srcId,int weight){
            this.boardId = boardId;
            this.srcId = srcId;
            this.weight = weight;
        }
    }
}
