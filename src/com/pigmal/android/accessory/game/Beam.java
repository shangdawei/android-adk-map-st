package com.pigmal.android.accessory.game;

import android.content.Context;

public class Beam {
    
    public static final int ACCELL = 15;
    private int x,y;
    private int vX, vY;
    public Beam(Context context, int x, int y, int vX, int vY) {
        super();
        this.x = x;
        this.y = y;
        this.vX = vX;
        this.vY = vY;
    }
    
    public int getX(){
        return x;
    }
    public int getY(){
        return y = y - vY - 15;
    }

}
