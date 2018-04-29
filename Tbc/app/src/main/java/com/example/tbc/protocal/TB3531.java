package com.example.tbc.protocal;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.example.tbc.bluetooth.BluetoothCom;
import com.example.tbc.bluetooth.BluetoothDataService;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jah on 16-11-12.
 */

public class TB3531 {

    public final static String TAG = "TB3531";

    public static int BOARD_NUM = 4;
    public static int PORT_NUM = 4;

    public final static int TB_BLUETOOTH_DATA_EVENT = 100;
    public final static int TB_BLUETOOTH_LOG_EVENT = 101;

    /**/

    public final static int EVENT_BASE_LEN = 8; /*3 + 4 + 1*/
    public final  static int GROUP_ID_OFFSET = 4;
    public final  static int EVENT_ID_OFFSET = 5;
    public final  static int EVENT_MSG_OFFSET = 7;
    /* event source*/
    public final static int EVENT_SROURE_ID_UP_MACHINE = 0;
    public final static int EVENT_SROURE_ID_FRONTBORAD = 1;
    public final static int EVENT_SROURE_ID_TB3531 = 2;

    /* event group id*/
    public final static int EVENT_GROUP_ID_NOTIFY = 0;
    public final static int EVENT_GROUP_ID_VIDEO = 0x01; /*视频输出*/
    public final static int EVENT_GROUP_ID_DISPLAY = 0x02; /*图像质量*/
    public final static int EVENT_GROUP_ID_SUBPIC = 0x03; /*子画面*/
    public final static int EVENT_GROUP_ID_SCENE = 0x04; /*场景*/
    public final static int EVENT_GROUP_ID_SOUND = 0x05; /*声音*/
    public final static int EVENT_GROUP_ID_OSD = 0x06; /*OSD*/
    public final static int EVENT_GROUP_ID_LED = 0x07; /*LED*/
    public final static int EVENT_GROUP_ID_SYSTEM = 0x08; /*系统*/

    public final static int EVENT_GROUP_ID_SCHEME = 0x0a; /*方案*/
    public final static int EVENT_GROUP_ID_TIMER = 0x0b; /*定时计划*/
    public final static int EVENT_GROUP_ID_HOTBAK = 0x0c; /*热备份*/
    public final static int EVENT_GROUP_ID_INSERT_PLAY = 0x0d; /*插播*/
    public final static int EVENT_GROUP_ID_TEST = 0x0e; /*图卡测试*/
    public final static int EVENT_GROUP_ID_PARAM = 0x0f; /*参数获取和设置*/
    public final static int EVENT_GROUP_ID_TRIAL_VERSION = 0x10; /*试用版*/
    public final static int EVENT_GROUP_ID_LOG = 0x11; /*LOG*/


    /*notify 0*/
    public final static int EVENT_NOTIFY_ID_HEARTBEAT = 0x01;
    public final static int EVENT_NOTIFY_ID_DVI_UNLINK = 0x02;
    public final static int EVENT_NOTIFY_ID_DVI_LINK = 0x03;
    public final static int EVENT_NOTIFY_ID_CONTROL_BY_UP = 0x04;
    public final static int EVENT_NOTIFY_ID_FREE_FROM_UP = 0x05;
    public final static int EVENT_NOTIFY_ID_REBOOT_OK = 0x06;
    public final static int EVENT_NOTIFY_ID_LOG = 0x07;

    /*video out 1*/
    public final static int EVENT_VIDEO_ID_SET_RESOLUTION = 0x01;/*分辨率*/
    public final static int EVENT_VIDEO_ID_SET_FRAMERATE = 0x02;/*帧率*/
    public final static int EVENT_VIDEO_ID_GET_INFO = 0x03;/*获取输出信息*/

    /*image qulity 2*/
    public final static int EVENT_DISPLAY_ID_SET_CONTRAST = 0x01; /*对比度*/
    public final static int EVENT_DISPLAY_ID_SET_SATURATION = 0x02;/*饱和度*/
    public final static int EVENT_DISPLAY_ID_SET_BRIGHTNESS = 0x03;/*亮度*/
    public final static int EVENT_DISPLAY_ID_GET_QULITY = 0x04;/*获取图像质量*/

