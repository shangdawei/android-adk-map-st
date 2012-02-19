package com.pigmal.android.accessory.game;

import android.content.Context;

public class Beam {
    
    public static final int ACCELL = 15;
    public int x,y;
    private int vX, vY;
    public boolean isVisible = true;
    public Beam(Context context, int x, int y, int vX, int vY) {
        super();
        this.x = x;
        this.y = y;
        this.vX = vX;
        this.vY = vY;
    }
    
    public int getNextX(){
        return x;
    }
    public int getNextY(){
        return y = y - vY - 15;
    }

}
