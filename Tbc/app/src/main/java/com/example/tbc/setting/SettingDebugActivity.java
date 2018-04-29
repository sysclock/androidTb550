package com.example.tbc.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tbc.R;
import com.example.tbc.protocal.TB3531;

import java.util.Calendar;


public class SettingDebugActivity extends Activity implements View.OnClickListener{

    public final static String TAG = "SettingDebug";

    EditText vCMD;
    int sencesFlag = 0;//场景的编号


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d("SetD onCreate","Enter SettingDebugActivity class onCreate");

        setContentView(R.layout.activity_debug);
        //获取场景编号
        Intent intent = getIntent();
        int defaultTmp = 0;
        sencesFlag = intent.getIntExtra("sences",defaultTmp);
        vCMD = (EditText)findViewById( R.id.cmd );

    }

    @Override
    protected void onResume() {

        TB3531.setDataHandler(handler);

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        TB3531.setDataHandler(null);
        super.onDestroy();
    }

    void goToOtherPage(Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(SettingDebugActivity.this, cls);
        startActivity(intent);
    }


    void exeCommand()
    {

        Log.d("exe Command","Enter exeCommand function ");
        String sname = vCMD.getText().toString();
        if (0 == sname.length())
        {
            Toast.makeText( this, "请输入场景名", Toast.LENGTH_SHORT ).show();
        }
        else
        {
            //保存用户的修改
            SharedPreferences.Editor  editor = getSharedPreferences("data",MODE_PRIVATE).edit();
            editor.putInt("sencesIndex",sencesFlag);
            editor.putString("name",sname);
            editor.apply();
            Toast.makeText( this, "场景名称修改成功", Toast.LENGTH_SHORT ).show();
            Log.d("user name is ",sname);
            //向蓝牙发送指令
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.submit:
                exeCommand();
                break;
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

    void doClearTrialVersion(){

        final EditText editText = new EditText(this);

        editText.setMaxEms( 6 );
        editText.setInputType( InputType.TYPE_CLASS_NUMBER  );
        editText.setTransformationMethod( PasswordTransformationMethod.getInstance());

        final AlertDialog builder = new AlertDialog.Builder( this )
                .setTitle( "请输入试用密码" )
                .setIcon( android.R.drawable.ic_dialog_info )
                .setView( editText )
                .setPositiveButton( "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String ps = editText.getText().toString();

                        byte[] psbase = ps.getBytes();
                        Byte[] psbyte = new Byte[8];

                        for(int i = 0; i < 8; i ++){
                            if(i < psbase.length){
                                psbyte[i] = psbase[i];
                            }else{
                                psbyte[i] = (byte)0;
                            }
                        }
                        //发送数据到蓝牙设备
                        TB3531.writeAsync( TB3531.EVENT_GROUP_ID_TRIAL_VERSION,
                                TB3531.EVENT_TRIAL_VERSION_CLEAR,psbyte);
                    }
                } )
                .setNegativeButton( "取消", null )
                .show();
    }

    void doSetTrialDays(final int days){

        final EditText editText = new EditText(this);

        editText.setMaxEms( 6 );
        editText.setInputType( InputType.TYPE_CLASS_NUMBER  );
        editText.setTransformationMethod( PasswordTransformationMethod.getInstance());

        final AlertDialog builder = new AlertDialog.Builder( this )
                .setTitle( "请输入试用密码" )
                .setIcon( android.R.drawable.ic_dialog_info )
                .setView( editText )
                .setPositiveButton( "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String ps = editText.getText().toString();

                        byte[] psbase = ps.getBytes();

                        doSendTrial(psbase,new byte[0],days);
                    }
                } )
                .setNegativeButton( "取消", null )
                .show();

    }

    void doSendTrial(byte[] pass,byte[] tel,int days){

        final Calendar c = Calendar.getInstance();

        c.add(Calendar.DAY_OF_MONTH,days);

        int chooseYear = c.get(Calendar.YEAR);
        int chooseMonth = c.get(Calendar.MONTH);
        int chooseDay= c.get(Calendar.DAY_OF_MONTH);

        int chooseHour = c.get(Calendar.HOUR_OF_DAY);
        int chooseMinute = c.get(Calendar.MINUTE);

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
        //向蓝牙发送试用信息
        TB3531.writeAsync(TB3531.EVENT_GROUP_ID_TRIAL_VERSION,
                TB3531.EVENT_TRIAL_VERSION_SET ,param);
    }

    public void processData(Byte[] data) {

        TB3531.logBuffer( data,data.length ,"read event data:");

        if(data[4] == (byte)TB3531.EVENT_GROUP_ID_TRIAL_VERSION
                && data[5] == (byte)TB3531.EVENT_TRIAL_VERSION_CLEAR){

            String ret;

            int err = data[TB3531.EVENT_MSG_OFFSET];
            if(err == 0){
                ret = "成功";
            }else{
                ret = "失败，未知错误,err=" + err;
            }

            Toast.makeText( this,"清除试用成功：" + ret ,Toast.LENGTH_SHORT).show();

        }else if(data[4] == (byte)TB3531.EVENT_GROUP_ID_TRIAL_VERSION
                && data[5] == (byte)TB3531.EVENT_TRIAL_VERSION_SET){

            String ret;

            int err = data[TB3531.EVENT_MSG_OFFSET];
            if(err == 0){
                ret = "成功";
            }else{
                ret = "失败，未知错误,err=" + err;
            }

            Log.i(TAG,"设置试用信息：" + ret);

            Toast.makeText( this,"设置试用信息：" + ret ,Toast.LENGTH_SHORT).show();
        }
    }

}