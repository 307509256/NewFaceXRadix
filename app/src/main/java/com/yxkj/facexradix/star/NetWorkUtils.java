package com.yxkj.facexradix.star;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class NetWorkUtils{

    /**
     * 检查互联网地址是否可以访问
     *
     * @param address  要检查的域名或IP地址
     * @param callback 检查结果回调（是否可以ping通地址）{@see java.lang.Comparable<T>}
     */
    public static void isNetWorkAvailable(final String address, final Comparable<Boolean> callback) {
        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (callback != null) {
                    callback.compareTo(msg.arg1 == 0);
                }
            }

        };
        new Thread(new Runnable() {

            @Override
            public void run() {
                Runtime runtime = Runtime.getRuntime();
                Message msg = new Message();
                try {
                    Process pingProcess = runtime.exec("/system/bin/ping -c 1 " + address);
                    InputStreamReader isr = new InputStreamReader(pingProcess.getInputStream());
                    BufferedReader buf = new BufferedReader(isr);
                    if (buf.readLine() == null) {
                        msg.arg1 = -1;
                    } else {
                        msg.arg1 = 0;
                    }
                    buf.close();
                    isr.close();
                } catch (Exception e) {
                    msg.arg1 = -1;
                    e.printStackTrace();
                } finally {
                    runtime.gc();
                    handler.sendMessage(msg);
                }
            }

        }).start();
    }

}