package com.yxkj.facexradix.netty.util;


import com.yxkj.facexradix.netty.Message;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

public class CustomDecoder extends ByteToMessageDecoder{

	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		//最少字节数
		if(in.readableBytes() < 8) {
			return;
		}
		//在读取前标记readerIndex
        in.markReaderIndex();
        //读取头部
        int msgId = in.readInt();
        int cmd = in.readShortLE();
        int len = in.readShortLE();
        if (in.readableBytes() < len) {
        	//消息不完整，无法处理，将readerIndex复位
            in.resetReaderIndex();
            return;
        }
        String str = null;
        if(len > 0) {
        	//ByteBuf data = in.readBytes(len);
        	byte[] data1 = new byte[len];
        	in.readBytes(data1, 0, len);
        	if (Message.isEncryption(cmd)) {
        		byte[] deData = TeaUtil.decryptByTea(data1, (byte)30);
        		int length = deData[0] + 1;
        		byte[] data = new byte[deData.length - length];
        		System.arraycopy(deData, length, data, 0, data.length);
        		str = new String(data, CharsetUtil.UTF_8);
     		} else {
     			str = new String(data1, CharsetUtil.UTF_8);
     		}
        }

		out.add(new Message(msgId, cmd, str));
	}

}
