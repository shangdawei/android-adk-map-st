package com.pigmal.android.accessory.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.pigmal.android.ex.accessory.DirectionDescriptor.OnDirectionChangeListener;
import com.pigmal.android.ex.accessory.R;

public class MySurfaceView extends SurfaceView implements
		SurfaceHolder.Callback, Runnable, OnDirectionChangeListener {

	private Bitmap img;
	private boolean isRunning = true;
	private Thread thread;
	private Paint paint;
	int x, y;
	int vX, vY;

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
		getHolder().addCallback(this);
		paint = new Paint();
		img = BitmapFactory.decodeResource(getContext().getResources(),
				R.drawable.ic_launcher_demokit);
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
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		thread = null;
	}

	public void move(int direction, int accell) {
		switch (direction) {
		case KeyEvent.KEYCODE_DPAD_DOWN:
			vY = vY + accell;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			vY = vY - accell;
			;
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

	private void draw() {
		Canvas canvas = getHolder().lockCanvas();
		canvas.drawColor(Color.GRAY);
		canvas.drawBitmap(img, vX, vY, paint);
		getHolder().unlockCanvasAndPost(canvas);

	}

}
