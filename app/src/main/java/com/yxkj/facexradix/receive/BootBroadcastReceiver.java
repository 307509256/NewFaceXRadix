package com.yxkj.facexradix.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yxkj.facexradix.ui.activity.NavigateActivity;


/**
 * @PackageName: com.yxdz.facex.receive
 * @Desription:
 * @Author: Dreamcoding
 * @CreatDate: 2019/2/21 21:31
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent i = new Intent(context, NavigateActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

    }
}
