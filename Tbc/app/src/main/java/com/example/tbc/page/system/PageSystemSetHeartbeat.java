package com.example.tbc.page.system;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tbc.R;
import com.example.tbc.protocal.RemoteCallRet;
import com.example.tbc.protocal.TB3531;

public class PageSystemSetHeartbeat extends Activity implements RemoteCallRet {

    Button set;
    TextView callRet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_system_set_heartbeat);

        initSubmit();
    }

    void initSubmit(){
        set = (Button)findViewById(R.id.submit);
        callRet = (TextView)findViewById(R.id.resp);

        set.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                /*if (Bluetooth.write(TB3531.packEvent(
                        TB3531.EVENT_GROUP_ID_SYSTEM,
                        TB3531.EVENT_SYSTEM_ID_CONTROL_HEARTBEAT))) {

                    set.setEnabled(false);
                    callRet.setText("");
                }*/

            }
        });
    }

    @Override
    public void onCallRet(Byte[] response) {

        TB3531.EventInfo ei = TB3531.parsePackage(response);

        if(ei != null && ei.EventGroupID == TB3531.EVENT_GROUP_ID_SYSTEM
                && ei.eventId == TB3531.EVENT_SYSTEM_ID_CONTROL_HEARTBEAT){

            set.setEnabled(true);

            callRet.setText(ei.event[0] == 0?"成功":"失败");
        }
    }

}
