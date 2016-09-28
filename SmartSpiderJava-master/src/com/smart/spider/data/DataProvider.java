package com.smart.spider.data;

import com.smart.spider.data.meta.Message;

public abstract class DataProvider {

	/**
	 * 打开
	 */
	public abstract void Open();

	/**
	 * 关闭
	 */
	public abstract void Close();

	/**
	 * 
	 * 发送抓取结果数据消息
	 * 
	 * @param message
	 */
	public abstract void Send(Message message);
		
}
