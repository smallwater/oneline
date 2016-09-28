package com.smart.spider.core;

import java.text.MessageFormat;

/**
 * 字段提取结果
 * 
 * @author smart
 *
 */
public class FieldResult {

	/**
	 * toString 重写字符换输出格式
	 */
	private String _format = "\"title\":\"{0}\", \"dataName\":\"{1}\", \"require\":{2}, \"dataValue\":\"{3}\"";
	
	/**
	 * 字段标题
	 */
	public String title = "";

	/**
	 * 编程字段
	 */
	public String dataName = "";

	/**
	 * 字段内容
	 */
	public String dataValue = "";

	/**
	 * 是否必须
	 */
	public boolean require = true;

	/**
	 * 验证字段
	 * 
	 * @return
	 */
	public boolean validation() {
		return !(require && dataValue == "");

	}

	@Override
	public String toString() {
		
		return "{" + MessageFormat.format(_format, title,dataName, require, dataValue.replace("\"", "\\\"")) + "}"; 

	}

}
