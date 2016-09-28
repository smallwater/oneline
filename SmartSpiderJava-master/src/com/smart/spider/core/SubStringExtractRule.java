package com.smart.spider.core;


/**
 * 字符串截取规则
 * 
 * @author smart
 * 
 */
public class SubStringExtractRule extends ExtractRule {

	/**
	 * 开始字符串
	 */
	public String begin = "";

	/**
	 * 结束字符串
	 */
	public String end = "";

	@Override
	public Content exec(Content content) {

		int startIndex = content.text.indexOf(begin) + begin.length();

		if (startIndex < begin.length()) {
			return new Content();
		}

		int endIndex = content.text.indexOf(end, startIndex);

		if (endIndex <= startIndex) {
			return new Content();
		}

		content.text = content.text.substring(startIndex, endIndex);

		return content;
	}

}
