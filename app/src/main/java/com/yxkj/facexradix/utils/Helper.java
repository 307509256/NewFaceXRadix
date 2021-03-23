package com.yxkj.facexradix.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.yxkj.facexradix.MyApplication;

public class Helper {

    /**
     * 重启整个APP
     */
    public static void restartAPP(){
        restartAPP(30000);
    }

    /**
     * 重启整个APP
     */
    public static void restartAPP(int delay){
        Intent intent = MyApplication.getAppContext().getPackageManager()
                .getLaunchIntentForPackage(MyApplication.getAppContext().getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(MyApplication.getAppContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager mgr = (AlarmManager)MyApplication.getAppContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + delay, restartIntent);
    }

    public static void reboot(int delay){
        Intent intent = MyApplication.getAppContext().getPackageManager()
                .getLaunchIntentForPackage(MyApplication.getAppContext().getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(MyApplication.getAppContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager)MyApplication.getAppContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + delay, restartIntent);
    }
}
