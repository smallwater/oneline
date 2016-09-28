package com.smart.spider.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 内容解析引擎
 * 
 * @author smart
 * 
 */
public class ExtractEngine {

	public String Filter = "";

	public List<Field> fieldList = new ArrayList<Field>();

	/**
	 * 解析内容
	 * 
	 * @param content
	 * @return
	 */
	public final List<FieldResult> exec(Content content) {
		return ExtractContent(content);
	}

	/**
	 * 解析内容
	 * 
	 * @param url
	 * @param content
	 * @return
	 */
	public final List<FieldResult> exec(String url, Content content) {

		List<FieldResult> result = new ArrayList<FieldResult>();

		Pattern compile = Pattern.compile(Filter, Pattern.CASE_INSENSITIVE);

		Matcher matcher = compile.matcher(url);

		boolean  matchResult = matcher.matches();
		
		if (url == null || url == "" || matchResult == false) {
			return result;
		}

		result = ExtractContent(content);

		return result;
	}

	private List<FieldResult> ExtractContent(Content content) {

		List<FieldResult> fieldResultList = new ArrayList<FieldResult>();

		for (Field field : fieldList) {

			Content tempContent = (Content) content.clone();

			FieldResult fieldResult = new FieldResult();
			fieldResult.title = field.title;
			fieldResult.dataName = field.dataName;
			fieldResult.require = field.require;
			fieldResult.dataValue = field.exec(tempContent).text;

			fieldResultList.add(fieldResult);
		}

		return fieldResultList;
	}

}
