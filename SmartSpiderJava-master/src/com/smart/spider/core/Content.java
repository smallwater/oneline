package com.smart.spider.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容上下文
 * 
 * @author smart
 * 
 */
public class Content implements Cloneable {

	/**
	 * 内容文本
	 */
	public String text = "";

	/**
	 * 
	 * Session
	 * 
	 */
	public List<NameValue> session = new ArrayList<NameValue>();

	public Content() {
	}

	public Content(String text) {
		this.text = text;
	}

	public Content(String text, List<NameValue> session) {
		this.session = session;
	}

	/**
	 * 克隆对象副本
	 */
	public Object clone() {

		try {

			return super.clone();

		} catch (CloneNotSupportedException e) {

			e.printStackTrace();

		}

		return null;
	}
}
