package com.pigmal.android.accessory;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.pigmal.android.accessory.view.MySurfaceView;
import com.pigmal.android.ex.accessory.ADKCommandReceiver;
import com.pigmal.android.ex.accessory.R;

public class GameActivity extends AccessoryBaseActivity implements
		OnClickListener {
	MySurfaceView surfaceView;
	Button buttonUp;
	Button buttonDown;
	Button buttonLeft;
    Button buttonRight;
    Button buttonBeam;

	private ADKCommandReceiver mReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_main);

		surfaceView = (MySurfaceView) findViewById(R.id.mySurfaceView1);

		mReceiver = new ADKCommandReceiver();
		mOpenAccessory.setListener(mReceiver);
		mReceiver.setOnDirectionChangeListener(surfaceView);

		buttonUp = (Button) findViewById(R.id.button_up);
		buttonUp.setOnClickListener(this);
		buttonDown = (Button) findViewById(R.id.button_down);
		buttonDown.setOnClickListener(this);
		buttonLeft = (Button) findViewById(R.id.button_left);
		buttonLeft.setOnClickListener(this);
		buttonRight = (Button) findViewById(R.id.button_right);
		buttonRight.setOnClickListener(this);
        buttonBeam = (Button)findViewById(R.id.button_beam);
        buttonBeam.setOnClickListener(this);


	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_left:
			surfaceView.move(KeyEvent.KEYCODE_DPAD_LEFT, 10);

			break;
		case R.id.button_right:
			surfaceView.move(KeyEvent.KEYCODE_DPAD_RIGHT, 10);

			break;
		case R.id.button_up:
			surfaceView.move(KeyEvent.KEYCODE_DPAD_UP, 10);
			break;
		case R.id.button_down:
			surfaceView.move(KeyEvent.KEYCODE_DPAD_DOWN, 10);
			break;
        case R.id.button_beam:
            surfaceView.fire();

		default:
			break;
		}
	}
}
