package com.example.tbc.setting.system;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.tbc.R;
import com.example.tbc.bluetooth.BluetoothDataService;
import com.example.tbc.protocal.TB3531;
import com.example.tbc.setting.SettingDebugActivity;
import com.example.tbc.setting.SettingHomeActivity;



/*
操作界面
* */

public class SettingSystemActivity extends Activity implements View.OnClickListener{

    public final static String TAG = "SettingHome";

    private  static  int  sencesClick1= 0;//点击3次进入修改页面，点击1次发信息到蓝牙模块
    private  static  int  sencesClick2 = 0;
    private  static  int  sencesClick3 = 0;
    private  static  int  sencesClick4 = 0;
    private  static  int  sencesClick5 = 0;
    private  static  int  sencesClick6 = 0;
    private  static  int  sencesClick7 = 0;
    private  static  int  sencesClick8 = 0;
    private  static  int  sencesClick9 = 0;

    Button  sencesButton1;
    Button  sencesButton2;
    Button  sencesButton3;
    Button  sencesButton4;
    Button  sencesButton5;
    Button  sencesButton6;
    Button  sencesButton7;
    Button  sencesButton8;
    Button  sencesButton9;

    String  sencesName;

//    @Override
//    public void onBackPressed() {
//       // goToOtherPage(SettingHomeActivity.class);
//        //finish();
//    }

    void goToOtherPage(Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(SettingSystemActivity.this, cls);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //getActionBar().setTitle( R.string.title_devices);
        //获取到sencesbutton的对象
        setContentView(R.layout.activity_setting_system);

        sencesButton1 = (Button) findViewById(R.id.button_scene1);
        sencesButton2 = (Button) findViewById(R.id.button_scene2);
        sencesButton3 = (Button) findViewById(R.id.button_scene3);
        sencesButton4 = (Button) findViewById(R.id.button_scene4);
        sencesButton5 = (Button) findViewById(R.id.button_scene5);
        sencesButton6 = (Button) findViewById(R.id.button_scene6);
        sencesButton7 = (Button) findViewById(R.id.button_scene7);
        sencesButton8 = (Button) findViewById(R.id.button_scene8);
        sencesButton9 = (Button) findViewById(R.id.button_scene9);
    }

