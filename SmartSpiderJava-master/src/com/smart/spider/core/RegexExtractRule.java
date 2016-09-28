package com.smart.spider.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 规则：根据正则表达式提取字符串内容，默认返回第一个匹配结果
 * 
 * @author smart
 *
 */
public class RegexExtractRule extends ExtractRule {
	
	public String Regex = "";

	@Override
	public Content exec(Content content) {

		Pattern compile = Pattern.compile(Regex);

		Matcher matcher = compile.matcher(content.text);

		if(matcher.find()){
			
			content.text =matcher.group(1);
			
		}
		
		return content;
	}

}
