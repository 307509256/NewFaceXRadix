package com.yxkj.facexradix.netty.util;


import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yxkj.facexradix.netty.Message;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

/**
 * 自定义编码器
 * @author Administrator
 *
 */
public class CustomEncoder extends MessageToByteEncoder<Message>{

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {

		byte[] data = null;

		if(msg.getData() != null) {
			String json = JSONObject.toJSONString(msg.getData(), SerializerFeature.WriteMapNullValue);
			data = json.getBytes(CharsetUtil.UTF_8);
			// log.d("--------CustomEncoder","send message:"+json);
			//判断数据包是否需要加密
			if(Message.isEncryption(msg.getCode())) {
				//加密数据长度必须8的倍数，不足在前面补0
				int l = 8- ((data.length + 1) % 8);
				byte[] teaData = new byte[data.length + 1 + l];
				//第一个字节为补0个数
				teaData[0] = (byte) l;
				System.arraycopy(data, 0, teaData, l+1, data.length);
				data = TeaUtil.encryptByTea(teaData, (byte)30);
			}
		}
		int len = 8;
		if(data != null) {
			len += data.length;
		}
		ByteBuf buf = Unpooled.buffer(len);
		buf.writeInt(msg.getMsgId());
		buf.writeShortLE(msg.getCode());
		if(data != null) {
			buf.writeShortLE(data.length);
			buf.writeBytes(data);
		}else{
			buf.writeShortLE(0);
		}
		out.writeBytes(buf);
		// log.d("-------CustomEncoder","out:"+buf.toString());
	}

}
