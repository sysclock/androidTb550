package com.example.tbc.protocal;

import android.util.Log;

/**
 * Created by jah on 16-11-22.
 */

public class TbInputSourceManager {

    TbInputSource[] inputArray;

    boolean[] used;

    int num;

    public TbInputSourceManager(TbInputSource[] inputArray){

        this.inputArray = inputArray;

        used = new boolean[inputArray.length];

        for(int i = 0;i < inputArray.length;i++){
            used[i] = false;
        }

        num = inputArray.length;
    }

    public TbInputSource[] getNoUsedInputSource(){

        TbInputSource[] nouse = new TbInputSource[num];

        int j = 0;

        for(int  i = 0; i < inputArray.length;i++ ){
            if(!used[i]) {
                nouse[j++] = new TbInputSource( inputArray[i] );
            }
        }

        return nouse;
    }

    public String[] getNoUsedInputSourceNames(){

        String[] nouse = new String[num];

        int j = 0;

        for(int  i = 0; i < inputArray.length;i++ ){
            if(!used[i]) {
                nouse[j++] = new String( inputArray[i].getItemName());
            }
        }

        return nouse;
    }

    public void setUsed(int bordId,int portId){
        for(int i = 0;i < inputArray.length;i++){
            if(inputArray[i].boardId == bordId
                    && inputArray[i].sourceId == portId){
                if(used[i] == true){
                    used[i] = false;
                    num --;
                }
            }
        }
    }

    public TbInputSource getInputSource(int bordId,int portId){

        TbInputSource tbInputSource = null;

        Log.e("","find--- " + bordId + "," + portId);

        for(int i = 0;i < inputArray.length;i++){

            Log.e("","cur--- " + inputArray[i].boardId + "," + inputArray[i].sourceId);

            if(inputArray[i].boardId == bordId
                    && inputArray[i].sourceId == portId){
                tbInputSource = inputArray[i];
                break;
            }
        }

        return tbInputSource;
    }
}
