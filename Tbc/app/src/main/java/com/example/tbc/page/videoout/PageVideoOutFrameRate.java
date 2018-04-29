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

public class PageVideoOutFrameRate extends Activity implements RemoteCallRet {

    Button set;
    TextView callRet;

    int srcID = 0;
    int resId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_videoout_frame_rate);

        initSourceGroup();
        initFrameRateGroup();

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
                        TB3531.EVENT_GROUP_ID_VIDEO,TB3531.EVENT_VIDEO_ID_GET_INFO,
                        (byte)(srcID & 0xff),
                        (byte)(resId & 0xff)))){

                    set.setEnabled(false);
                    callRet.setText("");
                }*/
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

    void initFrameRateGroup(){
        RadioGroup rsGroup = (RadioGroup)findViewById(R.id.radioGroupFrameRate);
        rsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // TODO Auto-generated method stub
                RadioButton radioButton = (RadioButton)findViewById(checkedId);

                String text = radioButton.getText().toString();

                procResolutionCheck(text);
            }
        });
    }

    void procResolutionCheck(String text){
        switch(text){
            case "60":
                resId = 1;
                break;
            case "50":
                resId = 2;
                break;
            case "30":
                resId = 3;
                break;
            default:
                resId = 1;
                break;
        }
    }


    @Override
    public void onCallRet(Byte[] response) {

        TB3531.EventInfo ei = TB3531.parsePackage(response);

        if(ei != null && ei.EventGroupID == TB3531.EVENT_GROUP_ID_VIDEO
                && ei.eventId == TB3531.EVENT_VIDEO_ID_SET_FRAMERATE){

            set.setEnabled(true);

            callRet.setText(ei.event[0] == 0?"成功":"失败");
        }
    }

}
