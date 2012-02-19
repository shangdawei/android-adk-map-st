package com.pigmal.android.ex.accessory;

import android.view.KeyEvent;

public class DirectionDescriptor {

	public interface OnDirectionChangeListener {
		void move(int direction, int accell);
	}

	public interface OnFireListener {
		void fire();
	}

	enum Axis {
		X, Y
	};

	private int oldX, oldY;
	private final int VELOCITY = 20;

	public int getDirection(Axis axis, int size) {
		int result = KeyEvent.KEYCODE_DPAD_CENTER;
		switch (axis) {
		case X:
			if (oldX * size > 0) {
				if (size > VELOCITY) {
					result = KeyEvent.KEYCODE_DPAD_RIGHT;
				} else if (size < -VELOCITY) {
					result = KeyEvent.KEYCODE_DPAD_LEFT;
				}
			}
			oldX = size;
			break;
		case Y:
			if (oldY * size > 0) {
				if (size > VELOCITY) {
					result = KeyEvent.KEYCODE_DPAD_DOWN;
				} else if (size < -VELOCITY) {
					result = KeyEvent.KEYCODE_DPAD_UP;
				}
			}
			oldY = size;
			break;
		}
		return result;
	}

}
