package com.example.tbc.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tbc.R;
import com.example.tbc.bluetooth.BluetoothSchemeActivity;
import com.example.tbc.protocal.TB3531;

import static com.example.tbc.bluetooth.BluetoothDiscoveryActivity.TAG;


public class SettingGuide1Activity extends Activity implements View.OnClickListener{

    public final static String TAG = "SettingGuide1";

    SettingGuideInfo sgi;
    Button next;
    EditText vw;
    EditText vh;
    EditText vname;

    public static int screenW = 10000;
    public static int screenH = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //getActionBar().setTitle( R.string.title_devices);

        setContentView(R.layout.activity_setting_guide1);

        vw = (EditText)findViewById( R.id.width );
        vh = (EditText)findViewById( R.id.height );
        vname = (EditText)findViewById( R.id.name );

        next = (Button)findViewById( R.id.next );

        sgi = SettingGuideInfo.getInstance();

        if(sgi.isScreenResSet){

            Log.i(TAG,"isScreenResSet is set.");

            vw.setText( "" + sgi.tbScheme.width );
            vh.setText( "" + sgi.tbScheme.height );
        }else{
            Log.i(TAG,"isScreenResSet is not Set.");
        }
    }

    /*
    @Override
    public void onBackPressed() {
        goToOtherPage(SettingHomeActivity.class);

    }*/

    void goToOtherPage(Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(SettingGuide1Activity.this, cls);
        startActivity(intent);
    }



    boolean setInfo(){

        boolean ret = false;

        try {

            String sw = vw.getText().toString();
            String sh = vh.getText().toString();
            String sname = vname.getText().toString();

            if(sw != null && sw.length() > 0
                && sh != null && sh.length() > 0) {

                int width = Integer.parseInt( sw );
                int height = Integer.parseInt( sh );

                if (width > 0 && height > 0 && sname.length() > 0) {

                    sgi.tbScheme.width = width;
                    sgi.tbScheme.height = height;
                    sgi.tbScheme.name = sname;
                    sgi.isScreenResSet = true;
                    //------------------------start--添加的发送大屏点数的代码-------
                    Byte[] param = new Byte[4];

                    param[0] = ((byte)(width >>8));
                    param[1] = ((byte)(width & 0xff));
                    param[2] = ((byte)(height >>8));
                    param[3] = ((byte)(height & 0xff));

                    screenW = width;
                    screenH = height;

                    TB3531.writeAsync(TB3531.EVENT_GROUP_ID_SCHEME,
                            TB3531.EVENT_SCHEME_ID_SET_BIGSCREEM,param);

                    //-------------------------end----------------------------------
                    ret = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return ret;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pre:
                goToOtherPage(SettingHomeActivity.class);//转到设置页面
                finish();
                break;
            case R.id.next:
                if(setInfo())
                {
                    goToOtherPage( SettingGuide2Activity.class );
                }
                else
                {
                    Toast.makeText(this,"设置参数无效",Toast.LENGTH_SHORT ).show();
                }
                break;

        }
    }
}