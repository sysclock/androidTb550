package com.example.tbc.page.display;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tbc.R;
import com.example.tbc.protocal.RemoteCallRet;
import com.example.tbc.protocal.TB3531;

public class PageDisplayContrast extends Activity implements RemoteCallRet {

    Button set;
    TextView callRet;

    int srcID;
    EditText vValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_display_set_contrast);

        vValue = (EditText)findViewById(R.id.value);

        initSubmit();
        initSourceGroup();
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

    void initSubmit(){
        set = (Button)findViewById(R.id.submit);
        callRet = (TextView)findViewById(R.id.resp);

        set.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                int value = Integer.parseInt("" + vValue.getText());

                if(value >= 0 && value <= 100) {
                    /*if (Bluetooth.write(TB3531.packEvent(
                            TB3531.EVENT_GROUP_ID_DISPLAY,
                            TB3531.EVENT_DISPLAY_ID_SET_CONTRAST,
                            (byte) (srcID & 0xff),
                            (byte) (value & 0xff)))) {

                        set.setEnabled(false);
                        callRet.setText("");
                    }*/
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "错误的值,(0-100)", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
    }

    @Override
    public void onCallRet(Byte[] response) {

        TB3531.EventInfo ei = TB3531.parsePackage(response);

        if(ei != null && ei.EventGroupID == TB3531.EVENT_GROUP_ID_DISPLAY
                && ei.eventId == TB3531.EVENT_DISPLAY_ID_SET_CONTRAST){

            set.setEnabled(true);

            callRet.setText(ei.event[0] == 0?"成功":"失败");
        }
    }

}
