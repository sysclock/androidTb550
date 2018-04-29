
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
import com.example.tbc.protocal.TbScheme;

import java.util.ArrayList;
import java.util.Calendar;

public class SettingLogActivity extends Activity implements View.OnClickListener {

    public final static String TAG = "SettingLog";

    Button set;
    TextView content;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_log);

        initSubmit();

        content = (TextView)findViewById( R.id.content );

        content.setText( TB3531.logBuf.toString() );
    }

    @Override
    protected void onResume() {

        TB3531.setDataHandler(handler);

        //initList();

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        TB3531.setDataHandler(null);
        super.onDestroy();
    }



    protected Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case TB3531.TB_BLUETOOTH_DATA_EVENT:
                    processData( (Byte[]) message.obj );
                    break;
                case TB3531.TB_BLUETOOTH_LOG_EVENT:
                    content.append( (String)message.obj );
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
        //set = (Button)findViewById(R.id.submit);

        //set.setOnClickListener(this);
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

    }


}
