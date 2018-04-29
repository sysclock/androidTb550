package com.example.tbc.protocal;

/**
 * Created by jah on 16-11-22.
 */

public class TbInputSource {
    public int boardId;
    public int sourceId;
    public int type;
    public int width;
    public int height;

    public TbInputSource(){

    }

    public TbInputSource(TbInputSource item){

        this.boardId = item.boardId;
        this.sourceId = item.sourceId;
        this.type = item.type;
        this.width = item.width;
        this.height = item.height;

    }

    public String getItemName(){

        String name = "unkown";

        switch(type){
            case 0:
                //name = "(HDMI/VGA)";
                name = "HDMI";
                break;
            case 1:
                name = "ADV";
                break;
            case 2:
                name = "DVI";
                break;
            case 3:
                name = "SDI";
                break;
            case 4:
                name = "CVBS";
                break;
            case 5:
                name = "IP";
                break;
        }

        return name + sourceId;
    }
}
