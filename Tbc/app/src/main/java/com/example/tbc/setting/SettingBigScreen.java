/********************************
     文件名称：SettingBigScreen
     文件功能：设置大屏点数
     创建时间：
     修改时间：
 **********************************/

package com.example.tbc.setting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tbc.R;
import com.example.tbc.bluetooth.BluetoothSchemeActivity;
import com.example.tbc.protocal.TB3531;
import com.example.tbc.protocal.TbInputSourceManager;

import static com.example.tbc.bluetooth.BluetoothDiscoveryActivity.TAG;


public class SettingBigScreen extends Activity implements View.OnClickListener{

    public final static String TAG = "SettingBigScreen";

    EditText vw;  //大屏的宽度
    EditText vh;  //大屏的高度

    EditText oldPassWordEdit;
    EditText newPassWordEdit;
    EditText secondPassWordEdit;


    public static int screenW = 10000;
    public static int screenH = 10000;

    String oldPassWordString;
    String newPassWordString;
    String secondPassWordString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_bigscreen);

        oldPassWordEdit = (EditText) findViewById(R.id.oldpass);
        newPassWordEdit = (EditText) findViewById(R.id.newpass);
        secondPassWordEdit = (EditText) findViewById(R.id.newpassrep);

        vw = (EditText)findViewById( R.id.width );
        vh = (EditText)findViewById( R.id.height );

    }

    @Override
    protected void onResume() {

        TB3531.setDataHandler(handler);

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        TB3531.setDataHandler(null);
        super.onDestroy();
    }

    void goToOtherPage(Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(SettingBigScreen.this, cls);
        startActivity(intent);
    }

    protected void setPassWord(){
        //先获取原来的密码，如果为空不用输入
        oldPassWordString = oldPassWordEdit.getText().toString();
        newPassWordString = newPassWordEdit.getText().toString();
        secondPassWordString = secondPassWordEdit.getText().toString();

        //先判断里面的是否保存有原来的密码
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        String tmpPassWordString = pref.getString("password","");

        if (0 == newPassWordString.length())
        {
            Toast.makeText( this,"新密码输入不可为空，请重新输入",Toast.LENGTH_SHORT ).show();
            return;
        }
        else if (0 == secondPassWordString.length())
        {
            Toast.makeText( this,"再次输入密码不可为空，请重新输入",Toast.LENGTH_SHORT ).show();
            return;
        }

        if (0 != tmpPassWordString.length())
        {
            if (oldPassWordString.equals(tmpPassWordString))
            {
                if (newPassWordString.equals(secondPassWordString))
                {
                    Toast.makeText( this,"密码修改成功",Toast.LENGTH_SHORT ).show();
                    //保存用户修改的密码
                    SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                    editor.putString("password",newPassWordString);
                    editor.apply();
                }
                else
                {
                    Toast.makeText( this,"两次输入的密码不一致,请重新输入",Toast.LENGTH_SHORT ).show();
                    return;
                }
            }
            else
            {
                Toast.makeText( this,"原始密码输入错误，请重新输入",Toast.LENGTH_SHORT ).show();
                return;
            }
        }
        else
        {
            if (newPassWordString.equals(secondPassWordString))
            {
                Toast.makeText( this,"密码修改成功",Toast.LENGTH_SHORT ).show();
                //保存用户修改的密码
                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putString("password",newPassWordString);
                editor.apply();
            }
            else
            {
                Toast.makeText( this,"两次输入的密码不一致,请重新输入",Toast.LENGTH_SHORT ).show();
                return;
            }
        }
        //函数结束
    }

    boolean setInfo(){

        boolean ret = false;

        try {

            String sw = vw.getText().toString();
            String sh = vh.getText().toString();

            //宽度大于高度
            if(sw.length() > 0 && sh.length() > 0) {

                int width = Integer.parseInt( sw );
                int height = Integer.parseInt( sh );

                if (width > 0 && height > 0 ) {

                    Byte[] param = new Byte[4];

                    param[0] = ((byte)(width >>8));
                    param[1] = ((byte)(width & 0xff));
                    param[2] = ((byte)(height >>8));
                    param[3] = ((byte)(height & 0xff));

                    screenW = width;
                    screenH = height;

                    //先蓝牙发送数据,参数在param 中存储，4个字节
                    TB3531.writeAsync(TB3531.EVENT_GROUP_ID_SCHEME,
                            TB3531.EVENT_SCHEME_ID_SET_BIGSCREEM,param);

                    ret = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if(!ret){
            Toast.makeText( this,"设置参数无效",Toast.LENGTH_SHORT ).show();
        }

        return ret;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.submit:
                setInfo();
                setPassWord();
                break;

        }
    }

    //handler ???---收到蓝牙返回的数据，需要进行处理
    protected Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case TB3531.TB_BLUETOOTH_DATA_EVENT:
                    processData( (Byte[]) message.obj ); //对收到的数据进行处理
                    break;
                default:
                    Log.e( TAG, "Not support msg received" );
                    break;
            }
        }
    };

    //对蓝牙返回的数据进行解析
    public void processData(Byte[] data) {

        TB3531.logBuffer( data,data.length ,"read event data:");

        if(data[4] == (byte)TB3531.EVENT_GROUP_ID_SCHEME
                && data[5] == (byte)TB3531.EVENT_SCHEME_ID_SET_BIGSCREEM)
        {
            String ret;

            int err = data[TB3531.EVENT_MSG_OFFSET];
            if(err == 0)
            {
                ret = "成功";
            }
            else
            {
                ret = "失败，未知错误,err=" + err;
            }

            Toast.makeText( this,"设置大屏点数：" + ret ,Toast.LENGTH_SHORT).show();
        }
    }

}