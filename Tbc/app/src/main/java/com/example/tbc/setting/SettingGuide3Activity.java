
/*************************************
     文件名称：SettingGuide3Activity3
     文件功能：第三级设置页面
     创建时间：
     修改时间：
 **************************************/

package com.example.tbc.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tbc.R;
import com.example.tbc.bluetooth.BluetoothDiscoveryActivity;
import com.example.tbc.bluetooth.BluetoothSchemeActivity;
import com.example.tbc.protocal.TB3531;
import com.example.tbc.protocal.TbImageConponent;
import com.example.tbc.protocal.TbInputSource;
import com.example.tbc.protocal.TbInputSourceManager;
import com.example.tbc.protocal.TbScheme;

import java.util.ArrayList;

import static com.example.tbc.bluetooth.BluetoothDiscoveryActivity.TAG;


public class SettingGuide3Activity extends ListActivity implements View.OnClickListener{

    public final static String TAG = "SettingGuide3";

    SettingGuideInfo sgi;
    ImageView imageview;

    Paint paint;
    Bitmap bitmap;
    Canvas canvas;

    TbImageListAdapter tbImageListAdapter;

    int canvasLeft = 0;
    int canvasTop = 0;

    int canvasWidth = 500;
    int canvasHeight = 281;

    float rate = 1;

    Button button;

    TbInputSourceManager inputSourceManager;

    @Override
    public void onBackPressed() {
        goToOtherPage(SettingGuide2Activity.class);
    }

    void goToOtherPage(Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(SettingGuide3Activity.this, cls);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //getActionBar().setTitle( R.string.title_devices);

        setContentView(R.layout.activity_setting_guide3);

        sgi = SettingGuideInfo.getInstance();

        tbImageListAdapter = new TbImageListAdapter();
        setListAdapter(tbImageListAdapter);

        button = (Button)findViewById( R.id.finish );

        inputSourceManager = new TbInputSourceManager( TB3531.tbInputSources );

        initScale();

        initView();

        drawImages();

        TB3531.setDataHandler(handler);
    }

    @Override
    protected void onDestroy(){

        TB3531.setDataHandler(null);

        super.onDestroy();
    }
    
    public final static int CANVAS_COLOR = Color.LTGRAY;
    public final static int SCREEN_COLOR = Color.LTGRAY;

    public final static int ITEM_COLOR = 0x400000ff;
    public final static int ITEM_COLOR_STROKE = 0x40ff0000;

    void initScale(){

        int max = (sgi.tbScheme.width > sgi.tbScheme.height)?
                sgi.tbScheme.width:sgi.tbScheme.height;

        rate = (float)canvasWidth / max;

        Log.i(TAG,"rate=" + rate + "canvasWidth="
                + canvasWidth + ",canvasHeight=" + canvasHeight);
    }

    void drawImages(){

        drawCanvasBack();
        drawBigScreen();
        doDrawImages();
        update_display();
    }

    void drawCanvasBack(){
        paint.setColor( CANVAS_COLOR);
        canvas.drawRect(canvasLeft,canvasTop,canvasWidth,canvasHeight,paint);
    }

    void update_display(){
        imageview.setImageBitmap(bitmap);
    }

    void drawBigScreen(){

        paint.setColor( SCREEN_COLOR);

        float w = (float)(sgi.tbScheme.width * rate);
        float h = (float)(sgi.tbScheme.height * rate);

        float left = (float)(canvasLeft + (canvasWidth - w)/2);
        float top = (float)(canvasTop + (canvasHeight - h)/2);

        canvas.drawRect(0,0,w,h,paint);

        Log.i(TAG,"drawCanvasBack [" + left + "," + top + "," + w + "," + h + "]");

    }

    void doDrawImages(){

        ArrayList<TbImageConponent> tbImageConponent
                = tbImageListAdapter.getImageList();

        int count = tbImageConponent.size();

        for(int i = 0; i < count;i ++){
            drawImageItem(i,tbImageConponent.get(i));
        }
    }

