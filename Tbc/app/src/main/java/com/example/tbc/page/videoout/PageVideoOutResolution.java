package com.example.tbc.page.videoout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.tbc.R;
import com.example.tbc.protocal.RemoteCallRet;
import com.example.tbc.protocal.TB3531;

public class PageVideoOutResolution extends Activity implements RemoteCallRet {

    Button set;
    TextView callRet;

    int srcID = 0;
    int resId = 0;
    int width = 1920;
    int height = 1080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_videoout_set_resolution);

        initSourceGroup();
        initResolutionGroup();

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

                Byte[] pack;

                if(resId == 0){

                    Byte[] evContent = new Byte[6];

                    evContent[0] = (byte)(srcID & 0xff);
                    evContent[1] = (byte)(resId & 0xff);

                    evContent[2] = (byte)((width >> 8) & 0xff);
                    evContent[3] = (byte)(width & 0xff);

                    evContent[4] = (byte)((height >> 8) & 0xff);
                    evContent[5] = (byte)(height & 0xff);


                    pack = TB3531.packEvent(
                            TB3531.EVENT_GROUP_ID_VIDEO,
                            TB3531.EVENT_VIDEO_ID_SET_RESOLUTION,
                            evContent);

                }else{
                    pack = TB3531.packEvent(
                            TB3531.EVENT_GROUP_ID_VIDEO,
                            TB3531.EVENT_VIDEO_ID_SET_RESOLUTION,
                            (byte)(srcID & 0xff),
                            (byte)(resId & 0xff));
                }

               /* if(Bluetooth.write(pack)){

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

    void initResolutionGroup(){
        RadioGroup rsGroup = (RadioGroup)findViewById(R.id.radioGroupResolution);
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
            case "自定义":
                updateCoustomResolution();
                break;
            case "2048*1152":
                resId = 1;width = 2048;height = 1152;
                break;
            case "1920*1200":
                resId = 2;width = 1920;height = 1200;
                break;
            case "1920*1080":
                resId = 3;width = 1920;height = 1080;
                break;
            case "1680*1050":
                resId = 4;width = 1680;height = 1050;
                break;
            case "1600*1200":
                resId = 5;width = 1600;height = 1200;
                break;
            case "1440*900":
                resId = 6;width = 1440;height = 900;
                break;
            case "1366*768":
                resId = 7;width = 1366;height = 768;
                break;
            case "1280*1024":
                resId = 8;width = 1280;height = 1024;
                break;
            case "1280*720":
                resId = 9;width = 1280;height = 720;
                break;
            case "1024*768":
                resId = 10;width = 1024;height = 768;
                break;
            case "800*600":
                resId = 11;width = 800;height = 600;
                break;
            case "640*480":
                resId = 12;width = 640;height = 480;
                break;
        }
    }

    void updateCoustomResolution(){

        EditText w = (EditText)findViewById(R.id.editTextWidth);
        EditText h = (EditText)findViewById(R.id.editTextHeight);

        resId = 0;

        try {
            width = Integer.parseInt(w.getText().toString());
            height = Integer.parseInt(h.getText().toString());
        }catch(Exception e){
            Log.e("","bad width or height");
        }
    }

    @Override
    public void onCallRet(Byte[] response) {

        TB3531.EventInfo ei = TB3531.parsePackage(response);

        if(ei != null && ei.EventGroupID == TB3531.EVENT_GROUP_ID_VIDEO
                && ei.eventId == TB3531.EVENT_VIDEO_ID_SET_RESOLUTION){

            set.setEnabled(true);

            callRet.setText(ei.event[0] == 0?"成功":"失败");
        }
    }

}
