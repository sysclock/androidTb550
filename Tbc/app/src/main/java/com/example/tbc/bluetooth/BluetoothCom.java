
package com.example.tbc.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.tbc.protocal.TB3531;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothCom {

    final static String TAG = "BluetoothCom";
    final static String CONTENT_UUID = "00001101-0000-1000-8000-00805F9B34FB";
   // final static String CONTENT_UUID = "E111FDA50693A4E24FB1AFCFC6EB07647825";

    static BluetoothDevice device;

    static volatile BluetoothSocket bluetoothSocket;
    static volatile InputStream inputStream;
    static volatile OutputStream outputStream;

    static volatile Thread thread;


    public static boolean connect(BluetoothDevice device) {

        boolean ret = false;

        BluetoothCom.device = device;

        UUID uuid = UUID.fromString(CONTENT_UUID);
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();//尝试连接
            inputStream = bluetoothSocket.getInputStream();//打开IO流
            outputStream = bluetoothSocket.getOutputStream();

            if (inputStream != null && outputStream != null){
                ret = true;
                Log.i(TAG,"----连接成功-----");

            }else{
                Log.i(TAG,"----连接获取输入流失败-----");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG,"----连接异常-----");
        }

        return ret;
    }

    public static void disConnect(){

        try {

            stopRead();

            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
                Log.i(TAG,"关闭蓝牙输入流");
            }

            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
                Log.i(TAG,"关闭蓝牙输出流");
            }

            if(bluetoothSocket != null){
                bluetoothSocket.close();
                bluetoothSocket = null;
                Log.i(TAG,"关闭蓝牙socket");
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            Thread.sleep( 100 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
    public static int write(byte[] data){

        int len = 0;

        if(outputStream != null){

            try {
                TB3531.logTime("write len=" + data.length);
                TB3531.logBuffer( data,data.length,"write event" );

                outputStream.write(data);
                //outputStream.write(TB3531.packHeartBeat());

                outputStream.flush();
                len = data.length;

                TB3531.logTime("write end" );

            } catch (IOException e) {
                Log.i(TAG,"Write IO Exception");
                e.printStackTrace();
            }
        }else{
            Log.i(TAG,"write fail, the outputStream is closed ");
        }

        return len;
    }
    */


    static void doRun(){

        byte[] arr = new byte[288];
        int len;

        Thread.yield();

        TB3531.preparePushData();

        while(true){

            if(thread != null){
                try {
                    Thread.yield();

                    len = inputStream.read( arr);
                    if(len > 0) {
                        Log.i(TAG,"read len=" + len);
                        TB3531.pushData( arr, len );
                    }else{
                        Log.i(TAG,"Read error, len=" + len);
                    }

                }catch (IOException e){
                    Log.i(TAG,"Read IO Exception");
                    e.printStackTrace();

                    /*try {
                        Thread.sleep( 1000 );
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }*/

                    break;
                }

                if (Thread.currentThread().isInterrupted()) {
                    Log.i(TAG,"Stopped by ifInterruptedStop()");
                    break;
                }
            }else{
                break;
            }
        }
    }

    static class MyThread extends Thread{

        @Override
        public void run(){
            doRun();
        }
    }

    public static void startRead(){

        if(thread == null) {

            Log.i(TAG,"启动数据接收线程");

            thread = new MyThread();

            thread.start();
        }
    }

    public static void startRead_nouse(){

        Log.i(TAG,"启动数据接收线程");

        if(thread == null) {

            Runnable runnable = new Runnable() {

                @Override
                public void run() {

                    doRun();
                }
            };

            thread = new Thread( runnable );

            thread.start();
        }
    }

    public static void stopRead(){

        if(thread != null) {

            Thread tmpThread = thread;
            thread = null;

            if (tmpThread != null) {
                tmpThread.interrupt();
            }
        }
    }
    /*
    public static void testWrite(){

        String test1 = "hello world!\n";

        BluetoothCom.write(test1.getBytes());
    }*/

    /*
    public static class TeleDataCome implements BluetoothDataLiscenler{

        @Override
        public void processData(byte[] data, int len) {
            logBuffer( data,len,"read:");
        }
    }


    public static void testRead(){

        setDataCom(new TeleDataCome());
        startRead();
    }
    */


}