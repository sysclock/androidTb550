package com.example.tbc.protocal;

import java.util.ArrayList;

/**
 * Created by jah on 16-11-22.
 */

public class TbImageConponent {
    public int boardId;
    public int sourceId;
    public int left;
    public int top;
    public int width;
    public int height;

    public TbImageConponent(){
        boardId = 0;
        sourceId = 0;
        left = 0;
        top = 0;
        width = 0;
        height = 0;
    }

    public TbImageConponent(TbImageConponent obj){
        boardId = obj.boardId;
        sourceId = obj.sourceId;
        left = obj.left;
        top = obj.top;
        width = obj.width;
        height = obj.height;
    }

    public TbImageConponent(
            int boardId,
            int sourceId,
            int left,
            int top,
            int width,
            int height){

        this.boardId = boardId;
        this.sourceId = sourceId;

        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    public final static int PARAM_FAIL_OUT_BOUND = 1;
    public final static int PARAM_FAIL_ID_EXIST = 2;

    public static int checkParam(ArrayList<TbImageConponent> tbiList,int boardId,
                                 int sourceId,
                                 int left,
                                 int top,
                                 int width,
                                 int height){

        if (boardId >= 0
            && sourceId >= 0
            && left >= 0
            && top >= 0
            && width > 0
            && height > 0){

            for(int i = 0; i < tbiList.size();i++){
                TbImageConponent tbi = tbiList.get(i);
                if(boardId == tbi.boardId && sourceId == tbi.sourceId){
                    return PARAM_FAIL_ID_EXIST;
                }
            }

            return 0;
        }else{
            return PARAM_FAIL_OUT_BOUND;
        }

    }
}
