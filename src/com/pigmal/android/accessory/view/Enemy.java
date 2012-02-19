package com.pigmal.android.accessory.view;

import java.util.Random;

public class Enemy {
    public int x, y;
    public boolean isVisible = true;

    public Enemy(int dispW, int dispH, int imgW, int imageH) {
        super();

        Random rnd = new Random();
        x = rnd.nextInt(dispW - imgW);
        y = rnd.nextInt((dispH - imageH) / 2);
    }
    

}
