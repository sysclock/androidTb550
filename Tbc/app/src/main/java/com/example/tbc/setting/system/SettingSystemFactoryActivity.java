package com.example.tbc.setting.system;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tbc.R;
import com.example.tbc.protocal.TB3531;

public class SettingSystemFactoryActivity extends Activity{

    public final static String TAG = "SettingSYSFactory";

    Button set;
    TextView callRet;
    int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_system_factory);

        initSubmit();
        //initGroupUpgradeMode();
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

        if(data[4] == (byte)TB3531.EVENT_GROUP_ID_SYSTEM
                && data[5] == (byte)TB3531.EVENT_SYSTEM_ID_FACTORY){

            String ret;

            int err = data[TB3531.EVENT_MSG_OFFSET];
            if(err == 0){
                ret = "成功";
            }else{
                ret = "失败，未知错误,err=" + err;
            }

            callRet.setText("出厂设置：" + ret);

            Toast.makeText( this,"出厂设置：" + ret ,Toast.LENGTH_SHORT).show();
        }
    }

    void initSubmit(){
        set = (Button)findViewById(R.id.submit);
        callRet = (TextView)findViewById(R.id.resp);

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(SettingSystemFactoryActivity.this).setTitle("确认要恢复出厂状态吗？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                TB3531.writeAsync(TB3531.EVENT_GROUP_ID_SYSTEM,
                                        TB3531.EVENT_SYSTEM_ID_FACTORY);

                            }
                        })
                        .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();

            }
        });
    }

    /*
    void initGroupUpgradeMode(){
        RadioGroup srcGroup = (RadioGroup)findViewById(R.id.radioGroup1);
        srcGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // TODO Auto-generated method stub
                RadioButton radioButton = (RadioButton)findViewById(checkedId);

                String text = radioButton.getText().toString();

                mode = text.equals("USB")?0:1;

                Log.i("tag", "你选择的升级方式是：" + text);
            }
        });
    }
    */
}
