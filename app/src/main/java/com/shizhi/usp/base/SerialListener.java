package com.shizhi.usp.base;

/**
 * @author Created by Administrator on  2018-01-16
 * @version 1.0.
 */

public interface SerialListener {

    boolean openSerial();

    void closeSerial();

    void initSerial();

    void writeData(byte[] data);

    void receiverData(byte[] data);

}