    protected void moDifySencesName(){
        //给方案改名字，界面刚开始加载时判断用户是否修改名字
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        if (pref != null)
        {
            int sencesIndex = pref.getInt("sencesIndex",0);
            sencesName = pref.getString("name","");

            Log.d("moDifySencesName",sencesName);

            if (1 == sencesIndex)
            {
                if (0 != sencesName.length())
                {
                    sencesButton1.setText(sencesName);
                }
            }
            else  if (2 == sencesIndex)
            {
                if (0 != sencesName.length())
                {
                    sencesButton2.setText(sencesName);
                }
            }
            else  if (3 == sencesIndex)
            {
                if (0 != sencesName.length())
                {
                    sencesButton3.setText(sencesName);
                }
            }
            else  if (4 == sencesIndex)
            {
                if (0 != sencesName.length())
                {
                    sencesButton4.setText(sencesName);
                }
            }
            else  if (5 == sencesIndex)
            {
                if (0 != sencesName.length())
                {
                    sencesButton5.setText(sencesName);
                }
            }
            else  if (6 == sencesIndex)
            {
                if (0 != sencesName.length())
                {
                    sencesButton6.setText(sencesName);
                }
            }
            else  if (7 == sencesIndex)
            {
                if (0 != sencesName.length())
                {
                    sencesButton7.setText(sencesName);
                }
            }
            else  if (8 == sencesIndex)
            {
                if (0 != sencesName.length())
                {
                    sencesButton8.setText(sencesName);
                }
            }
            else  if (9 == sencesIndex)
            {
                if (0 != sencesName.length())
                {
                    sencesButton9.setText(sencesName);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        moDifySencesName();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //给方案改名字,用户修改后返回
        moDifySencesName();
    }

    protected  void sendToBluetoothCommand()
    {
        int width = 200;
        int height = 100;

        Byte[] param = new Byte[4];

        param[0] = ((byte)(width >>8));
        param[1] = ((byte)(width & 0xff));
        param[2] = ((byte)(height >>8));
        param[3] = ((byte)(height & 0xff));

        //先蓝牙发送数据,参数在param 中存储，4个字节
        TB3531.writeAsync(TB3531.EVENT_GROUP_ID_SCHEME,
                TB3531.EVENT_SCHEME_ID_SET_BIGSCREEM,param);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_scene1:
            {
                sencesClick1++;
                if (1 == sencesClick1)
                {
                    //向蓝牙发送指令
                    sendToBluetoothCommand();
                   // BluetoothDataService.write("test .....");
                }
                else if (3 == sencesClick1)
                {
                    Log.d("onClick fun","three clicks");
                    //进行场景名称的修改
                    Intent intent = new Intent(SettingSystemActivity.this, SettingDebugActivity.class);
                    intent.putExtra("sences",1);
                    startActivity(intent);
                    sencesClick1 = 0;
                }
                break;
            }
            case R.id.button_scene2:
            {
                sencesClick2++;
                if (1 == sencesClick2)
                {
                    //向蓝牙发送指令
                }
                else if (3 == sencesClick2)
                {
                    Log.d("onClick fun","three clicks");
                    //进行场景名称的修改
                    Intent intent = new Intent(SettingSystemActivity.this, SettingDebugActivity.class);
                    intent.putExtra("sences",2);
                    startActivity(intent);
                    sencesClick2 = 0;
                }
                break;
            }
            case R.id.button_scene3:
            {
                sencesClick3++;
                if (1 == sencesClick3)
                {
                    //向蓝牙发送指令
                }
                else if (3 == sencesClick3)
                {
                    Log.d("onClick fun","three clicks");
                    //进行场景名称的修改
                    Intent intent = new Intent(SettingSystemActivity.this, SettingDebugActivity.class);
                    intent.putExtra("sences",3);
                    startActivity(intent);
                    sencesClick3 = 0;
                }
                break;
            }
            case R.id.button_scene4:
            {
                sencesClick4++;
                if (1 == sencesClick4)
                {
                    //向蓝牙发送指令
                }
                else if (3 == sencesClick4)
                {
                    Log.d("onClick fun","three clicks");
                    //进行场景名称的修改
                    Intent intent = new Intent(SettingSystemActivity.this, SettingDebugActivity.class);
                    intent.putExtra("sences",4);
                    startActivity(intent);
                    sencesClick4 = 0;
                }
                break;
            }
            case R.id.button_scene5:
            {
                sencesClick5++;
                if (1 == sencesClick5)
                {
                    //向蓝牙发送指令
                }
                else if (3 == sencesClick5)
                {
                    Log.d("onClick fun","three clicks");
                    //进行场景名称的修改
                    Intent intent = new Intent(SettingSystemActivity.this, SettingDebugActivity.class);
                    intent.putExtra("sences",5);
                    startActivity(intent);
                    sencesClick5 = 0;
                }
                break;
            }
            case R.id.button_scene6:
            {
                sencesClick6++;
                if (1 == sencesClick6)
                {
                    //向蓝牙发送指令
                }
                else if (3 == sencesClick6)
                {
                    Log.d("onClick fun","three clicks");
                    //进行场景名称的修改
                    Intent intent = new Intent(SettingSystemActivity.this, SettingDebugActivity.class);
                    intent.putExtra("sences",6);
                    startActivity(intent);
                    sencesClick6 = 0;
                }
                break;
            }
            case R.id.button_scene7:
            {
                sencesClick7++;
                if (1 == sencesClick7)
                {
                    //向蓝牙发送指令
                }
                else if (3 == sencesClick7)
                {
                    Log.d("onClick fun","three clicks");
                    //进行场景名称的修改
                    Intent intent = new Intent(SettingSystemActivity.this, SettingDebugActivity.class);
                    intent.putExtra("sences",7);
                    startActivity(intent);
                    sencesClick7 = 0;
                }
                break;
            }
            case R.id.button_scene8:
            {
                sencesClick8++;
                if (1 == sencesClick8)
                {
                    //向蓝牙发送指令
                }
                else if (3 == sencesClick8)
                {
                    Log.d("onClick fun","three clicks");
                    //进行场景名称的修改
                    Intent intent = new Intent(SettingSystemActivity.this, SettingDebugActivity.class);
                    intent.putExtra("sences",8);
                    startActivity(intent);
                    sencesClick8 = 0;
                }
                break;
            }
            case R.id.button_scene9:
            {
                sencesClick9++;
                if (1 == sencesClick9)
                {
                    //向蓝牙发送指令
                }
                else if (3 == sencesClick9)
                {
                    Log.d("onClick fun","three clicks");
                    //进行场景名称的修改
                    Intent intent = new Intent(SettingSystemActivity.this, SettingDebugActivity.class);
                    intent.putExtra("sences",9);
                    startActivity(intent);
                    sencesClick9 = 0;
                }
                break;
            }
            default:
            {
                break;
            }
        }
    }
}



