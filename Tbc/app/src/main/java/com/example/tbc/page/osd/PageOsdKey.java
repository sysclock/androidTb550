package com.example.tbc.page.osd;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tbc.R;
import com.example.tbc.protocal.RemoteCallRet;
import com.example.tbc.protocal.TB3531;

public class PageOsdKey extends Activity implements RemoteCallRet {

    final int KEY_NUM = 9;

    View.OnClickListener kl;

    Button[] keys;
    TextView callRet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_osd_key);

        initSubmit();
    }

    void initSubmit(){

        keys = new Button[KEY_NUM];

        callRet = (TextView)findViewById(R.id.resp);


        kl = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Button vKey = (Button)v;

                int keyCode = getKeyCode(vKey);

                /*if (Bluetooth.write(TB3531.packEvent(
                        TB3531.EVENT_GROUP_ID_OSD,
                        TB3531.EVENT_OSD_ID_SET_GENERAL,
                        (byte) (keyCode & 0xff)))) {

                    callRet.setText("");
                }*/
            }

            int getKeyCode(Button vKey){

                int keyCode = 0;

                int id = vKey.getId();

                switch(id){
                    case R.id.key0:
                        keyCode = 0;
                        break;
                    case R.id.key1:
                        keyCode = 1;
                        break;
                    case R.id.key2:
                        keyCode = 2;
                        break;
                    case R.id.key3:
                        keyCode = 3;
                        break;
                    case R.id.key4:
                        keyCode = 4;
                        break;
                    case R.id.key5:
                        keyCode = 5;
                        break;
                    case R.id.key6:
                        keyCode = 6;
                        break;
                    case R.id.key7:
                        keyCode = 7;
                        break;
                    case R.id.key8:
                        keyCode = 8;
                        break;
                }

                return keyCode;
            }
        };

        int[] kids = {
            R.id.key0,R.id.key1,R.id.key2,
            R.id.key3,R.id.key4,R.id.key5,
            R.id.key6,R.id.key7,R.id.key8,
        };

        for(int i = 0; i < KEY_NUM;i++){

            keys[i] = (Button)findViewById(kids[i]);
            keys[i].setOnClickListener(kl);
        }

    }

    @Override
    public void onCallRet(Byte[] response) {

        TB3531.EventInfo ei = TB3531.parsePackage(response);

        if(ei != null && ei.EventGroupID == TB3531.EVENT_GROUP_ID_OSD
                && ei.eventId == TB3531.EVENT_OSD_ID_SET_GENERAL){

           callRet.setText("" + ei.event[0]);
        }
    }

}