    /*subpicture 3*/
    public final static int EVENT_SUBPIC_NEXT = 0x01; /*上一个子画面*/
    public final static int EVENT_SUBPIC_PRE = 0x02; /*下一个子画面*/
    public final static int EVENT_SUBPIC_GET = 0x03; /*获取当前子画面编号*/

    /*scene 4*/
    public final static int EVENT_SCENE_ID_SET = 0x01; /*场景切换*/
    public final static int EVENT_SCENE_ID_TAKE = 0x02; /*场景*/
    public final static int EVENT_SCENE_ID_GET = 0x03; /*场景获取*/
    public final static int EVENT_SCENE_ID_SWITH_ROOM = 0x04; /*开关局部放大*/
    public final static int EVENT_SCENE_ID_GET_ROOM = 0x05; /*获取局部放大*/
    public final static int EVENT_SCENE_ID_SWITH_P2P = 0x06; /*开关点对点*/
    public final static int EVENT_SCENE_ID_GET_P2P = 0x07; /*获取点对点*/
    public final static int EVENT_SCENE_ID_GET_INFO = 0x08; /*查询点对点*/
    public final static int EVENT_SCENE_ID_ADD_SCENE = 0x09; /*增加场景*/

    /*sound 5*/
    public final static int EVENT_SOUND_ID_SET_VOLUME = 0x01; /*设置音量*/
    public final static int EVENT_SOUND_ID_GET_VOLUME = 0x02; /*获取音量*/

    /*OSD 6*/
    public final static int EVENT_OSD_ID_SET_GENERAL = 0x01; /*通用界面消息*/
    public final static int EVENT_OSD_ID_PLAY = 0x02; /*播放控制*/

    /*LED 7*/
    public final static int EVENT_LED_ID_SET_RESOLUTION = 0x01; /*设置分辨率*/
    public final static int EVENT_LED_ID_GET_RESOLUTION = 0x02; /*获取分辨率*/

    /*SYSTEM 8*/
    public final static int EVENT_SYSTEM_ID_CONTROL_HEARTBEAT = 0x01; /*控制心跳*/
    public final static int EVENT_SYSTEM_ID_HARDREBOOT = 0x02; /*软件重启*/
    public final static int EVENT_SYSTEM_ID_SOFTREBOOT = 0x03; /*硬件重启*/
    public final static int EVENT_SYSTEM_ID_UPGRADE = 0x04; /*升级*/
    public final static int EVENT_SYSTEM_ID_FACTORY = 0x05; /*出厂设置*/
    public final static int EVENT_SYSTEM_ID_CHECK_PIN = 0x06; /*校验密码*/
    public final static int EVENT_SYSTEM_ID_SET_PIN = 0x07; /*设置密码*/

    /*SCHEME 0x0a*/
    public final static int EVENT_SCHEME_ID_GET_SRC_LIS = 0x01; /*获取源列表*/
    public final static int EVENT_SCHEME_ID_GET_SCHEME_DES = 0x02; /*获取方案描述*/
    public final static int EVENT_SCHEME_ID_GET_SCHEME = 0x03; /*获取方案*/
    public final static int EVENT_SCHEME_ID_SAVE_SCHEME = 0x04; /*存储方案*/
    public final static int EVENT_SCHEME_ID_SWITCH_SCHEME = 0x05; /*切换方案*/
    public final static int EVENT_SCHEME_ID_SET_BIGSCREEM = 0x06; /*切换方案*/
    public final static int EVENT_SCHEME_ID_SET_IMAGES = 0x07; /*切换方案*/

    /*Timer 0x0a*/
    public final static int EVENT_TIMER_ID_GET_TIMER = 0x01; /*定时切换*/
    public final static int EVENT_TIMER_ID_SET_TIMER = 0x02; /*定时切换*/

    /*hotbak 0x0c*/
    public final static int EVENT_HOTBAK_ID_GET = 0x01; /*设置热播列表*/
    public final static int EVENT_HOTBAK_ID_SET = 0x02; /*获取热播列表*/


    /*insert play 0x0d*/
    public final static int EVENT_INSERT_ID_GET = 0x01; /*插播设置*/
    public final static int EVENT_INSERT_ID_SET = 0x02; /*插播列表获取*/

