package com.smart.spider.core;

/**
 * 
 * 规则：按制定的字符分割内容为字符串数组，并返回指定索引处的值
 * 
 * @author smart
 *
 */
public class SplitExtractRule extends ExtractRule {

	/**
	 * 分割字符串
	 */
	public String Split ;
	
	/**
	 * 索引位置，默认从0开始
	 */
	public int ValueIndex = 0;
	
	@Override
	public Content exec(Content content) {

		String[] splitResult = content.text.split(Split);
		
		if(splitResult.length> ValueIndex){
			
			content.text = splitResult[ValueIndex];
			
			return content;
		}
		
		content.text = "";
		
		return content;
	}

}
