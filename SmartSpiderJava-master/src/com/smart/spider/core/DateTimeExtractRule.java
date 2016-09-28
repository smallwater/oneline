package com.smart.spider.core;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * 规则：根据日期时间格式返回当前系统时间
 * 
 * @author smart
 *
 */
public class DateTimeExtractRule extends ExtractRule {

	/**
	 * 
	 * 默认格式：yyyy-MM-dd HH:mm:ss
	 * 
	 */
	public String DateFormat = "yyyy-MM-dd HH:mm:ss:fff";
	
	@Override
	public Content exec(Content content) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateFormat);
		
		content.text =simpleDateFormat.format(new Date());
		
		return content;
	}

}
