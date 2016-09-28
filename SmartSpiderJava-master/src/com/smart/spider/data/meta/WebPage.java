package com.smart.spider.data.meta;

/**
 * 
 * 普通网页
 * 
 * @author smart
 *
 */
public class WebPage extends BaseContent {

	/**
	 * 网页标题
	 */
	public String HtmlTitle;

	/**
	 * 网页元数据关键词
	 */
	public String HtmlKeywords;

	/**
	 * 网页元数据描述
	 */
	public String HtmlDescription;

	/**
	 * 网页HTML内容
	 */
	public String HtmlContent;

	/**
	 * 网页文本内容
	 */
	public String TextContent;

	/**
	 * 网站域名
	 */
	public String Domain;

	/**
	 * IP地址
	 */
	public String IpAddress;

	/**
	 * 响应时间
	 */
	public int ResponseTime;

	/**
	 * 网页大小
	 */
	public int ContentLength;

	public WebPage() {
		HtmlTitle = "";
		HtmlKeywords = "";
		HtmlDescription = "";
		HtmlContent = "";
		TextContent = "";
		Domain = "";
		IpAddress = "";
		ResponseTime = 0;
		ContentLength = 0;
	}

	@Override
	public boolean Verify() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void Trim() {
		// TODO Auto-generated method stub
		
	}
}
