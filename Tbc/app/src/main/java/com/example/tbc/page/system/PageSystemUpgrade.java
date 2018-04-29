package com.example.tbc.page.system;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.tbc.R;
import com.example.tbc.protocal.RemoteCallRet;
import com.example.tbc.protocal.TB3531;

public class PageSystemUpgrade extends Activity implements RemoteCallRet {

    Button set;
    TextView callRet;
    int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_system_upgreade);

        initSubmit();
        initGroupUpgradeMode();
    }

    void initSubmit(){
        set = (Button)findViewById(R.id.submit);
        callRet = (TextView)findViewById(R.id.resp);

        set.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


               /* if (Bluetooth.write(TB3531.packEvent(
                        TB3531.EVENT_GROUP_ID_SYSTEM,
                        TB3531.EVENT_SYSTEM_ID_UPGRADE))) {

                    set.setEnabled(false);
                    callRet.setText("");
                }*/

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

    @Override
    public void onCallRet(Byte[] response) {

        TB3531.EventInfo ei = TB3531.parsePackage(response);

        if(ei != null && ei.EventGroupID == TB3531.EVENT_GROUP_ID_SYSTEM
                && ei.eventId == TB3531.EVENT_SYSTEM_ID_UPGRADE){

            set.setEnabled(true);

            callRet.setText(ei.event[0] == 0?"成功":"失败");
        }
    }

}
