package com.shizhi.usp.usb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Created by Administrator on  2018-03-16
 * @version 1.0.
 */

public class UsbInfoFra extends Fragment {

    private ListView mListView;
    private UsbManager mUsbManager;
    private Activity mContext;
    private static UsbInfoFra mFra;

    public static UsbInfoFra getInstance() {
        if (mFra == null) mFra = new UsbInfoFra();
        return mFra;
    }


    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//          Toast.makeText(MainActivity.this, "" + ((TextView) view).getText().toString(), Toast.LENGTH_SHORT).show();
            UsbDetailDialog.showDialog(mContext, ((TextView) view).getText().toString());
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUsbManager = (UsbManager) getContext().getSystemService(Context.USB_SERVICE);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(android.R.layout.list_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setOnItemClickListener(mOnItemClickListener);
        refresh();
    }

    @SuppressLint("StaticFieldLeak")
    private void refresh() {
        AsyncTaskCompat.executeParallel(new AsyncTask<Object, Object, Set<String>>() {
            @Override
            protected Set<String> doInBackground(Object... params) {
                HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
                return deviceList.keySet();
            }

            @Override
            protected void onPostExecute(Set<String> strings) {
                super.onPostExecute(strings);
                if (mContext == null) return;
                String[] strings1 = strings.toArray(new String[]{});
                mListView.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, strings1));
            }
        });
    }


}
