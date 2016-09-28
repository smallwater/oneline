package com.smart.spider.core;

import java.text.MessageFormat;

/**
 * 
 * 复合字段提取结果
 * 
 * @author smart
 *
 */
public class ComplexFieldResult {

	private String _format = "\"title\":\"{0}\", \"dataName\":\"{1}\", \"require\":{2}, \"text\":\"{3}\"";
	
	/**
	 * 字段标题
	 */
	public String title = "";

	/**
	 * 编程字段
	 */
	public String dataName = "";

	/**
	 * 是否必须
	 */
	public boolean require = true;

	/**
	 * 字段内容
	 */
	public String text = "";

	@Override
	public String toString() {
		
		return "{" + MessageFormat.format(_format, title, dataName, require, text.replace("\"", "\\\"")) + "}";
		
	}
}
