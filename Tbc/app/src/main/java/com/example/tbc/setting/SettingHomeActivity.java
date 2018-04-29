package com.example.tbc.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tbc.R;
import com.example.tbc.page.videoout.PageVideoOutInfo;
import com.example.tbc.setting.system.SettingSystemActivity;


public class SettingHomeActivity extends Activity implements View.OnClickListener{

    public final static String TAG = "SettingHome";

    /*
    @Override
    public void onBackPressed() {
        goToOtherPage(SettingGuide2Activity.class);
    }

    void goToOtherPage(Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(BluetoothSchemeActivity.this, cls);
        startActivity(intent);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //getActionBar().setTitle( R.string.title_devices);

        setContentView(R.layout.activity_setting_home);
    }

    void goToOtherPage(Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(SettingHomeActivity.this, cls);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_operating:
            {
                //进入操作页面
                goToOtherPage(SettingSystemActivity.class);
                break;
            }
            case R.id.read_statius:
            {
                goToOtherPage(PageVideoOutInfo.class);
                break;
            }

            case R.id.bigScreen:
               goToOtherPage( SettingBigScreen.class);
                break;
            default:
                break;
        }
    }
}