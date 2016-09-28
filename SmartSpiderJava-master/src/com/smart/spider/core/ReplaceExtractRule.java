package com.smart.spider.core;

/**
 * 
 * 规则：查找替换字符串
 * 
 * @author smart
 *
 */
public class ReplaceExtractRule extends ExtractRule {

	/**
	 * 查找字符串
	 */
	public String Origin = "";

	/**
	 * 替换字符串
	 */
	public String Target = "";

	@Override
	public Content exec(Content content) {
		
		content.text = content.text.replace(Origin, Target);
		
		return content;
	}

}
