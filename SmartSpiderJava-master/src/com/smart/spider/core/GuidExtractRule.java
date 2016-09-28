package com.smart.spider.core;

import java.util.UUID;

/**
 * 
 * 规则：生成一个随机不重复的guid字符串
 * 
 * @author smart
 *
 */
public class GuidExtractRule extends ExtractRule {

	@Override
	public Content exec(Content content) {

		content.text = UUID.randomUUID().toString();

		return content;
	}

}
