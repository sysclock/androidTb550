package com.example.tbc.setting.system;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tbc.R;
import com.example.tbc.protocal.TB3531;

public class SettingSystemUpgradeActivity extends Activity{

    public final static String TAG = "SettingSYSUpgrade";

    Button set;
    TextView callRet;
    int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_system_upgreade);

        initSubmit();
        initGroupUpgradeMode();
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
                && data[5] == (byte)TB3531.EVENT_SYSTEM_ID_UPGRADE){

            String ret;

            int err = data[TB3531.EVENT_MSG_OFFSET];
            if(err == 0){
                ret = "成功";
            }else if(err == 1){
                ret = "当前版本已经是最新版本";
            }else if(err == 2){
                ret = "USB上不存在升级文件";
            }else if(err == 3){
                ret = "无法连接到网络";
            }else{
                ret = "失败，未知错误,err=" + err;
            }

            callRet.setText("升级：" + ret);

            Toast.makeText( this,"升级：" + ret ,Toast.LENGTH_SHORT).show();
        }
    }

    void initSubmit(){
        set = (Button)findViewById(R.id.submit);
        callRet = (TextView)findViewById(R.id.resp);

        set.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TB3531.writeAsync(TB3531.EVENT_GROUP_ID_SYSTEM,
                        TB3531.EVENT_SYSTEM_ID_UPGRADE,(byte)mode);

            }
        });
    }

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

    /*
    @Override
    public void onCallRet(Byte[] response) {

        TB3531.EventInfo ei = TB3531.parsePackage(response);

        if(ei != null && ei.EventGroupID == TB3531.EVENT_GROUP_ID_SYSTEM
                && ei.eventId == TB3531.EVENT_SYSTEM_ID_UPGRADE){

            set.setEnabled(true);

            callRet.setText(ei.event[0] == 0?"成功":"失败");
        }
    }

    */

}
