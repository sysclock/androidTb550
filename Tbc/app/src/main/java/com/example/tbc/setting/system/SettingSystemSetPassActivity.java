
package com.example.tbc.setting.system;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tbc.R;
import com.example.tbc.protocal.TB3531;

public class SettingSystemSetPassActivity extends Activity {

    public final static String TAG = "SettingPass";

    Button set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_setting_system_passset);

        initSubmit();
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
                && data[5] == (byte)TB3531.EVENT_SYSTEM_ID_SET_PIN){

            String ret;

            int err = data[TB3531.EVENT_MSG_OFFSET];
            if(err == 0){
                ret = "成功";
            }else{
                ret = "失败，未知错误,err=" + err;
            }

            Toast.makeText( this,"设置密码：" + ret ,Toast.LENGTH_SHORT).show();
        }
    }

    void initSubmit(){
        set = (Button)findViewById(R.id.submit);

        set.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText vold = (EditText)findViewById(R.id.oldpass  );
                EditText vnew = (EditText)findViewById(R.id.newpass  );
                EditText vnewrep = (EditText)findViewById(R.id.newpassrep  );

                byte[] old = vold.getText().toString().getBytes();
                byte[] n = vnew.getText().toString().getBytes();

                if(vnew.getText().toString().equals( vnewrep.getText().toString() )) {

                    Byte[] con = new Byte[old.length + n.length  + 3];

                    int i;
                    int off = 0;

                    con[off++] = 0; // 0: 明文传输
                    con[off++] = (byte)(old.length);

                    for(i = 0;i < old.length;i++){
                        con[off++] = old[i];
                    }

                    con[off++] = (byte)(n.length);

                    for(i = 0;i < n.length;i++){
                        con[off++] = n[i];
                    }

                    TB3531.writeAsync( TB3531.EVENT_GROUP_ID_SYSTEM,
                            TB3531.EVENT_SYSTEM_ID_SET_PIN,con);
                }else{
                    Toast.makeText( SettingSystemSetPassActivity.this,
                            "新输入的两次密码不一致",Toast.LENGTH_LONG ).show();
                }
            }
        });
    }
}
