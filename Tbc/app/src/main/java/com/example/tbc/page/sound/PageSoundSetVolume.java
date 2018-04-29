package com.example.tbc.page.sound;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tbc.R;
import com.example.tbc.protocal.RemoteCallRet;
import com.example.tbc.protocal.TB3531;

public class PageSoundSetVolume extends Activity implements RemoteCallRet {

    Button set;
    TextView callRet;

    int sound = 50;
    EditText vVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_sound_set_volume);

        vVolume = (EditText)findViewById(R.id.volume);

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

                int volume = Integer.parseInt("" + vVolume.getText());

                if(volume >= 0 && volume <= 100) {
                   /* if (Bluetooth.write(TB3531.packEvent(
                            TB3531.EVENT_GROUP_ID_SOUND,
                            TB3531.EVENT_SOUND_ID_SET_VOLUME,
                            (byte) (volume & 0xff)))) {

                        set.setEnabled(false);
                        callRet.setText("");
                    }*/
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "错误的音量值,(0-100)", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
    }

    @Override
    public void onCallRet(Byte[] response) {

        TB3531.EventInfo ei = TB3531.parsePackage(response);

        if(ei != null && ei.EventGroupID == TB3531.EVENT_GROUP_ID_SOUND
                && ei.eventId == TB3531.EVENT_SOUND_ID_SET_VOLUME){

            set.setEnabled(true);

            callRet.setText(ei.event[0] == 0?"成功":"失败");
        }
    }

}
