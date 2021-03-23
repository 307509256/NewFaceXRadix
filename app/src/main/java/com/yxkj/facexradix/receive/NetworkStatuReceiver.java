package com.yxkj.facexradix.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.yxdz.commonlib.util.SPUtils;
import com.yxkj.facexradix.Constants;
import com.yxkj.facexradix.netty.util.ClientMain;
import com.yxkj.facexradix.netty.util.ClientUtil;
import com.yxkj.facexradix.room.FacexDatabase;
import com.yxkj.facexradix.room.bean.Record;

import java.util.List;


/**
 * @ClassName: NetworkStatuReceiver
 * @Desription: 网络状态变化的接收器
 * @author: Dreamcoding
 * @date: 2017/11/14
 */
public class NetworkStatuReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    Toast.makeText(context, "当前网络可用", Toast.LENGTH_SHORT).show();

                }
            } else {
                // 当前所连接的网络不可用
                Toast.makeText(context, "当前网络不可用", Toast.LENGTH_SHORT).show();

            }
        }
    }


}
