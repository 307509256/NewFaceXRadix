package com.yxkj.facexradix.netty.util;

import android.content.Intent;
import android.util.Log;

import com.yxdz.commonlib.util.SPUtils;
import com.yxkj.facexradix.Constants;
import com.yxkj.facexradix.MyApplication;
import com.yxkj.facexradix.ui.activity.MainActivity;

import java.util.concurrent.TimeUnit;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientMain {

    private static Channel channel;
    private static Bootstrap bootstrap = new Bootstrap();
    private static EventLoopGroup group = new NioEventLoopGroup();

    static {
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ClientInitializer());
    }

    private ClientMain() {

    }


    public static void doConnect() {
        boolean appIsClose = SPUtils.getInstance().getBoolean("ACTIVITY_IS_CLOSE", false);
        if (appIsClose) {
            return;
        }
        String serverIp = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS, Constants.DEFAULT_SERVER_ADDRESS);
        String serverPort = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS_PORT, Constants.DEFAULT_SERVER_ADDRESS_PORT);
        ChannelFuture future = bootstrap.connect(serverIp, Integer.valueOf(serverPort));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    // log.d("ClientMain", "success");
                    channel = future.channel();
//                    int tcpclient_count = SPUtils.getInstance().getInt("TCPCLIENT_COUNT", 0);
//                    tcpclient_count += 1;
//                    SPUtils.getInstance().put("TCPCLIENT_COUNT", tcpclient_count);

                } else {
                    // log.d("ClientMain", "fail");
                    MyApplication.getAppContext().sendBroadcast(new Intent(Constants.TCP_CONNECTION_FAIL));
                    if (SPUtils.getInstance().getBoolean("isReconnect", false)) {
                        doConnect();
                        // log.d("ClientMain", "重連");
                    } else {
                        future.channel().eventLoop().schedule(new Runnable() {
                            @Override
                            public void run() {
                                doConnect();
                                // log.d("ClientMain", "重連");
                            }
                        }, 2, TimeUnit.SECONDS);
                    }
                }
                // log.d("nettyFuture", String.valueOf(future.isSuccess()));
            }
        });
    }

    public static Channel getChannel() {
        return channel;
    }


}
