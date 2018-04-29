package com.example.tbc.setting;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tbc.R;
import com.example.tbc.bluetooth.BluetoothSchemeActivity;
import com.example.tbc.protocal.TbScreenConponent;

import static com.example.tbc.bluetooth.BluetoothDiscoveryActivity.TAG;


public class SettingGuide2Activity extends Activity implements View.OnClickListener{

    public final static String TAG = "SettingGuide2";
    public final static int MAX_ROWS = 4;
    public final static int MAX_COLS = 4;

    EditText etRows;
    EditText etCols;

    int rows;
    int cols;

    SettingGuideInfo sgi;
    /*
    @Override
    public void onBackPressed() {
        goToOtherPage(SettingGuide1Activity.class);

    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //getActionBar().setTitle( R.string.title_devices);

        setContentView(R.layout.activity_setting_guide2);

        sgi = SettingGuideInfo.getInstance();

        etRows  = (EditText)findViewById( R.id.rows );
        etCols  = (EditText)findViewById( R.id.cols );

        etRows.addTextChangedListener(textWatcherRows);
        etCols.addTextChangedListener(textWatcherCols);

        if(sgi.isScreenCopSet){

            rows = sgi.tbScheme.tbScreen.length;
            cols = sgi.tbScheme.tbScreen[0].length;

            Log.e(TAG,"-->resume from sgi rows=" + rows + ",cols=" + cols);

            etRows.setText( "" + rows );
            etCols.setText( "" + cols );

            Log.e(TAG,"resume from sgi rows=" + rows + ",cols=" + cols);

        }else {
            rows = Integer.parseInt( etRows.getText().toString() );
            cols = Integer.parseInt( etCols.getText().toString() );

        }

        initScrenConponentWH();

        display(rows,cols);

    }

    void display(int rows,int cols){
        int i;
        int j;

        for(j = 0; j < MAX_ROWS;j++){
            for(i = 0;i < MAX_COLS;i++){

                String idName = ("item" + i) + j;

                LinearLayout linearLayout = (LinearLayout)findViewByName(idName);
                if(linearLayout != null){
                    if( i < cols && j < rows){
                        linearLayout.setVisibility( View.VISIBLE );
                        //Log.i(TAG,"set item visible. id=" + idName);
                    }else{
                        linearLayout.setVisibility( View.GONE );
                        //Log.i(TAG,"set item gone. id=" + idName);
                    }
                }else{
                    Log.e(TAG,"get item fail. id=" + idName);
                }
            }
        }
    }

    private View findViewByName(String name) {

        /*int view_id = R.id.class.getField("xxx").getInt(null);
        View view = findViewById(R.id.view_id);
        */
        int id = getResources().getIdentifier(name, "id",this.getPackageName());
        View view  = findViewById(id);
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.next:

                if(doCheckScrenConponent())
                {
                    doSaveScrenConponent();
                    goToOtherPage( SettingGuide3Activity.class );
                }
                else
                {
                    Toast.makeText( this,"请输入各个输入部分的宽高",Toast.LENGTH_LONG ).show();
                }

                break;
            case R.id.pre:
                goToOtherPage(SettingGuide1Activity.class);

