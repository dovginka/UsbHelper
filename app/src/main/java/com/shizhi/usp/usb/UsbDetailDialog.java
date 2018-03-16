package com.shizhi.usp.usb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

/**
 * @author Created by Administrator on  2017/11/3
 * @version 1.0.
 */

public class UsbDetailDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String TAG = "UsbDetailDialog";
    static String usbName = "";

    /**
     * Helper method
     *
     * @param parent FragmentActivity
     * @return dialog
     */
    public static UsbDetailDialog showDialog(final Activity parent, String mName/* add parameters here if you need */) {
        UsbDetailDialog dialog = newInstance(/* add parameters here if you need */);
        try {
            usbName = mName;
            dialog.show(parent.getFragmentManager(), TAG);
        } catch (final IllegalStateException e) {
            dialog = null;
        }
        return dialog;
    }

    public static UsbDetailDialog newInstance(/* add parameters here if you need */) {
        final UsbDetailDialog dialog = new UsbDetailDialog();
        final Bundle args = new Bundle();
        // add parameters here if you need
        dialog.setArguments(args);
        return dialog;
    }


    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(initView());
//        builder.setTitle(R.string.select);
//        builder.setPositiveButton(android.R.string.ok, this);
//        builder.setNegativeButton(android.R.string.cancel, this);
        final Dialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    /**
     * create view that this fragment shows
     *
     * @return body
     */
    private View initView() {
        View inflate = getActivity().getLayoutInflater().inflate(android.R.layout.select_dialog_item, null);
        UsbManager manager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        if (manager != null) {
            for (UsbDevice device : manager.getDeviceList().values()) {
                if (device.getDeviceName().equals(usbName)) {
                    ((TextView) inflate).setText(String.format("usb:%s", device.toString()));
                    break;
                }
            }
            ((TextView) inflate).setMovementMethod(new ScrollingMovementMethod());
        } else {
            ((TextView) inflate).setText(String.format("usb:%s", "null"));
        }
        return inflate;
    }

}
