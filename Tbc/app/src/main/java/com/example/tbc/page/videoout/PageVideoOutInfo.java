package com.example.tbc.page.videoout;

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

public class PageVideoOutInfo extends Activity implements RemoteCallRet {

    Button set;
    TextView callRet;

    int srcID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_videoout_info);

        initSourceGroup();

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

                /*
                TB3531.write(
                        TB3531.EVENT_GROUP_ID_VIDEO,
                        TB3531.EVENT_VIDEO_ID_GET_INFO,
                        (byte)(srcID & 0xff));


                set.setEnabled(false);
                callRet.setText("");
                */

            }
        });
    }

    void initSourceGroup(){
        RadioGroup srcGroup = (RadioGroup)findViewById(R.id.radioGroupSource);
        srcGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // TODO Auto-generated method stub
                RadioButton radioButton = (RadioButton)findViewById(checkedId);

                String text = radioButton.getText().toString();

                srcID = text.equals("VGA")?0:1;

                Log.i("tag", "你选择的源是：" + text);
            }
        });
    }


    @Override
    public void onCallRet(Byte[] response) {

        TB3531.EventInfo ei = TB3531.parsePackage(response);

        if(ei != null && ei.EventGroupID == TB3531.EVENT_GROUP_ID_VIDEO
                && ei.eventId == TB3531.EVENT_VIDEO_ID_GET_INFO){

            set.setEnabled(true);

            callRet.setText(ei.event[0] == 0?"成功":"失败");

            TextView vw = (TextView)findViewById(R.id.width);
            TextView vh = (TextView)findViewById(R.id.height);
            TextView vframe = (TextView)findViewById(R.id.framerate);

            int w = ((ei.event[1] & 0xff) << 8) | ((ei.event[2] & 0xff));
            int h = ((ei.event[2] & 0xff) << 8) | ((ei.event[3] & 0xff));
            int frame = ((ei.event[4] & 0xff));

            vw.setText("" + w);
            vh.setText("" + h);
            vframe.setText("" + frame);
        }
    }
}
