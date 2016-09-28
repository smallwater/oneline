package com.smart.spider.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 内容提取结果字段
 * 
 * @author smart
 *
 */
public class Field {

	/**
	 * 字段标题
	 */
	public String title = "";

	/**
	 * 编程字段
	 */
	public String dataName = "";
	
	/**
	 * 是否必填
	 */
	public boolean require = true;
	
	/**
	 * 字段提取规则集合
	 */
	public List<ExtractRule> extractRule = new   ArrayList<ExtractRule>(); 
	
	public final Content exec(Content content){
		
		for(ExtractRule rule : extractRule){
			
			content = rule.exec(content);
			
		}
		
		return content;
	}
	
}
