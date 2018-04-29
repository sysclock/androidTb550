package com.example.tbc.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by jah on 16-11-16.
 */

public class DataSave {

    private final static String SHARE_PRE_NAME = "data";

    public static void setData(Context context, String name, String value){

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARE_PRE_NAME,Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(name,value);

        editor.apply();
    }

    public static String getData(Context context, String name, String defValue){

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARE_PRE_NAME,Activity.MODE_PRIVATE);

        return sharedPreferences.getString(name,defValue);
    }

    public static void setData(Context context, String name,Set<String> values){

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARE_PRE_NAME,Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putStringSet(name,values);

        editor.apply();
    }

    public static Set<String> getData(Context context, String name,Set<String> defValues){

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARE_PRE_NAME,Activity.MODE_PRIVATE);

        return sharedPreferences.getStringSet(name,defValues);
    }
}
