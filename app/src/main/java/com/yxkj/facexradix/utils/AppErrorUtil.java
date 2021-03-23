package com.yxkj.facexradix.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.yxdz.commonlib.util.SPUtils;
import com.yxkj.facexradix.MyApplication;
import com.yxkj.facexradix.Constants;

/**
 * @PackageName: com.yxdz.facex.utils
 * @Desription:
 * @Author: Dreamcoding
 * @CreatDate: 2019/3/12 16:06
 */
public class AppErrorUtil {

    public static  void toCameraError(){
        int error= SPUtils.getInstance().getInt(Constants.CAMERA_ERROR_REMOVE,0);
        if (error==5){
            //被移除了5次
            SPUtils.getInstance().put(Constants.CAMERA_ERROR_REMOVE,0);
//            MyApplication.getFireflyApi().reboot();
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent resetAppIntent = MyApplication.getAppContext().getPackageManager()
                            .getLaunchIntentForPackage(MyApplication.getAppContext().getPackageName());
                    PendingIntent restartIntent = PendingIntent.getActivity(MyApplication.getAppContext(), 0, resetAppIntent, PendingIntent.FLAG_ONE_SHOT);
                    AlarmManager mgr = (AlarmManager)MyApplication.getAppContext().getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 5000, restartIntent);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }
            },5000);

        }
    }
}
