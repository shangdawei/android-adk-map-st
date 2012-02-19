package com.pigmal.android.ex.accessory;

import android.view.KeyEvent;

public class DirectionDescriptor {

	public interface OnDirectionChangeListener {
		void move(int direction, int accell);
	}

	private int X, Y;
	private final int VELOCITY = 20;

	public int getDirection(int x, int y) {
		int result = KeyEvent.KEYCODE_DPAD_CENTER;
		if (X * x > 0) {
			if (x > VELOCITY) {
				result = KeyEvent.KEYCODE_DPAD_RIGHT;
			} else if (x < -VELOCITY) {
				result = KeyEvent.KEYCODE_DPAD_LEFT;
			}
		} else if (Y * y > 0) {
			if (y > VELOCITY) {
				result = KeyEvent.KEYCODE_DPAD_DOWN;
			} else if (y < -VELOCITY) {
				result = KeyEvent.KEYCODE_DPAD_UP;
			}
		}
		X = x;
		Y = y;
		return result;
	}

}
