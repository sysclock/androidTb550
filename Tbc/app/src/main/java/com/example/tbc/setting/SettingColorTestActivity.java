
package com.example.tbc.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.support.v4.app.DialogFragment;
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
import com.example.tbc.protocal.TbOutputSource;
import com.example.tbc.protocal.TbScheme;

import java.util.ArrayList;
import java.util.Calendar;

public class SettingColorTestActivity extends ListActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener {

    public final static String TAG = "colorTest";

    Button set;
    TextView callRet;
    SeekBar r,g,b;

    TbOutputSource[] outputSources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_colortest);

        doInitOutputSource();
    }

    void doInitOutputSource(){

        outputSources = new TbOutputSource[8];

        for(int i = 0; i < 8 ;i ++){
            outputSources[i] = new TbOutputSource((i < 4?0:1),i % 4);
        }
    }

    void doColorChange(){

        int red = r.getProgress();
        int green = g.getProgress();
        int blue = b.getProgress();

        int color = 0xff000000 |
                ((red & 0xff) << 16) |
                ((green & 0xff) << 8) |
                ((blue & 0xff));

        //Log.i(TAG,"red =" + red + "green=" + green  + ",blue=" + blue);

        TextView vc = (TextView)findViewById( R.id.colorValue );

        String strColor = ("0x" + Integer.toHexString( color )).toUpperCase();
        vc.setText(strColor);
        vc.setBackground( new ColorDrawable(color) );

        //Log.i(TAG,"strColor =" + strColor );
    }

    @Override
    protected void onResume() {

        TB3531.setDataHandler(handler);

        initSubmit();

        r = (SeekBar)findViewById( R.id.seekBarRed );
        g = (SeekBar)findViewById( R.id.seekBarGreen );
        b = (SeekBar)findViewById( R.id.seekBarBlue );

        r.setOnSeekBarChangeListener( this );
        g.setOnSeekBarChangeListener( this );
        b.setOnSeekBarChangeListener( this );

        doColorChange();

        initList();

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


    public void processData(Byte[] data) {

        TB3531.logBuffer( data,data.length ,"read event data:");

        if(data[4] == (byte)TB3531.EVENT_GROUP_ID_TEST
                && data[5] == (byte)TB3531.EVENT_TEST_COLOR){

            String ret;

            int err = data[TB3531.EVENT_MSG_OFFSET];
            if(err == 0){
                ret = "成功";
            }else{
                ret = "失败，未知错误,err=" + err;
            }

            Toast.makeText( this,"提交图卡测试成功：" + ret ,Toast.LENGTH_SHORT).show();

        }
    }


    void initSubmit(){
        set = (Button)findViewById(R.id.submit);
        callRet = (TextView)findViewById(R.id.resp);

        set.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                Log.e(TAG,"do color test.");
                doSubmit();
                break;

            default:
                break;
        }
    }

    void doSubmit(){

        String[] items = TbOutputSource.getNameList( outputSources );

        new AlertDialog.Builder(this)
                .setTitle("请选择测试输出口")
                .setSingleChoiceItems(items, 0, buttonOnClick)
                .setPositiveButton( "确定", buttonOnClick)
                .setNegativeButton("取消", null).show();
    }

    void doSendColorTest(TbOutputSource src){

        try {

            int boardId = src.boardId;
            int portId = src.portId;

           // if(boardId >= 0 && boardId < TB3531.BOARD_NUM
            //        && portId >= 0 && portId < TB3531.PORT_NUM)
            {
                Byte[] param = new Byte[5];

                int red = r.getProgress();
                int green = g.getProgress();
                int blue = b.getProgress();

                //// TODO: 16-12-4 输入０，１，输出2,3

                param[0] = (byte)((boardId  + 2) & 0xff);
                param[1] = (byte)(portId & 0xff);

                param[2] = (byte)(red & 0xff);
                param[3] = (byte)(green & 0xff);
                param[4] = (byte)(blue & 0xff);

                //发送图卡测试的数据
                TB3531.writeAsync(TB3531.EVENT_GROUP_ID_TEST,
                        TB3531.EVENT_TEST_COLOR,param);

                Log.i(TAG,"提交图卡测试.");

            }
            /*
            else
            {
                Toast.makeText( this,"参数错误，提交失败",Toast.LENGTH_SHORT ).show();
            }
                */
        }catch(Exception e){
            Toast.makeText(this,"参数错误，提交失败",Toast.LENGTH_SHORT ).show();
        }

    }

    ButtonOnClick buttonOnClick = new ButtonOnClick(0);


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

                    TbOutputSource src = outputSources[index];

                    doSendColorTest(src);
                }
            }
        }
    }

    TbItemListAdapter tbItemListAdapter;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        doColorChange();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    private class TbItemListAdapter extends BaseAdapter {
        private ArrayList<TbItem> itemList;
        private LayoutInflater inflator;

        public TbItemListAdapter() {
            super();
            itemList = new ArrayList<TbItem>();
            inflator = SettingColorTestActivity.this.getLayoutInflater();
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

            String itemInfo = "板子" + item.boardId + ",输出口" + item.srcId
                    + "---: 优先级别" + item.weight ;

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
