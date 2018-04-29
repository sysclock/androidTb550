package com.example.tbc.setting;

import com.example.tbc.protocal.TbScheme;

/**
 * Created by jah on 16-11-26.
 */

public class SettingGuideInfo {

    TbScheme tbScheme;

    public boolean isScreenResSet;
    public boolean isScreenCopSet;
    public boolean isImgCopSet;

    static SettingGuideInfo sgi;

    public static SettingGuideInfo getInstance(){

        if(sgi != null){
            return sgi;
        }else{
            sgi = new SettingGuideInfo();
        }

        return sgi;
    }

    private  SettingGuideInfo(){

        tbScheme = new TbScheme();

        isScreenResSet = false;
        isScreenCopSet = false;
        isImgCopSet = false;
    }

    public static  void  clear(){
        sgi = new SettingGuideInfo();
    }
}
