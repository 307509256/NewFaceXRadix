package com.yxdz.commonlib.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @PackageName: com.yxdz.commonlib.util
 * @Desription:
 * @Author: Dreamcoding
 * @CreatDate: 2019/1/7 20:35
 */
public class DeviceUtil {
    public static String getLocalMacAddress(Context context) {
        String mac="";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mac = getMachineHardwareAddress();
        } else {
//            WifiManager wifi = (WifiManager) context
//                    .getSystemService(Context.WIFI_SERVICE);
//            WifiInfo info = wifi.getConnectionInfo();
//            mac = info.getMacAddress().replace(":", "");
            mac = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        }

        return mac.toUpperCase();
    }

    /**
     * 获取设备的mac地址和IP地址（android6.0以上专用）
     *
     * @return
     */
    public static String getMachineHardwareAddress() {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String hardWareAddress = null;
        NetworkInterface iF = null;
        while (interfaces.hasMoreElements()) {
            iF = interfaces.nextElement();
            try {
                hardWareAddress = bytesToString(iF.getHardwareAddress());
                if (hardWareAddress == null) continue;
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        if (iF != null && iF.getName().equals("wlan0")) {
            hardWareAddress = hardWareAddress.replace(":", "");
        }
        return hardWareAddress;
    }

    /***
     * byte转为String
     * @param bytes
     * @return
     */
    private static String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }
}
