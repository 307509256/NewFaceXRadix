package com.yxkj.facexradix.netty;


import java.util.HashMap;
import java.util.Map;


public class Message<T> {
	private Integer msgId;//消息id,叠加，谁发谁定义
	private Integer code;//操作命令
	private Object data;//接口返回的业务数据，类型可为数值、字符串或集合等



	public Integer getMsgId() {
		return msgId;
	}

	public void setMsgId(Integer msgId) {
		this.msgId = msgId;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}


	public Message() {
	}

	public Message(Integer code) {
		this.code = code;
	}

	public Message(Integer code, Object data) {
		this.code = code;
		this.data = data;
	}

	public Message(Integer id, Integer code, Object data) {
		this.msgId = id;
		this.code = code;
		this.data = data;
	}

	public static Message success(Integer id, Integer code) {
		Message msg = new Message(id, code);
		Map<String, Object> map = new HashMap<>();
		map.put("code", 0);
		map.put("msg", "成功");
		msg.setData(map);
		return msg;
	}

	public static Message error(Integer id, Integer code, String retmsg) {
		Message msg = new Message(id, code);
		Map<String, Object> map = new HashMap<>();
		map.put("code", 1);
		map.put("msg", retmsg);
		msg.setData(map);
		return msg;
	}


	public static boolean isEncryption(int cmd) {
		switch(cmd) {
			case 108:
			case 301:
				return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	@Override
	public String toString() {
		return "massage["+msgId+"],cmd["+code+"],"+data.toString();
	}

}







