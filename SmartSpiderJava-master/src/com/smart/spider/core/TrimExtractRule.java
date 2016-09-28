package com.smart.spider.core;

/**
 * 
 * 规则：替换掉字符串中的空白、换行、制表、回车字符
 * 
 * @author smart
 *
 */
public class TrimExtractRule extends ExtractRule {

	@Override
	public Content exec(Content content) {

		content.text = content.text.replace(" ","").replace("\t", "").replace("\r", "").replace("\n", "").trim();
		
		return content;
	}

}
