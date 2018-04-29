package com.example.tbc.protocal;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by jah on 16-12-4.
 */

public class TbTimer {

    public String name;
    public int type;
    public int schemeId;

    public Calendar calendar;

    public TbTimer(){

    }

    public TbTimer(String name,int type,int schemeId,Calendar calendar){

        this.name = name;
        this.type = type;
        this.schemeId = schemeId;

        this.calendar = calendar;//new GregorianCalendar(2007, 11, 25,0,0,0);
    }
}
