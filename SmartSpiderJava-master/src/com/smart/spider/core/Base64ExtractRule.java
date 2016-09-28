package com.smart.spider.core;

/**
 * 
 * 规则：将字符串编码为base64
 * 
 * @author smart
 *
 */
public class Base64ExtractRule extends ExtractRule {

	public String Encoding = "utf-8";

	@Override
	public Content exec(Content content) {

		try {

			content.text = new String(content.text.getBytes(), Encoding);

		} catch (Exception ex) {

			ex.printStackTrace();

			content.text = "";

		}
		return content;
	}

}
