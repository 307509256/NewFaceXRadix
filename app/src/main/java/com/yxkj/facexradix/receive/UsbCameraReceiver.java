package com.yxkj.facexradix.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.yxdz.commonlib.util.LogUtils;
import com.yxdz.commonlib.util.ToastUtils;
import com.yxkj.facexradix.utils.AppErrorUtil;

/**
 * @PackageName: com.yxdz.facex.receive
 * @Desription:
 * @Author: Dreamcoding
 * @CreatDate: 2019/3/12 8:47
 */
public class UsbCameraReceiver extends BroadcastReceiver {


    public boolean isUsbCamera(UsbDevice usbDevice) {
        return usbDevice != null && 239 == usbDevice.getDeviceClass() && 2 == usbDevice.getDeviceSubclass();
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        // 这里可以拿到插入的USB设备对象
        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        switch (intent.getAction()) {
            case UsbManager.ACTION_USB_DEVICE_ATTACHED: // 插入USB设备
                break;
            case UsbManager.ACTION_USB_DEVICE_DETACHED: // 拔出USB设备
                if (isUsbCamera(usbDevice)){
                    LogUtils.d("UsbCameraReceiver","摄像头连接错误，重启app");
                    ToastUtils.showLongToast("摄像头连接错误，5秒后重启app");
                    AppErrorUtil.toCameraError();
                }
                break;
            default:
                break;
        }
    }
}
