package com.smart.spider.core;

import java.text.MessageFormat;

/**
 * 
 * Key/Value结构对象
 * 
 * @author smart
 *
 */
public final class NameValue {

	/**
	 * Key名称
	 */
	public String name = "";

	
	/**
	 * Value值
	 */
	public String value = "";

	@Override
	public String toString() {

		return MessageFormat.format("{0}.{1}", name, value);

	}
}
