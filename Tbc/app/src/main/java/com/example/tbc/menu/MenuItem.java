package com.example.tbc.menu;

import android.app.Activity;

/**
 * Created by jah on 16-11-12.
 */

public class MenuItem {

    private String name;
    private int type;
    private Class<?> target;
    private String icon;

    public MenuItem(String name,int type,Class<?> target,String icon){
        this.name = name;
        this.type = type;
        this.target = target;
        this.icon = icon;
    }

    String getName(){
        return name;
    }

    int getType(){
        return type;
    }

    Class<?> getTarget(){
        return target;
    }

    String getIcon(){
        return icon;
    }
}