    void drawImageItem(int i,TbImageConponent item){

        paint.setColor( ITEM_COLOR);

        float w = (float)(item.width * rate);
        float h = (float)(item.height * rate);

        float left = (float)(canvasLeft +(item.left * rate));
        float top = (float)(canvasTop + (item.top * rate));


        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(left,top,left + w -1,top + h - 1,paint);

        paint.setStyle( Paint.Style.STROKE);
        paint.setColor( ITEM_COLOR_STROKE);
        canvas.drawRect(left,top,left + w -1,top + h - 1,paint);
        Log.i(TAG,"draw item [" + left + "," + top + "," + w + "," + h + "]");
    }

    void initView(){
        //lcView; myView=new MyView(this);
        imageview = (ImageView)findViewById( R.id.lcView );

        paint = new Paint();
        paint.setColor( CANVAS_COLOR);

        //create a bitmap
        bitmap = Bitmap.createBitmap((int)canvasWidth,(int)canvasHeight, Bitmap.Config.ARGB_8888 );

        //from bitmap,create a canvas
        canvas = new Canvas(bitmap);
        canvas.drawColor(SCREEN_COLOR);

        //canvas.drawRect(canvasLeft,canvasTop,canvasWidth,canvasHeight,paint);

        /*
        String familyName = "宋体";
        Typeface font = Typeface.create(familyName, Typeface.BOLD);
        paint.setColor(Color.RED);
        paint.setTypeface(font);
        paint.setTextSize(20);
        canvas.drawText("写字...", 30, 30, paint);
        */

        //update_display();
    }

    boolean addImage(TbInputSource inputSource){

        boolean ret = false;

        EditText vleft = (EditText)findViewById( R.id.left );
        EditText vtop = (EditText)findViewById( R.id.top );
        EditText vwidth = (EditText)findViewById( R.id.width );
        EditText vheight = (EditText)findViewById( R.id.height );

        int bid = inputSource.boardId;
        int sid = inputSource.sourceId;

        int left = Integer.parseInt( vleft.getText().toString());
        int top = Integer.parseInt( vtop.getText().toString());
        int width = Integer.parseInt( vwidth.getText().toString());
        int height = Integer.parseInt( vheight.getText().toString());

        String hint = "添加画面成功";

        if(TbImageConponent.checkParam(tbImageListAdapter.getImageList(),
                bid,sid,left,top,width,height) == 0){
            TbImageConponent tbic = new TbImageConponent(bid,sid,left,top,width,height);
            tbImageListAdapter.addImage(tbic);
            tbImageListAdapter.notifyDataSetChanged();
            ret = true;

        }else{
            hint = "添加画面失败";
        }

        Toast.makeText( this,hint,Toast.LENGTH_SHORT ).show();

        return ret;
    }

    void setImageInfo(){

        ArrayList<TbImageConponent> tbImageConponent
                = tbImageListAdapter.getImageList();
        int num = tbImageConponent.size();

        sgi.tbScheme.tbImageConponent = new TbImageConponent[num];

        for(int i = 0;i < num; i ++){
            sgi.tbScheme.tbImageConponent[i] =
                    new TbImageConponent(tbImageConponent.get(i));
        }

        sgi.isImgCopSet = true;
    }



    Dialog dialog;

    private void hint(String title,String text){
        if(dialog != null){
            dialog.dismiss();
        }

        if(text != null) {
            dialog = android.app.ProgressDialog.show(this, title, text);
            View v = dialog.getWindow().getDecorView();

            setDialogText(v);
        }
    }

