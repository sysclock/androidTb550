package com.example.tbc.menu;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.tbc.R;

import java.util.Date;

public class TbUtils{



    public static String getVersionName(Context context)
    {
        try {
            String pkName = context.getPackageName();

            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;

            int versionCode = context.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;


            return versionName;

        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}