    /*test 0x0d*/
    public final static int EVENT_TEST_COLOR = 0x01; /*图卡测试*/


    /*test 0x10*/
    public final static int EVENT_TRIAL_VERSION_GET = 0x01; /*获取试用信息*/
    public final static int EVENT_TRIAL_VERSION_SET = 0x02; /*设置试用信息*/
    public final static int EVENT_TRIAL_VERSION_CLEAR = 0x03; /*解除试用*/

    /*test 0x11*/
    public final static int EVENT_LOG_GET_ITEM = 0x01; /*获取信息*/
    public final static int EVENT_LOG_SET = 0x02; /*打开，关闭*/
    public final static int EVENT_LOG_CLAER = 0x03; /*清除信息*/
    public final static int EVENT_LOG_GET_STATUS = 0x04; /*获取状态*/


    public static TbInputSource[] tbInputSources;
    public static TbSchemeManager tbSchemeManager;

    private static boolean writeAsyncFlag = true;

    private volatile  static List<Byte[]> event = new LinkedList<Byte[]>();

    private volatile static Handler notifyHandler;

    public volatile  static StringBuffer logBuf = new StringBuffer();

    public static class EventInfo{

        public int sourceId;
        public int EventGroupID;
        public int eventId;
        public int reserve;

        public Byte[] event;
    }

    public static void setDataHandler(Handler handler){
        notifyHandler = handler;
    }


