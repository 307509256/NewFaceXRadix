package com.yxkj.facexradix.netty.util;


import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

public class ClientInitializer extends ChannelInitializer<Channel>{



	@Override
	protected void initChannel(Channel ch) throws Exception {

		ch.pipeline()
				.addLast(new IdleStateHandler(60, 5, 0, TimeUnit.SECONDS))
				.addLast(new CustomDecoder())
				.addLast(new CustomEncoder())
				.addLast(new ClientHandler());
	}
}
