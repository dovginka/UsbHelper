
package com.shizhi.usp.serialport;

import android.os.AsyncTask;
import android.serialport.SerialIoManager;
import android.serialport.hex.HexData;
import android.serialport.utils.SerialUtil;
import android.support.v4.os.AsyncTaskCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.shizhi.usp.base.BaseSerialFra;

/**
 * @author Created by Administrator on  2018-01-15
 * @version 1.0.
 */

public class SerialTestFra extends BaseSerialFra {

    SerialUtil      mSerialUtil;
    SerialIoManager ioManager;
    private static final String TAG = "SerialTestFra";

    static SerialTestFra mFra;

    public static SerialTestFra getInstance() {
        if (mFra == null) {
            mFra = new SerialTestFra();
        }
        return mFra;
    }

    private SerialIoManager.ResponseDataCallback mDataCallback = new SerialIoManager.ResponseDataCallback() {
        @Override
        public void responseData(byte[] bytes) {
            receiverData(bytes);
        }

        @Override
        public void sendData(byte[] bytes) {
            Log.i(TAG, "sendData: " + HexData.hexToString(bytes));
        }

        @Override
        public void onRunError(Exception e) {
            Log.e(TAG, "onRunError: ", e);
        }
    };

    @Override
    public boolean openSerial() {
        AsyncTaskCompat.executeParallel(new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                String result = "請配置串口！！！";
                try {
                    closeSerial();
                    String[] port = getSerialPort();
                    if (port != null) {
                        mSerialUtil = SerialUtil.INIT;
                        mSerialUtil.init(port[0], Integer.valueOf(port[1]), Integer.valueOf(port[2]));
                        ioManager = new SerialIoManager(mSerialUtil);
                        ioManager.setListener(mDataCallback);
                        ioManager.start();
                        result = "打开成功";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "打开失败:" + e.getMessage();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (!TextUtils.isEmpty(s)) {
                    Toast.makeText(mActivity, "" + s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return false;
    }

    @Override
    public void closeSerial() {
        if (ioManager != null) {
            ioManager.stopMe();
            ioManager = null;
        }
        if (mSerialUtil != null) {
            mSerialUtil.closeSerialPort();
            mSerialUtil = null;
        }
    }


    @Override
    public void writeData(byte[] data) {
        if (ioManager != null && data.length > 0) {
            ioManager.syncWrite(data);
        }
    }


}