    private static EventInfo doParsePackage(Byte[] resp){

        EventInfo ret = null;

        try{
            if(resp[0] == 0xAA && resp[1] == 0xBB ){

                int msgLen = (resp[2] & 0xff);
                int sum = (resp[3 + msgLen]) & 0xff;

                if(msgLen >= 3 && checkSum(resp,msgLen,sum)){

                    ret = new EventInfo();

                    ret.sourceId = resp[3];
                    ret.EventGroupID = resp[4];
                    ret.eventId = resp[5];
                    ret.reserve = resp[6];

                    ret.event = new Byte[msgLen - 4];

                    System.arraycopy(resp,7,ret.event,0,msgLen - 4);

                }else{
                    Log.e("","checkSum fail");
                }
            }else{
                Log.e("","bad respmsg");
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return ret;
    }

    public static EventInfo parsePackage(Byte[] resp){
        return doParsePackage(resp);
    }

    private static boolean checkSum(Byte[] resp,int msgLen,int checkSum){

        int sum = 0;

        for(int i = 3;i< 3 + msgLen;i++){
            sum += (resp[i] & 0xff);
        }

        return sum == checkSum;
    }

    private static void initReq(Byte[] req,int eventLen,
            int source,int eventGroupId,int eventId){

        req[0] = (byte)0xAA;
        req[1] = (byte)0xBB;
        req[2] = (byte)(eventLen & 0xff);

        req[3] = (byte)(source & 0xff);
        req[4] = (byte)(eventGroupId & 0xff);
        req[5] = (byte)(eventId & 0xff);
        req[6] = (byte)0;
    }

    private static void calcCheckSum(Byte[] req,int len){

        int sum = 0;

        for(int i = 3; i < len - 1;i ++){
            Log.i(TAG,"i=" + i + ",len=" + len);
            sum += (req[i] & 0xff);
        }

        req[len - 1] = (byte)(sum & 0xff);
    }

    private static int version = 0x0101;

    public static byte[] packHeartBeat(){
        Byte[] pack = packEvent(0x00,0x01,(byte)((version >> 8) & 0xff),(byte)((version) & 0xff));
        return getByte(pack);
    }

    public static Byte[] packEvent(int groupId,int event){

        int len = EVENT_BASE_LEN;
        Byte[] req = new Byte[len];

        initReq(req,len - 4,EVENT_SROURE_ID_UP_MACHINE,groupId,event);

        calcCheckSum(req,len);

        return req;
    }

    public static Byte[] packEvent(int groupId,int event,Byte[] msg){

        int len = EVENT_BASE_LEN + msg.length;
        Byte[] req = new Byte[len];

        initReq(req,len - 4,EVENT_SROURE_ID_UP_MACHINE,groupId,event);

        Log.e(TAG,"msg.length=" + msg.length + ",len=" + len);

        for(int i = 0; i < msg.length;i++){
            req[7 + i] = msg[i];
        }

        //System.arraycopy(msg,0,req,7,msg.length);

        calcCheckSum(req,len);

        return req;
    }

    public static Byte[] packEvent(int groupId,int event,byte msg1){

        int len = EVENT_BASE_LEN + 1;
        Byte[] req = new Byte[len];

        initReq(req,len - 4,EVENT_SROURE_ID_UP_MACHINE,groupId,event);

        req[7] = msg1;

        calcCheckSum(req,len);

        return req;
    }

    public static Byte[] packEvent(int groupId,int event,byte msg1,byte msg2){

        int len = EVENT_BASE_LEN + 2;
        Byte[] req = new Byte[len];

        initReq(req,len - 4,EVENT_SROURE_ID_UP_MACHINE,groupId,event);

        req[7] = msg1;
        req[8] = msg2;

        calcCheckSum(req,len);

        return req;
    }

    public static Byte[] packEvent(int groupId,int event,byte msg1,byte msg2,byte msg3){

        int len = EVENT_BASE_LEN + 3;
        Byte[] req = new Byte[len];

        initReq(req,len - 4,EVENT_SROURE_ID_UP_MACHINE,groupId,event);

        req[7] = msg1;
        req[8] = msg2;
        req[9] = msg3;

        calcCheckSum(req,len);

        return req;
    }

    public static String getHexString( byte[] b) {

        String ret = "";

        for (int i = 0; i < b.length; i++) {

            String hex = Integer.toHexString(b[i] & 0xFF);

            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            ret += "0x" + hex + " ";
        }

        return ret;
    }


    public static String getHexString( Byte[] b) {

        String ret = "";

        for (int i = 0; i < b.length; i++) {

            String hex = Integer.toHexString(b[i] & 0xFF);

            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += "0x" + hex + " ";
        }

        return ret;
    }

    public static void logBuffer(Byte[] arr,int len,String tag){

        Byte[] bt = new Byte[len];

        for(int i = 0; i < len; i ++){
            bt[i] = arr[i];
        }

        Log.i(TAG,tag + ": len=" + len + ",arr=" + getHexString(bt));
    }

    public static void logBuffer(byte[] arr,int len,String tag){

        Byte[] bt = new Byte[len];

        for(int i = 0; i < len; i ++){
            bt[i] = arr[i];
        }

        Log.i(TAG,tag + ": len=" + len + ",arr=" + getHexString(bt));
    }


    private final static int MAX_NUM = 4096;

    private static int count = 0;
    private static byte[] buffer = new byte[MAX_NUM];

    public static void preparePushData(){
        count = 0;
    }

    public static boolean checkSum(byte[] buf,int start,int len,byte sum){

        boolean ret = true;

        //todo

        return ret;
    }

    public static boolean checkValidPackage(byte[] buf,int len){
        boolean ret = false;

        int msgLen = (buf[2] & 0xff);

        if(buf[0] == (byte)0xaa && buf[1] == (byte)0xbb
                && len >= (msgLen + 4)){
            ret = checkSum(buf,3,msgLen,buf[msgLen + 3]);
            if(!ret){
                Log.i(TAG,"CheckValidPackage checkSum fail.");
            }
        }else{
            //Log.i(TAG,"CheckValidPackage pack not receive end.");
        }

        return ret;
    }


    public static void moveOutEvent(int eventLen){

        for(int i = 0; i < (count - eventLen);i++){
            buffer[i] = buffer[i + eventLen];
        }

        count -= eventLen;
    }

    public static void pushData(byte[] data,int len){

        int eventLen;

        System.arraycopy( data,0,buffer,count,len );
        count += len;

        if(count == 42){
            Log.i(TAG,"");
        }
        if(checkValidPackage(buffer,count)){
            eventLen = (buffer[2] & 0xff) + 4;
            parsePackage(buffer,eventLen);
            TB3531.logTime("read");
            moveOutEvent(eventLen);
        }else{

        }
    }


    public static void parsePackage(byte[] arr,int len){

        if((arr[4] & 0xff) == EVENT_GROUP_ID_NOTIFY
            /*&& (arr[4] & 0xff) == EVENT_NOTIFY_ID_LOG*/){

            if((arr[5] & 0xff) == EVENT_NOTIFY_ID_LOG){
                saveLog(arr,8,(int)arr[7]);
            }else{
                Log.i(TAG,"msg:" + arr[5]);
            }
        }else {
            Log.i(TAG,"parse a event and add to list.");
            if(writeAsyncFlag){
                //nofity.processData(getByte( arr, len ));
                if(notifyHandler != null) {
                    Message msg = notifyHandler.obtainMessage();

                    msg.what = TB_BLUETOOTH_DATA_EVENT;
                    msg.obj = getByte( arr, len );

                    notifyHandler.sendMessage(msg);
                }

            }else {
                event.add( getByte( arr, len ) );
            }
        }
    }

    public static void saveLog(byte[] arr,int pos,int len){

        byte[] logBytes = new byte[len];

        System.arraycopy( arr,pos,logBytes,0,len );

        String log =  new String(logBytes);

        Log.i(TAG,"remote_log:" + log);

        logBuf.append( log );

        if(notifyHandler != null) {

            Message msg = notifyHandler.obtainMessage();

            msg.what = TB_BLUETOOTH_LOG_EVENT;
            msg.obj = log;

            notifyHandler.sendMessage(msg);
        }
    }

    public static byte[] getByte(Byte[] arr){

        byte[] bt = new byte[arr.length];

        for(int i = 0;i < arr.length;i++){
            bt[i] = arr[i];
        }

        return bt;
    }

    public static Byte[] getByte(byte[] arr){

        Byte[] bt = new Byte[arr.length];

        for(int i = 0;i < arr.length;i++){
            bt[i] = arr[i];
        }

        return bt;
    }

    public static Byte[] getByte(byte[] arr,int len){

        Byte[] bt = new Byte[len];

        for(int i = 0;i < len;i++){
            bt[i] = arr[i];
        }

        return bt;
    }

    public static int writeAsync(final Byte[] arr){
        return BluetoothDataService.write( getByte(arr) );
    }

    public static Byte[] write(final Byte[] arr){

        Byte[] ret = null;

        BluetoothDataService.write( getByte(arr) );

        long pre = (new Date()).getTime();

        while(true){

            if(!event.isEmpty()) {

                Byte[] temp = event.get(0);

                Log.i( TAG, "event come: " + temp[GROUP_ID_OFFSET] + "," + temp[EVENT_ID_OFFSET] );

                event.remove( 0 );

                if (temp[GROUP_ID_OFFSET].equals(arr[GROUP_ID_OFFSET])
                        && temp[EVENT_ID_OFFSET].equals(arr[EVENT_ID_OFFSET])) {
                    ret = temp;
                    Log.d(TAG,"call return");
                    break;
                }else{
                    Log.i(TAG,"Get a event form list. but not the callers's.");
                }
            }else{

                long cur = (new Date()).getTime();
                if(cur - pre > 5000 ){
                    Log.d(TAG,"call timeout");
                    break;
                }

                try {
                    Thread.sleep( 100 );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if(ret != null){
            logBuffer(ret,ret.length,"");
        }
        return ret;
    }

    public static Byte[] write(int groupId,int event){
        return write(packEvent(groupId,event));
    }

    public static int writeAsync(int groupId,int event){
        return writeAsync(packEvent(groupId,event));
    }

    public static Byte[] write(int groupId,int event,Byte[] msg){
        return write(packEvent(groupId,event,msg));
    }

    public static int writeAsync(int groupId,int event,Byte[] msg){
        return writeAsync(packEvent(groupId,event,msg));
    }

    public static Byte[] write(int groupId,int event,byte msg1){
        return write(packEvent(groupId,event,msg1));
    }

    public static int writeAsync(int groupId,int event,byte msg1){
        return writeAsync(packEvent(groupId,event,msg1));
    }

    public static Byte[] write(int groupId,int event,byte msg1,byte msg2){
        return write(packEvent(groupId,event,msg1,msg2));
    }

    public static int writeAsync(int groupId,int event,byte msg1,byte msg2){
        return writeAsync(packEvent(groupId,event,msg1,msg2));
    }

    public static Byte[] write(int groupId,int event,byte msg1,byte msg2,byte msg3){
        return write(packEvent(groupId,event,msg1,msg2,msg3));
    }

    public static int writeAsync(int groupId,int event,byte msg1,byte msg2,byte msg3){
        return writeAsync(packEvent(groupId,event,msg1,msg2,msg3));
    }

    public static TbInputSource[] parseSrcList(Byte[] pack) {

        TbInputSource[] tbInputSources = null;

        int off = TB3531.EVENT_MSG_OFFSET;
        int off2 = off + 2;
        int off3;

        if (pack[off] == 0) {

            int num = (pack[off + 1] & 0xff);

            if (num > 0) {
                tbInputSources = new TbInputSource[num];

                for (int i = 0; i < num; i++) {

                    tbInputSources[i] = new TbInputSource();

                    off3 = off2 + i * 7;

                    tbInputSources[i].boardId = (pack[off3]) & 0xff;
                    tbInputSources[i].sourceId = (pack[off3 + 1]) & 0xff;
                    tbInputSources[i].type = (pack[off3 + 2]) & 0xff;

                    /*Log.e(TAG,"source 3 value： " +  tbInputSources[i].boardId +
                        ","  +  tbInputSources[i].sourceId +
                        "," + +  tbInputSources[i].sourceId);
                    */

                    tbInputSources[i].width = (((pack[off3 + 3]) & 0xff) << 8)
                            | ((pack[off3 + 4]) & 0xff);
                    tbInputSources[i].height = (((pack[off3 + 5]) & 0xff) << 8)
                            | ((pack[off3 + 6]) & 0xff);
                }
            } else {
                Log.i( TAG, "parseSrcList num=" + num );
            }

        } else {
            Log.i( TAG, "parseSrcList ret=" + pack[off] );
        }

        return tbInputSources;
    }

    public static TbSchemeManager parseSchemeDes(Byte[] pack) {

        TbSchemeManager tbSchemeManager = null;

        int off = TB3531.EVENT_MSG_OFFSET;
        int off2 = off + 2;
        int off3;

        if (pack[off] == 0) {

            int curid = (pack[off + 1] & 0xff);
            int num = (pack[off + 2] & 0xff);

            if (num > 0) {
                tbSchemeManager = new TbSchemeManager();

                tbSchemeManager.curId = curid;
                tbSchemeManager.ids = new int[num];

                for (int i = 0; i < num; i++) {
                    tbSchemeManager.ids[i] = (pack[off + 3 + i] & 0xff);
                    Log.i( TAG, "parseSchemeDes id[" + i + "]=" + tbSchemeManager.ids[i] );
                }
            } else {
                Log.i( TAG, "parseSchemeDes num=" + num );
            }

        } else {
            Log.i( TAG, "parseSchemeDes ret=" + pack[off] );
        }

        return tbSchemeManager;
    }

    public static String getStringFromBytes(Byte[] pack,int start,int len){

        String ret = null;

        int i;
        int num;

        for (i = 0; i < len; i++) {
            if (pack[start + i] == 0) {
                break;
            }
        }

        num = i;

        byte[] temp = new byte[num];

        if (num > 0) {
            for (i = 0; i < num; i++) {
                temp[i] = pack[i + start];
            }

            try {
                ret = new String( temp,"UTF-8" );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(ret == null){
            ret = "";
        }

        return ret;
    }

    public static TbTimer[] parseTimer(Byte[] data){

        TbTimer[] ret = null;

        int off = TB3531.EVENT_MSG_OFFSET;

        int err = data[off];
        int num;
        int offItem;

        if(err == 0){

            num = data[off + 1 ];

            if(num > 0) {

                ret = new TbTimer[num];

                for (int i = 0; i < num; i++) {

                    ret[i] = new TbTimer();

                    offItem = TB3531.EVENT_MSG_OFFSET + 2 + (i * 24);

                    ret[i].name = getStringFromBytes( data,offItem,15);

                    offItem += 15;

                    ret[i].type = (data[offItem ++] & 0xff);
                    ret[i].schemeId = (data[offItem ++] & 0xff);

                    int year = ((data[offItem] & 0xff) << 8) +
                            ((data[offItem + 1]) & 0xff);
                    offItem += 2;
                    int month=  (data[offItem ++] & 0xff);
                    int day=  (data[offItem ++] & 0xff);

                    int hour=  (data[offItem ++] & 0xff);
                    int minute=  (data[offItem ++] & 0xff);
                    int second=  (data[offItem] & 0xff);

                    Log.i(TAG,"T " + ret[i].name + " "
                            + year + "-" + month + "-" + day + " "
                            + hour + ":" + minute +":" + second);

                    ret[i].calendar = new GregorianCalendar(year,month,day,hour,minute,second);

                }
            }
        }

        return ret;
    }

    public static boolean parseSchemeInfo(Byte[] pack,TbSchemeManager tbSchemeManager,int id) {

        boolean ret = true;
        int i;
        int j;

        int off = TB3531.EVENT_MSG_OFFSET;
        int off2;
        int off3;

        if (pack[off] == 0) {

            TbScheme tbSch = new TbScheme();

            tbSch.version = (pack[off + 1] & 0xf0 );
            tbSch.reserve = 0;

            tbSch.id = (pack[off + 2] & 0xff );
            tbSch.name = getStringFromBytes(pack,off + 3,15);

            /*屏幕点数*/
            tbSch.width = ((pack[off + 18] & 0xff) << 8) | (pack[off + 19] & 0xff);
            tbSch.height = ((pack[off + 20] & 0xff) << 8) | (pack[off + 21] & 0xff);

            Log.i(TAG,"parse scheme,id =" + tbSch.id + ",name=" + tbSch.name +
                ",width ="+tbSch.width +",height=" + tbSch.height);

            int rows = (pack[off + 22] & 0xff);
            int cols = (pack[off + 23] & 0xff);

            /*屏幕拼接矩阵*/
            Log.i(TAG,"parse scheme, scheme rows = " + rows + ",cols=" + cols);

            off2 = off + 24;

            if(rows > 0 && cols > 0) {

                tbSch.tbScreen = new TbScreenConponent[rows][cols];

                for (j = 0; j < rows; j++) {

                    tbSch.tbScreen[j] = new TbScreenConponent[cols];

                    for (i = 0; i < cols; i++) {

                        off3 = off2 + (j * cols + i) * 4;

                        int w =  ((pack[off3] & 0xff) << 8) | (pack[off3 + 1] & 0xff);
                        int h = ((pack[off3 + 2] & 0xff) << 8) | (pack[off3 + 3] & 0xff);

                        tbSch.tbScreen[j][i] = new TbScreenConponent(w,h);
                        Log.i(TAG,"parse scheme screen["+j+"," + i +"]:width=" + w + ",height=" + h);
                    }
                }
            }else{
                Log.e(TAG,"bad rows or cols");
                ret = false;
            }

            /*图像组成部分*/
            off3 = off2 + (rows * cols) * 4;

            int num = (pack[off3] & 0xff);

            if(num > 0){
                tbSch.tbImageConponent = new TbImageConponent[num];

                off3 ++;

                for(i = 0; i < num; i ++){

                    tbSch.tbImageConponent[i] = new TbImageConponent();

                    tbSch.tbImageConponent[i].boardId = (pack[off3] & 0xff);
                    tbSch.tbImageConponent[i].sourceId = (pack[off3  + 1] & 0xff);

                    tbSch.tbImageConponent[i].left =
                            ((pack[off3 + 2] & 0xff) << 8) | (pack[off3 + 3] & 0xff);
                    tbSch.tbImageConponent[i].top =
                            ((pack[off3 + 4] & 0xff) << 8) | (pack[off3 + 5] & 0xff);
                    tbSch.tbImageConponent[i].width =
                            ((pack[off3 + 6] & 0xff) << 8) | (pack[off3 + 7] & 0xff);
                    tbSch.tbImageConponent[i].height =
                            ((pack[off3 + 8] & 0xff) << 8) | (pack[off3 + 9] & 0xff);

                    Log.i(TAG,"parse scheme img["+ i +"]:"
                            +"boardId=" + tbSch.tbImageConponent[i].boardId
                            +",sourceId=" +  tbSch.tbImageConponent[i].sourceId
                            +",left=" +  tbSch.tbImageConponent[i].left
                            +",top=" +  tbSch.tbImageConponent[i].top
                            +",width=" +  tbSch.tbImageConponent[i].width
                            +",height=" +  tbSch.tbImageConponent[i].height);
                }
            }else{
                ret = false;
                Log.e(TAG,"parse scheme, image num = 0");
            }

            tbSchemeManager.map.put(id,tbSch);

        } else {
            ret =false;
            Log.i( TAG, "parseSchemeDes ret=" + pack[off] );
        }

        return ret;
    }

    public static void logTime(String tag){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String time =  formatter.format(new Date());
        Log.i(TAG,tag + ":" + time);
    }

    public static byte[] checksum16(String s){
        byte[] ret = new byte[2];

        int checksum = 0;
        byte[] temp = s.getBytes();

        for(int i = 0; i < temp.length;i++){
            checksum += (temp[i] & 0xff);
        }

        ret[0] = (byte)((checksum >> 8) & 0xff);
        ret[1] = (byte)((checksum) & 0xff);

        return ret;
    }

    public static void saveScheme(TbScheme scheme){

        int i;
        int j;

        int w,h,l,t;

        int rows = scheme.tbScreen.length;
        int cols = scheme.tbScreen[0].length;

        int num = scheme.tbImageConponent.length;

        int schemeLen = (1 + 1 + 15) + 4 + (2 +rows*cols * 4) + ( 1 + num * 10);

        int EventLen = schemeLen + 1 + 4;

        int packLen = EventLen + 4;

        Byte[] req = new Byte[packLen];

        initReq(req,EventLen,EVENT_SROURE_ID_UP_MACHINE,
                EVENT_GROUP_ID_SCHEME,EVENT_SCHEME_ID_SAVE_SCHEME);

        req[7] = 1; /*保存并切换到这个方案*/

        int off = 8;

        req[off] = (byte)((scheme.version << 4)); //version
        req[off + 1] = 0; //id

        byte[] name = scheme.name.getBytes();

        for(i = 0; i < 15;i++){
            if( i < name.length){
                req[off + 2 + i] = name[i];
            }else{
                req[off + 2 + i] = (byte)0;
            }
        }

        off += 17;

        req[off] = (byte)((scheme.width >> 8) & 0xff);
        req[off + 1] = (byte)((scheme.width) & 0xff);
        req[off + 2] = (byte)((scheme.height >> 8) & 0xff);
        req[off + 3] = (byte)((scheme.height) & 0xff);

        req[off + 4] = (byte)((rows) & 0xff);
        req[off + 5] = (byte)((cols) & 0xff);

        off += 6;

        int off2;

        for (j = 0; j < rows; j++) {
            for (i = 0; i < cols; i++) {

                off2 = off + (j * cols + i) * 4;

                w = scheme.tbScreen[j][i].width;
                h = scheme.tbScreen[j][i].height;

                req[off2] = (byte)((w >> 8) & 0xff);
                req[off2 + 1] = (byte)((w) & 0xff);
                req[off2 + 2] = (byte)((h >> 8) & 0xff);
                req[off2 + 3] = (byte)((h) & 0xff);
            }
        }

        off += (rows * cols * 4);

        req[off] = (byte)(num & 0xff);

        for(i = 0; i < num ;i ++){

            TbImageConponent img = scheme.tbImageConponent[i];

            req[off + 1 + i * 10] = (byte)(img.boardId);
            req[off + 2 + i * 10] = (byte)(img.sourceId);

            req[off + 3 + i * 10] = (byte)((img.left >> 8) & 0xff);
            req[off + 4 + i * 10] = (byte)((img.left) & 0xff);

            req[off + 5 + i * 10] = (byte)((img.top >> 8) & 0xff);
            req[off + 6 + i * 10] = (byte)((img.top) & 0xff);

            req[off + 7 + i * 10] = (byte)((img.width >> 8) & 0xff);
            req[off + 8 + i * 10] = (byte)((img.width) & 0xff);

            req[off + 9 + i * 10] = (byte)((img.height >> 8) & 0xff);
            req[off + 10 + i * 10] = (byte)((img.height) & 0xff);
        }

        calcCheckSum(req,packLen);

        writeAsync( req );
    }
}
