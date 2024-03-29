/*
 * Copyright (C) 2011 PIGMAL LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pigmal.android.accessory;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;
import com.google.android.maps.MapActivity;

abstract public class AccessoryBaseMapActivity extends MapActivity {
    private static final String TAG = "SimpleDemokit";

    private static final String ACTION_USB_PERMISSION = "com.pigmal.android.SimpleDemoKit.action.USB_PERMISSION";

    private PendingIntent mPermissionIntent;

    private boolean mPermissionRequestPending;

    private UsbManager mUsbManager;

    private UsbAccessory mUsbAccessory;

    protected Accessory mOpenAccessory;

    /**
     * nofity USB is atached
     */
    protected void onUsbAtached() {
    };

    /**
     * nofity USB is detached
     */
    protected void onUsbDetached() {
    };

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = UsbManager.getAccessory(intent); // 2.3.4
                    // UsbAccessory accessory =
                    // (UsbAccessory)intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    // //3.1
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (mOpenAccessory.open(accessory)) {
                            mUsbAccessory = accessory;
                            onUsbAtached();
                        }
                    } else {
                        Log.d(TAG, "permission denied for accessory " + accessory);
                    }
                    mPermissionRequestPending = false;
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = UsbManager.getAccessory(intent); // 2.3.4
                // UsbAccessory accessory =
                // (UsbAccessory)intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                // //3.1
                if (accessory != null && accessory.equals(mUsbAccessory)) {
                    mOpenAccessory.close();
                    onUsbDetached();
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUsbManager = UsbManager.getInstance(this); // 2.3.4
        // mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        // //3.1
        mOpenAccessory = new Accessory(mUsbManager);

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION),
                0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(mUsbReceiver, filter);

        if (getLastNonConfigurationInstance() != null) {
            mUsbAccessory = (UsbAccessory) getLastNonConfigurationInstance();
            mOpenAccessory.open(mUsbAccessory);
            onUsbAtached();
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        if (mUsbAccessory != null) {
            return mUsbAccessory;
        } else {
            return super.onRetainNonConfigurationInstance();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Intent intent = getIntent();
        if (mOpenAccessory.isConnected()) {
            return;
        }

        UsbAccessory[] accessories = mUsbManager.getAccessoryList();
        UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        if (accessory != null) {
            if (mUsbManager.hasPermission(accessory)) {
                if (mOpenAccessory.open(accessory)) {
                    mUsbAccessory = accessory;
                    onUsbAtached();
                }
            } else {
                synchronized (mUsbReceiver) {
                    if (!mPermissionRequestPending) {
                        mUsbManager.requestPermission(accessory, mPermissionIntent);
                        mPermissionRequestPending = true;
                    }
                }
            }
        } else {
            Log.d(TAG, "mAccessory is null");
        }
    }

    @Override
    public void onPause() {
        mOpenAccessory.close();
        mUsbAccessory = null;
        super.onPause();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mUsbReceiver);
        super.onDestroy();
    }
}
