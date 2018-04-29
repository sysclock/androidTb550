package com.example.tbc.page.led;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tbc.R;
import com.example.tbc.protocal.RemoteCallRet;
import com.example.tbc.protocal.TB3531;

public class PageLedSetResolution extends Activity implements RemoteCallRet {

    Button set;
    TextView callRet;

    int width = 0;
    int height = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_led_set_resolution);

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

            updateCoustomResolution();

            Byte[] evContent = new Byte[4];

            evContent[0] = (byte)((width >> 8) & 0xff);
            evContent[1] = (byte)(width & 0xff);

            evContent[2] = (byte)((height >> 8) & 0xff);
            evContent[3] = (byte)(height & 0xff);

            Byte[] pack = TB3531.packEvent(
                    TB3531.EVENT_GROUP_ID_LED,
                    TB3531.EVENT_LED_ID_SET_RESOLUTION,
                    evContent);

            /*if(Bluetooth.write(pack)){

                set.setEnabled(false);
                callRet.setText("");
            }*/
            }
        });
    }

    void updateCoustomResolution(){

        EditText w = (EditText)findViewById(R.id.editTextWidth);
        EditText h = (EditText)findViewById(R.id.editTextHeight);

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

        if(ei != null && ei.EventGroupID == TB3531.EVENT_GROUP_ID_LED
                && ei.eventId == TB3531.EVENT_LED_ID_SET_RESOLUTION){

            set.setEnabled(true);

            callRet.setText(ei.event[0] == 0?"成功":"失败");
        }
    }

}
