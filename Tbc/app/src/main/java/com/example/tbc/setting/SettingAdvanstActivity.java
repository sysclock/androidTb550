/*************************************
    文件名称：SettingAdvanstActivity
    文件功能：设置大屏点数
    创建时间：
    修改时间：
**************************************/


package com.example.tbc.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.tbc.R;
import com.example.tbc.bluetooth.BluetoothSchemeActivity;
import com.example.tbc.setting.system.SettingSystemActivity;
import com.example.tbc.setting.system.SettingSystemFactoryActivity;
import com.example.tbc.setting.system.SettingSystemSetPassActivity;
import com.example.tbc.setting.system.SettingSystemUpgradeActivity;

import static com.example.tbc.bluetooth.BluetoothDiscoveryActivity.TAG;


public class SettingAdvanstActivity extends Activity implements View.OnClickListener{

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

        setContentView(R.layout.activity_setting_advanst);
    }

    void goToOtherPage(Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(SettingAdvanstActivity.this, cls);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

//            case R.id.timerPlan: //定时器页面
//                goToOtherPage( SettingTimerPlanActivity.class);
//                break;
//            case R.id.hotbak:
//                goToOtherPage( SettingHotBakActivity.class);
//                break;
//            case R.id.insertList:
//                goToOtherPage( SettingInsertListActivity.class);
//                break;
            case R.id.colorTest:
                goToOtherPage( SettingColorTestActivity.class);
                break;
            /*case R.id.log:
                goToOtherPage( SettingLogActivity.class);
                break;*/
            case R.id.passwordSet: //修改用户名和密码页面
                goToOtherPage( SettingSystemSetPassActivity.class);
                break;
            case R.id.upgrade:
                goToOtherPage( SettingSystemUpgradeActivity.class);
                break;
            case R.id.factory:
                goToOtherPage( SettingSystemFactoryActivity.class);
                break;
            case R.id.debugCmd:
                goToOtherPage( SettingDebugActivity.class);
                break;
            default:
                break;
        }
    }
}