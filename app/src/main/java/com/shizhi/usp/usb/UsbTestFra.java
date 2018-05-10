package com.shizhi.usp.usb;

import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.shizhi.usp.base.BaseSerialFra;

public class UsbTestFra extends BaseSerialFra {

    private static final String TAG = "UsbTestFra";
    private UsbSerialDevice usbSerialDevice;
    static UsbTestFra mFra;

    public static UsbTestFra getInstance() {
        if (mFra == null) {
            mFra = new UsbTestFra();
        }
        return mFra;
    }

    /**
     * 打开串口
     */
    @Override
    public boolean openSerial() {
        AsyncTaskCompat.executeParallel(new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                String result = "請配置串口！！！";
                final String[] port = getSerialPort();
                if (port != null) {
                    UsbDevice device = mUsbManager.getDeviceList().get(port[0]);
                    if (device != null) {
                        if (!mUsbManager.hasPermission(device)) {
                            mUsbManager.requestPermission(device, PendingIntent.getActivity(mActivity, 100, new Intent(""), 0));
                        }
                        int wating = 10;
                        boolean flag = false;
                        while (true) {
                            wating--;
                            if (wating < 0) break;
                            if (mUsbManager.hasPermission(device)) {
                                flag = true;
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                        if (flag) {
                            try {
                                UsbDeviceConnection usbDeviceConnection = mUsbManager.openDevice(device);
                                usbSerialDevice = UsbSerialDevice.createUsbSerialDevice(device, usbDeviceConnection);
                                if (usbSerialDevice != null && usbSerialDevice.open()) {
                                    usbSerialDevice.setBaudRate(Integer.valueOf(port[1]));
                                    usbSerialDevice.setDataBits(Integer.valueOf(port[2]));
                                    usbSerialDevice.setStopBits(Integer.valueOf(port[3]));
                                    usbSerialDevice.setParity(Integer.valueOf(port[4]));
                                    usbSerialDevice.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                                    usbSerialDevice.read(mCallback);
                                    usbSerialDevice.debug(true);
                                    result = "串口已打开";
                                } else {
                                    if (usbSerialDevice != null)
                                        usbSerialDevice.close();
                                    usbSerialDevice = null;
                                    result = "打開失敗";
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "doInBackground: ", e);
                                result = "打開失敗" + e.toString();
                                return result;
                            }
                        } else {
                            result = "授权失敗";
                        }
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();
            }
        });
        return false;
    }

    @Override
    public void closeSerial() {
        if (usbSerialDevice != null)
            usbSerialDevice.close();
        usbSerialDevice = null;
    }

    @Override
    public void writeData(byte[] data) {
        if (usbSerialDevice != null && data.length > 0) {
            usbSerialDevice.write(data);
        }
    }

    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] bytes) {
            receiverData(bytes);
        }
    };
}
