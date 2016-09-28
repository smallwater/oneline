package com.smart.spider.core;


/**
 * 
 * 内容提取规则
 * 
 * @author smart
 *
 */
public abstract class ExtractRule {

	/**
	 * 
	 * 内容提取
	 * 
	 * @param content
	 * @return
	 */
	public abstract Content exec(Content content);
	
}
