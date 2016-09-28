package com.smart.spider.core;

/**
 * 
 * 规则：获取当前Session会话对象中的key值
 * 
 * @author smart
 *
 */
public class SessionExtractRule extends ExtractRule {

	/**
	 * Session会话对象key
	 */
	public String Key = "";

	@Override
	public Content exec(Content content) {

		if (content.session != null && content.session.size() > 0) {

			for (NameValue m : content.session) {

				if (m.name == Key) {

					content.text = m.value;

					return content;

				}

			}

		}

		content.text = "";

		return content;
	}

}
