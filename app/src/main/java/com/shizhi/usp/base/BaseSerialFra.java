package com.shizhi.usp.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.felhr.utils.HexData;
import com.shizhi.usp.R;
import com.shizhi.usp.setting.SettingsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author Created by Administrator on  2018-01-16
 * @version 1.0.
 */

public abstract class BaseSerialFra extends Fragment implements SerialListener {
    private static final String TAG = "BaseSerialAct";
    @BindView(R.id.open)
    ToggleButton mOpen;
    @BindView(R.id.send_info)
    EditText mSendInfo;
    @BindView(R.id.send)
    Button mSend;
    @BindView(R.id.send_type)
    ToggleButton mSendType;
    @BindView(R.id.clear_info)
    Button mClearInfo;
    @BindView(R.id.settings)
    Button mSettings;
    @BindView(R.id.receiver_info)
    TextView mReceiverInfo;

    public UsbManager mUsbManager;
    private Unbinder unbinder;
    private boolean isHex = true;
    public Activity mActivity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mUsbManager = ((UsbManager) mActivity.getSystemService(Context.USB_SERVICE));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.activity_main, container, false);
        unbinder = ButterKnife.bind(this, inflate);
        return inflate;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openSerial();
                } else {
                    closeSerial();
                }
            }
        });
        mSendType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isHex = !isChecked;
            }
        });

        /*if (this instanceof UsbTestFra) {
            mSendInfo.setText("555500000003010301");
        } else if (this instanceof SerialTestFra) {
            mSendInfo.setText("FE0D001001070000000101000128FF");
        }*/
    }


    @Override
    public void onDestroyView() {
        unbinder.unbind();
        closeSerial();
        super.onDestroyView();
    }


    @OnClick({R.id.settings, R.id.send, R.id.clear_info})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.settings:
                initSerial();
                break;
            case R.id.send:
                String trim = mSendInfo.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) return;
                try {
                    byte[] data;
                    if (isHex) {
                        data = HexData.stringTobytes(trim);
                    } else {
                        data = trim.getBytes();
                    }
                    writeData(data);
                } catch (Exception e) {
                    Toast.makeText(mActivity, "格式错误", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.clear_info:
                mReceiverInfo.setText("");
                break;
        }
    }

    @Override
    public void initSerial() {
        Intent intent = new Intent(mActivity, SettingsActivity.class);
        intent.putExtra("activity", this.getClass().getName());
        startActivity(intent);
    }

    @Override
    public void receiverData(byte[] data) {
        final String s = HexData.hexToString(data);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mReceiverInfo.append(s);
            }
        });
    }

    /**
     * @return port info
     */
    public String[] getSerialPort() {
        String name;
        int braunter, data, stop, parity;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        name = sharedPreferences.getString("name", "");
        braunter = Integer.valueOf(sharedPreferences.getString("braunte", "-1"));
        data = Integer.valueOf(sharedPreferences.getString("data", "-1"));
        stop = Integer.valueOf(sharedPreferences.getString("stop", "-1"));
        parity = Integer.valueOf(sharedPreferences.getString("parity", "-1"));
        Log.d(TAG, String.format("info: name:%s braunter:%s data:%s stop:%s parity:%s", name, braunter, data, stop, parity));
        if (!TextUtils.isEmpty(name) || braunter != -1 || data != -1 || stop != -1 || parity != -1) {
            return new String[]{name, String.valueOf(braunter), String.valueOf(data), String.valueOf(stop), String.valueOf(parity)};
        }
        return null;
    }
}
