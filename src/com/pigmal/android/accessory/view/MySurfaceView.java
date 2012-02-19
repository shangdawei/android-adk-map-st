package com.pigmal.android.accessory.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.pigmal.android.accessory.game.Beam;
import com.pigmal.android.ex.accessory.DirectionDescriptor.OnDirectionChangeListener;
import com.pigmal.android.ex.accessory.R;

public class MySurfaceView extends SurfaceView implements
		SurfaceHolder.Callback, Runnable, OnDirectionChangeListener {

    public static final int MAX_APPLE = 2;
    
    private Bitmap img, imgBeam, imgEnemy;
    private boolean isRunning = true;
    private Thread thread;
    private Paint paint;
    private Beam beam;
    int firstY;
    int vX, vY;
    int dispW, dispH;
    private ArrayList<Enemy> enemies = new ArrayList<Enemy>(MAX_APPLE);

    public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MySurfaceView(Context context) {
        super(context);
        init();
    }

    private void init() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        dispW = display.getWidth();
        dispH = display.getHeight();

        getHolder().addCallback(this);
        paint = new Paint();
        img = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.hiouki);
        imgEnemy = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_launcher_demokit);
        imgBeam = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_demokit);
       
        setCenterBottom();
        initEnemies();
    }
    
    private void initEnemies(){
        for( int i = 0; i < MAX_APPLE; i++){
            Enemy enemy = new Enemy(dispW, dispH, imgBeam.getWidth(), imgBeam.getHeight());
            enemies.add(enemy);
        }
    }
    
    private void setCenterBottom(){
        vY = dispH -( img.getHeight() * 2);
        vX = dispW / 2 - (img.getWidth() / 2);
    }

    @Override
    public void run() {
        while (isRunning) {
            draw();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new Thread(this);
        thread.start();
        isRunning = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
        thread = null;
    }
    
    public void move(int direction, int accell){
        switch (direction) {
        case KeyEvent.KEYCODE_DPAD_DOWN:
            vY = vY + accell;
            break;
        case KeyEvent.KEYCODE_DPAD_UP:
            vY = vY - accell;;
            break;
        case KeyEvent.KEYCODE_DPAD_LEFT:
            vX = vX - accell;
            break;
        case KeyEvent.KEYCODE_DPAD_RIGHT:
            vX = vX + accell;
            break;

        default:
            break;
        }
        
    }
    
    private void draw(){
        Canvas canvas = getHolder().lockCanvas();
        canvas.drawColor(Color.GRAY);
        
        //Enemy
        for(Enemy enemy:enemies){

            if(enemy.isVisible){
                if(isCollision(enemy, beam)){
                    enemy.isVisible = false;
                    beam.isVisible = false;
                }
            }
            
            if(enemy.isVisible){
                canvas.drawBitmap(imgEnemy, enemy.x, enemy.y, paint);
            }
        }
        // Beam
        if(beam != null && beam.isVisible){
            canvas.drawBitmap(imgBeam, beam.getNextX(), beam.getNextY(), paint);
        }
        
        canvas.drawBitmap(img, vX, vY, paint);
        getHolder().unlockCanvasAndPost(canvas);

    }
    
    
    public void fire(){
        beam = new Beam(getContext(), vX, vY, Beam.ACCELL, Beam.ACCELL);
    }
    
    private boolean isCollision(Enemy enemy, Beam beam){
        
        if(beam == null || enemy == null){
            return false;
        }
        
        if(beam.x + (imgBeam.getWidth() / 2) >= enemy.x && beam.x <= enemy.x + imgEnemy.getWidth()
           && beam.y <= enemy.y
        ){
          return true;  
        }
        
        return false;
    }
}
