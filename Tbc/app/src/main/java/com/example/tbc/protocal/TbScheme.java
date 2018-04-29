package com.example.tbc.protocal;

/**
 * Created by jah on 16-11-22.
 */

public class TbScheme {

    public int version;
    public int reserve;

    public int id;
    public String name;

    public int width;
    public int height;

    public TbScreenConponent[][] tbScreen;

    public TbImageConponent[] tbImageConponent;

    public TbScheme(){
        version = 1;
        reserve = 0;
        name = "scheme";
    }

}
