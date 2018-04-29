package com.example.tbc.page.sound;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tbc.R;
import com.example.tbc.protocal.RemoteCallRet;
import com.example.tbc.protocal.TB3531;

public class PageSoundGetVolume extends Activity implements RemoteCallRet {

    Button set;
    TextView callRet;

    TextView vVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_sound_get_volume);

        vVolume = (TextView)findViewById(R.id.volume);

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

               /* if(Bluetooth.write(TB3531.packEvent(
                        TB3531.EVENT_GROUP_ID_SOUND,
                        TB3531.EVENT_SOUND_ID_GET_VOLUME))){

                    set.setEnabled(false);
                    callRet.setText("");
                }*/
            }
        });
    }




    @Override
    public void onCallRet(Byte[] response) {

        TB3531.EventInfo ei = TB3531.parsePackage(response);

        if(ei != null && ei.EventGroupID == TB3531.EVENT_GROUP_ID_SOUND
                && ei.eventId == TB3531.EVENT_SOUND_ID_GET_VOLUME){

            set.setEnabled(true);

            callRet.setText(ei.event[0] == 0?"成功":"失败");

            if(ei.event[0] == 0) {
                vVolume.setText("" + (int) (ei.event[1] & 0xff));
            }else{
                vVolume.setText("");
            }
        }
    }

}
