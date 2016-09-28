package com.smart.spider.data;

import com.smart.spider.data.meta.Message;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

/**
 * 
 * 消息序列化组件
 * 
 * @author smart
 *
 */
public class MessageSerializeManage {

	/**
	 * 
	 * 序列化组件
	 * 
	 */
	private XStream xStream = null;

	/**
	 * 构造函数
	 */
	public MessageSerializeManage() {

		this.xStream = new XStream(new JettisonMappedXmlDriver());

	}

	/**
	 * 
	 * 序列化消息
	 * 
	 * @param message
	 * @return
	 */
	public String Serialize(Message message) {

		try {

			return xStream.toXML(message);

		} catch (Exception e) {

			e.printStackTrace();

		}

		return "";

	}

	/**
	 * 反序列化消息
	 * 
	 * @param messageText
	 * @return
	 */
	public Message Deserialize(String messageText) {

		try {

			return (Message) xStream.fromXML(messageText);

		} catch (Exception e) {

			e.printStackTrace();

		}

		return null;
	}

	/**
	 * 
	 * 获取消息序列化组件实例
	 * 
	 * @return
	 */
	public static MessageSerializeManage getInstance() {

		return new MessageSerializeManage();

	}

}
