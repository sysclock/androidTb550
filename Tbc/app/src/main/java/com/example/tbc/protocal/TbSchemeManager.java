package com.example.tbc.protocal;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jah on 16-11-22.
 */

public class TbSchemeManager {
    public int curId;
    public int[] ids;

    public Map<Integer,TbScheme> map;
    public TbScheme[] arr;

    public TbSchemeManager(){
        curId = -1;
        ids = null;
        map = new HashMap<Integer,TbScheme>();
    }

    public TbScheme getTbSchemeById(int id){
        return map.get(id);
    }

    public TbScheme getTbSchemeBySubscript(int index){
        TbScheme ret = null;
        if(index >= 0 && index < ids.length){
            ret = map.get(ids[index]);
        }

        return ret;
    }

    public String[] getTbSchemeNames(){
        String[] ret = null;

        if(map != null && !map.isEmpty()) {

            ret = new String[map.size()];
            int i = 0;
            for(Map.Entry<Integer, TbScheme> entry:map.entrySet()){
                TbScheme scheme = entry.getValue();
                ret[i ++] = scheme.name;
            }
        }
        return ret;
    }

    public TbScheme[] getschemes(){
        TbScheme[] arr = null;

        if(map != null && !map.isEmpty()) {

            arr = new TbScheme[map.size()];
            int i = 0;

            for(Map.Entry<Integer, TbScheme> entry:map.entrySet()){
                arr[i++] = entry.getValue();
            }
        }
        return arr;
    }

    public TbScheme getScheme(int index){
        TbScheme scheme = null;

        if(map != null && !map.isEmpty()) {

            int i = 0;
            for(Map.Entry<Integer, TbScheme> entry:map.entrySet()){
                if(i == index){
                    scheme = entry.getValue();
                    break;
                }else{
                    i++;
                }
            }
        }

        return scheme;
    }
}
