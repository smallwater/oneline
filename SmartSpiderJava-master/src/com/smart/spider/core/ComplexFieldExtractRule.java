package com.smart.spider.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 复合字段提取规则
 * 
 * @author smart
 *
 */
public class ComplexFieldExtractRule extends ExtractRule {

	/**
	 * 字段列表
	 */
	public List<Field> fieldList = new ArrayList<Field>();

	/**
	 * 
	 * 提取复合字段内容结果
	 * 
	 */
	@Override
	public Content exec(Content content) {

		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("[");
		
		for(Field field : fieldList){
			
			Content tempContent = (Content)content.clone();
			
			ComplexFieldResult complexFieldResult = new ComplexFieldResult();
			
			complexFieldResult.title = field.title;
			complexFieldResult.dataName = field.dataName;
			complexFieldResult.require = field.require;
			complexFieldResult.text = field.exec(tempContent).text;
			
			String complexFieldJson = complexFieldResult.toString() + ",";
			
			stringbuilder.append(complexFieldJson);
		}
		stringbuilder.append("]");

		return new Content(stringbuilder.toString());
	}

}
