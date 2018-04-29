package com.example.tbc.menu;

import android.util.Log;

import com.example.tbc.menu.MenuItem;
import com.example.tbc.page.display.PageDisplayContrast;
import com.example.tbc.page.display.PageDisplayInfo;
import com.example.tbc.page.display.PageDisplaySaturation;
import com.example.tbc.page.display.PageDisplayShaprness;
import com.example.tbc.page.led.PageLedGetResolution;
import com.example.tbc.page.led.PageLedSetResolution;
import com.example.tbc.page.osd.PageOsdKey;
import com.example.tbc.page.osd.PageOsdPlay;
import com.example.tbc.page.sound.PageSoundGetVolume;
import com.example.tbc.page.sound.PageSoundSetVolume;
import com.example.tbc.page.system.PageSystemFactory;
import com.example.tbc.page.system.PageSystemReboot;
import com.example.tbc.page.system.PageSystemSetHeartbeat;
import com.example.tbc.page.system.PageSystemUpgrade;
import com.example.tbc.page.videoout.PageVideoOutFrameRate;
import com.example.tbc.page.videoout.PageVideoOutInfo;
import com.example.tbc.page.videoout.PageVideoOutResolution;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jah on 16-11-12.
 */


class MenuUtils {

    /*1:父菜单，0:子菜单*/

    private static MenuItem[] menus = new MenuItem[]{

        new MenuItem("设置向导",1,null,"ic_action_goright"),

        new MenuItem("视频输出",1,null,"ic_action_tv"),
            new MenuItem("设置分辨率",0,PageVideoOutResolution.class,""),
            new MenuItem("设置帧率",0, PageVideoOutFrameRate.class,""),
            new MenuItem("查询参数",0,PageVideoOutInfo.class,""),

        new MenuItem("声音输出",1,null,"ic_action_music_1"),
            new MenuItem("音量设置",0, PageSoundSetVolume.class,""),
            new MenuItem("查询音量",0,PageSoundGetVolume.class,""),

        new MenuItem("图像质量",1,null,"ic_action_tv"),
            new MenuItem("设置对比度",0,PageDisplayContrast.class,""),
            new MenuItem("设置饱和度",0,PageDisplaySaturation.class,""),
            new MenuItem("设置亮度",0,PageDisplayShaprness.class,""),
            new MenuItem("查询图像质量",0,PageDisplayInfo.class,""),

        new MenuItem("子画面",1,null,"ic_action_tv"),
            new MenuItem("子画面选择",0,PageDisplayContrast.class,""),
            new MenuItem("查询当前选择子画面",0,PageDisplaySaturation.class,""),

        new MenuItem("场景",1,null,"ic_action_tv"),
            new MenuItem("切换场景",0,PageDisplayContrast.class,""),
            new MenuItem("take场景",0,PageDisplaySaturation.class,""),
            new MenuItem("get场景",0,PageDisplayShaprness.class,""),
            new MenuItem("开启/关闭局部放大",0,PageDisplayInfo.class,""),
            new MenuItem("查询局部放大状态",0,PageDisplayShaprness.class,""),
            new MenuItem("开启/关闭点对点",0,PageDisplayInfo.class,""),
            new MenuItem("查询点对点状态",0,PageDisplayShaprness.class,""),

        new MenuItem("界面控制",1,null,"ic_action_news"),
            new MenuItem("按键控制",0,PageOsdKey.class,""),
            new MenuItem("播放控制",0,PageOsdPlay.class,""),

        new MenuItem("LED设置",1,null,"ic_action_news"),
            new MenuItem("设置分辨率",0,PageLedSetResolution.class,""),
            new MenuItem("查询分辨率",0,PageLedGetResolution.class,""),

        new MenuItem("系统设置",1,null,"ic_action_gear"),
            new MenuItem("重启",0,PageSystemReboot.class,""),
            new MenuItem("升级",0,PageSystemUpgrade.class,""),
            new MenuItem("恢复出厂设置",0,PageSystemFactory.class,""),
            new MenuItem("心跳设置",0,PageSystemSetHeartbeat.class,"")
    };

    static MenuItem[] getMenuItems(){
        return menus;
    }

}