    private void setDialogText(View v) {

        if (v instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) v;
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt(i);
                setDialogText(child);
            }
        } else if (v instanceof TextView) {
            CharSequence title = ((TextView) v).getText();
            if(false){
                ((TextView) v).setTextSize(32);
                ((TextView) v).setTextColor(0xff0000ff);
            }else{
                ((TextView) v).setTextSize(24);
            }
        }
    }

    void saveScheme(){
        TB3531.saveScheme(sgi.tbScheme);
    }

    protected Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case TB3531.TB_BLUETOOTH_DATA_EVENT:
                    processData( (Byte[]) message.obj );
                    break;
                default:
                    Log.e( TAG, "Not support msg received" );
                    break;
            }
        }
    };

    public void processData(Byte[] data) {

        TB3531.logBuffer( data,data.length ,"read event data:");

        if(data[4] == (byte)TB3531.EVENT_GROUP_ID_SCHEME
                && data[5] == (byte)TB3531.EVENT_SCHEME_ID_SAVE_SCHEME){
            hint("",null);
            String ret = "失败";

            if(data[TB3531.EVENT_MSG_OFFSET] == 0){
                ret = "成功";

                int id = (data[TB3531.EVENT_MSG_OFFSET + 1] & 0xff);
                sgi.tbScheme.id = id;

                TB3531.tbSchemeManager.map.put(id, sgi.tbScheme );
                sgi.clear();

                goToOtherPage( SettingHomeActivity.class );

            }

            Log.i(TAG,"保存方案" + ret);

            Toast.makeText( this,"切换保存方案" + ret ,Toast.LENGTH_SHORT).show();
        }
    }

    ButtonOnClick buttonOnClick = new ButtonOnClick(0);

    private class ButtonOnClick implements DialogInterface.OnClickListener{

        private int index;

        public ButtonOnClick(int index){
            this.index = index;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {

            Log.e(TAG,"index=" + index);

            if (which >= 0){
                index = which;
            }else {

                if (which == DialogInterface.BUTTON_POSITIVE) {
                    TbInputSource[] noUseInputs = inputSourceManager.getNoUsedInputSource();

                    if(addImage(noUseInputs[index])) {
                        drawImages();
                        inputSourceManager.setUsed(noUseInputs[index].boardId,noUseInputs[index].sourceId);
                    }
                }
            }
        }
    }

    void doChooseInputSource(){

        String[] items = inputSourceManager.getNoUsedInputSourceNames();

        new AlertDialog.Builder(this).setTitle("请选择输入源").setSingleChoiceItems(
                items, 0, buttonOnClick)
                .setPositiveButton( "确定", buttonOnClick)
                .setNegativeButton("取消", null).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pre:
                goToOtherPage(SettingGuide2Activity.class);

                break;
            case R.id.add:
                doChooseInputSource();

                break;
            case R.id.finish:
                setImageInfo();
                saveScheme();
                hint("","正在切换并保存方案...");
                break;
        }
    }



    private class TbImageListAdapter extends BaseAdapter {
        private ArrayList<TbImageConponent> tbImageConponent;
        private LayoutInflater inflator;

        TbImageListAdapter() {
            super();
            tbImageConponent = new ArrayList<TbImageConponent>();
            inflator =SettingGuide3Activity.this.getLayoutInflater();
        }

        public ArrayList<TbImageConponent> getImageList(){
            return tbImageConponent;
        }

        public void addImage(TbImageConponent tbiconponent) {
            if (!tbImageConponent.contains(tbiconponent)) {
                tbImageConponent.add(tbiconponent);
            }else{
                Toast.makeText( SettingGuide3Activity.this,"添加对象已经存在",Toast.LENGTH_SHORT).show();
            }
        }

        public TbImageConponent getTbiConponent(int position) {
            return tbImageConponent.get(position);
        }

        public void clear() {
            tbImageConponent.clear();
        }

        @Override
        public int getCount() {
            return tbImageConponent.size();
        }

        @Override
        public Object getItem(int i) {
            return tbImageConponent.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;


            if (view == null) {
                view = inflator.inflate(R.layout.image_conponent, null);
                viewHolder = new ViewHolder();
                viewHolder.imgInfo = (TextView) view.findViewById(R.id.tbcInfo);

                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            TbImageConponent tbic = tbImageConponent.get(i);

            final String imgInfo = "画面"  + (i + 1) + "  [" + tbic.left + "," + tbic.top
                    + "," + tbic.width + ","  + tbic.height +"]";

            //Log.i(TAG,"count=" + getCount() +",imgInfo=" + imgInfo);

            viewHolder.imgInfo.setText(imgInfo);

            return view;
        }
    }

    static class ViewHolder {
        TextView imgInfo;
    }
}