                break;

        }
    }

    boolean doCheckScrenConponent(){

        boolean ret = true;

        int i;
        int j;


        for(j = 0; j < rows;j++) {
            for (i = 0; i < cols; i++) {

                EditText vw = (EditText)findViewByName(("w" + i) + j);
                EditText vh = (EditText)findViewByName(("h" + i) + j);

                String ws = vw.getText().toString();
                String wh = vh.getText().toString();

                if(ws != null && ws.length() > 0
                        && wh != null && wh.length() > 0) {
                    int w = Integer.parseInt( ws );
                    int h = Integer.parseInt( wh );

                    if (w <= 0 || h <= 0) {
                        ret = false;
                        break;
                    }
                }else{
                    ret = false;
                    break;
                }
            }
        }

        return ret;
    }

    void doSaveScrenConponent(){

        int i;
        int j;

        if(cols > 0 && rows > 0){

            Log.e(TAG,"doSaveScrenConponent rows=" + rows + ",cols=" + cols);

            sgi.tbScheme.tbScreen = new TbScreenConponent[rows][cols];

            Log.e(TAG,"sgi.tbScheme.tbScreen.length=" + sgi.tbScheme.tbScreen.length);

            for(j = 0; j < rows;j++) {

                sgi.tbScheme.tbScreen[j] = new TbScreenConponent[cols];

                for (i = 0; i < cols; i++) {

                    EditText vw = (EditText)findViewByName(("w" + i) + j);
                    EditText vh = (EditText)findViewByName(("h" + i) + j);

                    String ws = vw.getText().toString();
                    String wh = vh.getText().toString();

                    if(ws != null && ws.length() > 0
                            && wh != null && wh.length() > 0) {
                        int w = Integer.parseInt( ws );
                        int h = Integer.parseInt( wh );

                        sgi.tbScheme.tbScreen[j][i] = new TbScreenConponent(w,h);
                    }
                }
            }

            int temprows = sgi.tbScheme.tbScreen.length;
            int tempcols = sgi.tbScheme.tbScreen[0].length;

            Log.e(TAG,"--doSaveScrenConponent rows=" + temprows + ",cols=" + tempcols);


            sgi.isScreenCopSet = true;
        }


        return ;
    }

    void initScrenConponentWH(){

        int i;
        int j;

        if(cols > 0 && rows > 0){

            for(j = 0; j < rows;j++) {

                for (i = 0; i < cols; i++) {

                    EditText vw = (EditText)findViewByName(("w" + i) + j);
                    EditText vh = (EditText)findViewByName(("h" + i) + j);

                    if(sgi.isScreenCopSet){
                        vw.setText( "" +  sgi.tbScheme.tbScreen[i][j].width);
                        vh.setText( "" +  sgi.tbScheme.tbScreen[i][j].height);
                    }else {
                        vw.setText( "1920" );
                        vh.setText( "1080" );
                    }
                }
            }
        }

        return ;
    }

    void goToOtherPage(Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(SettingGuide2Activity.this, cls);
        startActivity(intent);
    }

    TextWatcher textWatcherRows = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            Log.i( TAG, "start=" + start + ",before=" + before + ",count=" + count );

            if (count > 0) {

                String sr = etRows.getText().toString();


                int tempRows = Integer.parseInt( sr );

                if (tempRows <= 0) {
                    etRows.setText( "" + 1 );
                }

                if (tempRows > MAX_ROWS) {
                    etRows.setText( "" + MAX_ROWS );
                }


                Log.i( TAG, "[" + rows + "," + cols + " ] --->" );

                sr = etRows.getText().toString();


                tempRows = Integer.parseInt( sr );

                if (rows != tempRows) {
                    rows = Integer.parseInt( etRows.getText().toString() );
                }

                Log.i( TAG, "--->[" + rows + "," + cols + " ]" );

                display( rows, cols );

                initScrenConponentWH();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {


        }
    };

    TextWatcher textWatcherCols = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            Log.i( TAG, "start=" + start + ",before=" + before + ",count=" + count );

            if (count > 0) {

                String sc = etCols.getText().toString();

                int tempCols = Integer.parseInt( sc );


                if (tempCols <= 0) {
                    etCols.setText( "" + 1 );
                }

                if (tempCols > MAX_COLS) {
                    etCols.setText( "" + MAX_COLS );
                }

                Log.i( TAG, "[" + rows + "," + cols + " ] --->" );

                sc = etCols.getText().toString();

                tempCols = Integer.parseInt( sc );

                if (cols != tempCols) {
                    cols = Integer.parseInt( etCols.getText().toString() );
                }

                Log.i( TAG, "--->[" + rows + "," + cols + " ]" );

                display( rows, cols );
                initScrenConponentWH();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}