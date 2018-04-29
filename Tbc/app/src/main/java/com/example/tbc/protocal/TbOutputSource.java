package com.example.tbc.protocal;

/**
 * Created by jah on 16-12-4.
 */

public class TbOutputSource {

    public int boardId;
    public int portId;

    public TbOutputSource(int boardId,int portId){
        this.boardId = boardId;
        this.portId = portId;
    }

    public String getDisplayInfo(){
        return "板子" + (this.boardId) + " 输出口" + this.portId;
    }

    public static String[] getNameList(TbOutputSource[] obj){

        String[] items = new String[obj.length];

        for(int i = 0; i < obj.length;i++){
            items[i] =  obj[i].getDisplayInfo();
        }
        return items;
    }
}
