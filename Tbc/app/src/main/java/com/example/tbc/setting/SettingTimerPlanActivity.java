
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
import com.example.tbc.protocal.TbScheme;
import com.example.tbc.protocal.TbTimer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SettingTimerPlanActivity extends ListActivity implements View.OnClickListener{

    public final static String TAG = "SettingSYSPlan";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_timer_plan);

    }


    @Override
    protected void onResume() {

        initList();
        getTimerList();

        TB3531.setDataHandler(handler);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        TB3531.setDataHandler(null);
        super.onDestroy();
    }

    private void initList(){

        if(tbTimerListAdapter == null) {

            tbTimerListAdapter = new TbTimerListAdapter();

            this.setListAdapter( tbTimerListAdapter );

            this.getListView().setOnItemClickListener(
                    new AdapterView.OnItemClickListener(){

                        public void onItemClick(AdapterView<?> arg0, View arg1,int position,long arg3) {

                            tbTimerListAdapter.notifyDataSetChanged();
                            doClickItem(position);
                        }

                        private void doClickItem(int position){
                            /*Context ctx = NoteItemListActivity.this;
                            Intent intent = new Intent(ctx,NoteItemAddModifyActivity.class);

                            Note item = notes.get(position);
                            intent.putExtra("name", item.name);
                            intent.putExtra("type", item.type);
                            intent.putExtra("info", item.info);
                            startActivity(intent);
                            */
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

        if(data[4] == (byte)TB3531.EVENT_GROUP_ID_TIMER
                && data[5] == (byte)TB3531.EVENT_TIMER_ID_GET_TIMER){

            String ret;

            int err = data[TB3531.EVENT_MSG_OFFSET];
            if(err == 0){
                ret = "成功";
                parseTimerList(data);
            }else{
                ret = "失败，未知错误,err=" + err;
            }

            Toast.makeText( this,"获取定时器列表：" + ret ,Toast.LENGTH_SHORT).show();

        }else if(data[4] == (byte)TB3531.EVENT_GROUP_ID_TIMER
                && data[5] == (byte)TB3531.EVENT_TIMER_ID_SET_TIMER){

            String ret;

            int err = data[TB3531.EVENT_MSG_OFFSET];
            if(err == 0){
                ret = "成功";

                if(lastTimer != null){
                    tbTimerListAdapter.addItem( lastTimer);
                    tbTimerListAdapter.notifyDataSetChanged();
                }
            }else{
                ret = "失败，未知错误,err=" + err;
            }

            Toast.makeText( this,"添加定时切换方案：" + ret ,Toast.LENGTH_SHORT).show();
        }
    }

    void parseTimerList(Byte[] data){

        TbTimer[] timerArr = TB3531.parseTimer(data);

        Log.i(TAG,"parseTimerList num=" + (timerArr!= null?timerArr.length:0));

        if(timerArr != null && timerArr.length > 0){
            for(int i = 0; i < timerArr.length;i++){
                tbTimerListAdapter.addItem( timerArr[i] );
                tbTimerListAdapter.notifyDataSetChanged();
            }
        }
    }


    void getTimerList(){
        TB3531.writeAsync(TB3531.EVENT_GROUP_ID_TIMER,
                TB3531.EVENT_TIMER_ID_GET_TIMER);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chooseScheme:
                Log.i(TAG,"chooseScheme");
                doChooseScheme();
                break;
            case R.id.chooseDate:
                doChooseDate();
                break;
            case R.id.chooseTime:
                doChooseTime();
                break;
            case R.id.submit:
                doSubmit();
                break;
            default:
                break;
        }
    }

    TbTimer lastTimer;

    void doSubmit(){

        String name = (((EditText)findViewById( R.id.name )).getText()).toString();

        if(name.length() == 0){
            Toast.makeText( this,"请填写定时器名称",Toast.LENGTH_LONG ).show();
            return;
        }

        if(chooseTbScheme == null){
            Toast.makeText( this,"请选择定时方案",Toast.LENGTH_LONG ).show();
            return;
        }

        if(!timeChoosed){
            Toast.makeText( this,"请选择定时时间",Toast.LENGTH_LONG ).show();
            return;
        }

        if(!dateChoosed){
            Toast.makeText( this,"请选择定时日期",Toast.LENGTH_LONG ).show();
            return;
        }

        final Calendar c = Calendar.getInstance();


        Calendar setTimer = new GregorianCalendar(chooseYear,chooseMonth,chooseDay,
                chooseHour,chooseMinute,0);

        if(setTimer.getTime().getTime() - c.getTime().getTime() <= 0){
            Toast.makeText( this,"日期时间小于当前日期时间",Toast.LENGTH_LONG ).show();
            return;
        }

        lastTimer = new TbTimer( name,0,chooseTbScheme.id,
                new GregorianCalendar(chooseYear,chooseMonth,chooseDay,
                        chooseHour,chooseMinute,0));

        Byte[] params = new Byte[25];

        byte[] nameByte = name.getBytes();

        params[0] = 1; /*num*/

        for(int i = 1; i < 16;i ++){
            if(i < name.length()){
                params[i] = nameByte[i];
            }else{
                params[i] = 0;
            }
        }

        params[16] = 0;
        params[17] = (byte)(chooseTbScheme.id);

        params[18] = (byte)(chooseYear >> 8);
        params[19] = (byte)(chooseYear & 0xff);

        params[20] = (byte)(chooseMonth);
        params[21] = (byte)(chooseDay);
        params[22] = (byte)(chooseHour);
        params[23] = (byte)(chooseMinute);
        params[24] = (byte)(0);

        TB3531.writeAsync(TB3531.EVENT_GROUP_ID_TIMER,
                TB3531.EVENT_TIMER_ID_SET_TIMER,params);
    }

    void doChooseDate(){

        final Calendar c = Calendar.getInstance();

        int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH);
        int curDay= c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog( this,new DatePickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                chooseYear = year;
                chooseMonth = (monthOfYear);
                chooseDay = dayOfMonth;
                dateChoosed = true;

                TextView dateview = (TextView)findViewById( R.id.date );
                dateview.setText( "" +  chooseYear + "年" + (chooseMonth + 1) + "月"  + chooseDay + "日");
            }
        }, curYear, curMonth, curDay);

        dpd.show();
    }

    void doChooseTime(){
        TimePickerDialog dialog = new TimePickerDialog( this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                chooseHour = hourOfDay;
                chooseMinute = minute;

                timeChoosed = true;

                TextView timeview = (TextView)findViewById( R.id.time );
                timeview.setText( "" +  chooseHour + "时" + chooseMinute + "分" );
            }
        } ,0,0,true);

        dialog.show();
    }

    boolean dateChoosed = false;
    boolean timeChoosed = false;
    int chooseYear = 0;
    int chooseMonth = 0;
    int chooseDay = 0;

    int chooseHour = 0;
    int chooseMinute = 0;


    TbScheme chooseTbScheme;
    TbScheme[] schs;


    ButtonOnClick buttonOnClick = new ButtonOnClick(0);

    private class ButtonOnClick implements DialogInterface.OnClickListener{

        private int index;

        public ButtonOnClick(int index){
            this.index = index;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {

            Log.e(TAG,"index=" + index);

            if (which >= 0){
                index = which;
            }else {

                if (which == DialogInterface.BUTTON_POSITIVE) {
                    chooseTbScheme = TB3531.tbSchemeManager.getScheme( index );
                    TextView sc = (TextView)findViewById( R.id.scheme );
                    sc.setText( chooseTbScheme.name );
                }
            }
        }
    }

    void doChooseScheme(){
        if(schs == null) {
            schs = TB3531.tbSchemeManager.getschemes();
        }

        if(schs != null && schs.length > 0) {
            String[] items = new String[schs.length];

            for(int i = 0; i < schs.length;i++){
                items[i] = schs[i].name;
            }

            new AlertDialog.Builder( this ).setTitle( "请选择方案" ).setSingleChoiceItems(
                    items, 0, buttonOnClick )
                    .setPositiveButton( "确定", buttonOnClick )
                    .setNegativeButton( "取消", null ).show();
        }else{
            Toast.makeText( this,"没有方案可选择",Toast.LENGTH_LONG ).show();
        }
    }

    TbTimerListAdapter tbTimerListAdapter;


    private class TbTimerListAdapter extends BaseAdapter {
        private ArrayList<TbTimer> timerList;
        private LayoutInflater inflator;

        public TbTimerListAdapter() {
            super();
            timerList = new ArrayList<TbTimer>();
            inflator = SettingTimerPlanActivity.this.getLayoutInflater();
        }

        public void addItem(TbTimer timer) {
            if (!timerList.contains(timer)) {
                timerList.add(timer);
            }
        }

        public void clear() {
            timerList.clear();
        }

        @Override
        public int getCount() {
            return timerList.size();
        }

        @Override
        public Object getItem(int i) {
            return timerList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            SettingTimerPlanActivity.ViewHolder viewHolder;

            if (view == null) {
                view = inflator.inflate(R.layout.scheme_timer, null);
                viewHolder = new SettingTimerPlanActivity.ViewHolder();
                viewHolder.info = (TextView) view.findViewById(R.id.info);

                view.setTag(viewHolder);
            } else {
                viewHolder = (SettingTimerPlanActivity.ViewHolder) view.getTag();
            }

            TbTimer timer = timerList.get(i);

            //SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
           //String timerInfo = format.format(timer.calendar);


            Date date = timer.calendar.getTime();
            Date curDate = new Date();

            long s = (date.getTime() - curDate.getTime()) / 1000;

            String Exs = "还有";

            if(s > 0){
                int daysec = (24*3600);
                long days = s / daysec;

                long daySecs = s % (24*3600);

                long hh = daySecs / 3600;
                long mm = (daySecs / 60) % 60;
                long ss = daySecs % 60;

                if(days > 0){
                    Exs += days + "天";
                }

                if(hh > 0){
                    Exs += hh + "小时";
                }

                if(mm > 0){
                    Exs += mm + "分钟";
                }

                viewHolder.info.setTextColor( 0xff000000 );

            }else{
                Exs = "过时的定时器";
                viewHolder.info.setTextColor( 0xffff0000 );
            }

            String timerInfo = timer.name + " " + Exs;

            viewHolder.info.setText( timerInfo );

            return view;
        }
    }

    static class ViewHolder {
        TextView info;
    }


    /*
    TimePickerDialog dialog;

    public void startDateDialog(){
        dialog = new TimePickerDialog( this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            }
        } ,0,0,true);
    }

    void test(){
        Calendar calendar = Calendar.getInstance();
        calendar.get(Calendar.SECOND);
    }
    */
}
