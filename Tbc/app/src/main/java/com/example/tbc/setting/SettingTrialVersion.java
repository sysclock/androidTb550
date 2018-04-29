package com.example.tbc.setting;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.tbc.R;
import com.example.tbc.protocal.TB3531;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class SettingTrialVersion extends Activity implements View.OnClickListener{

    public final static String TAG = "TrialVersion";

    //EditText vCMD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting_trialversion);

        //vCMD = (EditText)findViewById( R.id.cmd );


    }

    @Override
    protected void onResume() {

        TB3531.setDataHandler(handler);

        doGetTrialInfo();

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        TB3531.setDataHandler(null);
        super.onDestroy();
    }

    void goToOtherPage(Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(SettingTrialVersion.this, cls);
        startActivity(intent);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chooseDate:
                doChooseDate();
                break;
            case R.id.chooseTime:
                doChooseTime();
                break;
            case R.id.trialSet:
                doTrialSet();
                break;
            case R.id.trialUnSet:
                doTrialUnSet();
                break;
        }
    }

    void hint(String text){
        Toast.makeText( this,text,Toast.LENGTH_SHORT ).show();
    }

    void doTrialSet(){

        EditText vPass = (EditText)findViewById( R.id.trialPassword );
        EditText vTel = (EditText)findViewById( R.id.trialTel );


        if(!dateChoosed || !timeChoosed){
            hint("请输入试用终止日期和时间");
            return;
        }

        final Calendar c = Calendar.getInstance();


        Calendar setTimer = new GregorianCalendar(chooseYear,chooseMonth,chooseDay,
                chooseHour,chooseMinute,0);

        if(setTimer.getTime().getTime() - c.getTime().getTime() <= 0){
            hint("试用时间点不能小于当前时间");
            return;
        }

        chooseTel = vTel.getText().toString();
        if(chooseTel.length() <= 0){
            hint("请输入联系电话");
            return;
        }
        String sPass = vPass.getText().toString();

        doSendTrial(sPass.getBytes(),chooseTel.getBytes());
    }

    void doSendTrial(byte[] pass,byte[] tel){

        int i;
        Byte[] param = new Byte[39];

        for(i = 0; i < 8; i ++){
            if( i < pass.length){
                param[i] = pass[i];
                param[i + 8] = pass[i];
            }else{
                param[i] = 0;
                param[i + 8] = 0;
            }
        }


        param[16] = (byte)(chooseYear >> 8);
        param[17] = (byte)(chooseYear & 0xff);

        param[18] = (byte)(chooseMonth);
        param[19] = (byte)(chooseDay);
        param[20] = (byte)(chooseHour);
        param[21] = (byte)(chooseMinute);
        param[22] = (byte)(0);

        for(i = 0; i < 16;i++){
            if(tel != null &&  i < tel.length){
                param[i + 23] = tel[i];

            }else{
                param[i + 23] = 0;
            }
        }

        Log.i(TAG,"发送命令设置试用信息.");


        TB3531.writeAsync(TB3531.EVENT_GROUP_ID_TRIAL_VERSION,
                TB3531.EVENT_TRIAL_VERSION_SET ,param);
    }

    void doTrialUnSet(){
        EditText vPass = (EditText)findViewById( R.id.trialPassword );
        String sPass = vPass.getText().toString();

        byte[] pass = sPass.getBytes();

        Byte[] param = new Byte[8];

        for(int i = 0; i < 8; i ++){
            if( i < pass.length){
                param[i] = pass[i];
            }else{
                param[i] = 0;
            }
        }

        Log.i(TAG,"发送命令解除试用信息.");

        TB3531.writeAsync(TB3531.EVENT_GROUP_ID_TRIAL_VERSION,
                TB3531.EVENT_TRIAL_VERSION_CLEAR ,param);
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

    String chooseTel;

    int chooseYear = 0;
    int chooseMonth = 0;
    int chooseDay = 0;

    int chooseHour = 0;
    int chooseMinute = 0;

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

    void doGetTrialInfo(){

        Log.i(TAG,"发送命令获取试用信息.");
        TB3531.writeAsync(TB3531.EVENT_GROUP_ID_TRIAL_VERSION,
                TB3531.EVENT_TRIAL_VERSION_GET );
    }

    int status = 0;

    int year;
    int month;
    int day;

    int hour;
    int minute;
    int second;
    String tel;

    void parseTrialInfo(Byte[] data){

        int off = TB3531.EVENT_MSG_OFFSET;

        status = data[off + 1];
        status = 0;

        year = ((data[off + 10] & 0xff) << 8) | (data[off + 11] & 0xff);
        month = (data[off + 12] & 0xff);
        day = (data[off + 13] & 0xff);

        hour = (data[off + 14] & 0xff);
        minute = (data[off + 15] & 0xff);
        second = (data[off + 16] & 0xff);

        tel = TB3531.getStringFromBytes(data,off + 17,16);

        setStatus(status,null);
    }

    void setStatus(int st,String str){

        TextView v = (TextView)findViewById( R.id.info );

        status = st;

        if(str == null){
            if(status == 0){
                str = "正式版";
            }else if(status == 1){

                Log.e(TAG,"到期时间：" + year + "-" + month + "-" + day
                        + " " + hour + ":" + minute + ":" + second);

                Calendar ca = new GregorianCalendar(year,month,day,hour,minute,second);

                long s = (ca.getTime().getTime() - (new Date()).getTime()) / 1000;

                if(s > 0){
                    int daysec = (24*3600);
                    long days = s / daysec;

                    if(days > 0) {
                        str = "试用版,还有" + days + "天到期";
                    }else{
                        long daySecs = s % (24*3600);
                        long hh = daySecs / 3600;
                        long mm = (daySecs / 60) % 60;

                        str = "试用版,还有" + hh + "小时" + mm + "分钟到期";
                    }

                }else{
                    str = "试用版,已经过期";
                }

                str += ",电话" + tel;

            }else{
                str = "未知试用类型" + status;
            }
        }

        if(status == 0){
            v.setTextColor( Color.BLUE );
        }else{
            v.setTextColor( Color.RED );
        }

        v.setText( str );
    }

    public void processData(Byte[] data) {

        TB3531.logBuffer( data,data.length ,"read event data:");

        if(data[4] == (byte)TB3531.EVENT_GROUP_ID_TRIAL_VERSION
                && data[5] == (byte)TB3531.EVENT_TRIAL_VERSION_GET){

            String ret;

            int err = data[TB3531.EVENT_MSG_OFFSET];
            if(err == 0){
                ret = "成功";
                parseTrialInfo(data);
            }else{
                ret = "失败，未知错误,err=" + err;
            }

            Log.i(TAG,"获取试用信息：" + ret);
            Toast.makeText( this,"获取试用信息：" + ret ,Toast.LENGTH_SHORT).show();

        }else if(data[4] == (byte)TB3531.EVENT_GROUP_ID_TRIAL_VERSION
                && data[5] == (byte)TB3531.EVENT_TRIAL_VERSION_SET){

            String ret;

            int err = data[TB3531.EVENT_MSG_OFFSET];
            if(err == 0){
                ret = "成功";

                year = chooseYear;
                month = chooseMonth;
                day = chooseDay;
                hour = chooseHour;
                minute= chooseMinute;
                second = 0;
                tel = chooseTel;

                dateChoosed = false;
                timeChoosed = false;

                TextView dateview = (TextView)findViewById( R.id.date );
                dateview.setText("");

                TextView timeview = (TextView)findViewById( R.id.time );
                timeview.setText("");

                setStatus(1,null);
            }else{
                ret = "失败，未知错误,err=" + err;
            }

            Log.i(TAG,"设置试用信息：" + ret);

            Toast.makeText( this,"设置试用信息：" + ret ,Toast.LENGTH_SHORT).show();

        }else if(data[4] == (byte)TB3531.EVENT_GROUP_ID_TRIAL_VERSION
                && data[5] == (byte)TB3531.EVENT_TRIAL_VERSION_CLEAR){

            String ret;

            int err = data[TB3531.EVENT_MSG_OFFSET];
            if(err == 0){
                ret = "成功";
                setStatus(0,"正式版");
                dateChoosed = false;
                timeChoosed = false;
            }else{
                ret = "失败，未知错误,err=" + err;
            }

            Log.i(TAG,"解除试用：" + ret);
            Toast.makeText( this,"解除试用：" + ret ,Toast.LENGTH_SHORT).show();
        }
    }
}