package com.smart.spider.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	/**
	 * 
	 * 正则表达式辅助类
	 * 
	 * @param input 字符串
	 * @param regex 正则表达式
	 * @return 正则表达式匹配结果第一条
	 */
	public static String Match(String input, String regex) {

		Pattern praiseCompile = Pattern.compile(regex);

		Matcher praiseMatcher = praiseCompile.matcher(input);

		if (praiseMatcher.find()) {

			return praiseMatcher.group(0);

		}

		return null;

	}

}
