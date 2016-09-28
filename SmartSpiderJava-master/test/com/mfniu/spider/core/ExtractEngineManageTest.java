package com.mfniu.spider.core;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.smart.spider.core.Content;
import com.smart.spider.core.ExtractEngine;
import com.smart.spider.core.ExtractEngineManage;
import com.smart.spider.core.Field;
import com.smart.spider.core.FieldResult;
import com.smart.spider.core.SubStringExtractRule;
import com.thoughtworks.xstream.XStream;

public class ExtractEngineManageTest {

	@Test
	public void testInit() {

		ExtractEngineManage extractEngineManage = new ExtractEngineManage();

		String xml = getEngineXml();

		extractEngineManage.Init(xml);

		String url = "http://www.cnblogs.com/xiaoyaojian/p/4763016.html";
		Content content = new Content();
		content.text ="<div id=\"post_detail\"><div class=\"post\" id=\"post\">   <a name=\"top\"></a><h2><a id=\"cb_post_title_url\" href=\"http://www.cnblogs.com/xiaoyaojian/p/4763016.html\">在ASP.NET 5应用程序中的跨域请求功能详解</a></h2>  <small>2015-08-27 13:16 by 小白哥哥, <span id=\"post_view_count\">...</span> 阅读, <span id=\"post_comment_count\">...</span> 评论, <a href=\"#\" onclick=\"AddToWz(4763016);return false;\">收藏</a>,  <a href =\"http://i.cnblogs.com/EditPosts.aspx?postid=4763016\" rel=\"nofollow\">编辑</a></small>";
		
		List<FieldResult> result = new ArrayList<FieldResult>();

		result = extractEngineManage.exec(url, content);

		ExtractEngineTest.PrintResult(result);
	}

	private String getEngineXml() {

		List<ExtractEngine> tempEngineList = new ArrayList<ExtractEngine>();

		// 初始化
		tempEngineList.add(getCnblogsEngine());
		//tempEngineList.add(getDianPingEngine());

		// 序列化
		XStream xStream = new XStream();
		
		String xml = xStream.toXML(tempEngineList);

		return xml;
	}

	// 博客园博文提取模型
	private ExtractEngine getCnblogsEngine() {
		
		ExtractEngine engine = new ExtractEngine();

		/*
		 * http://www.cnblogs.com/HQFZ/p/4761650.html
		 * http://www.cnblogs.com/dreamagain/p/4763704.html
		 */
		engine.Filter = "http://www.cnblogs.com/[0-9a-zA-Z_-]+/p/[0-9]+.html";
		
		Field title = new Field();
		title.title = "博文标题";
		title.dataName = "title";
		
		{
			SubStringExtractRule one = new SubStringExtractRule();
			one.begin = "cb_post_title_url";
			one.end = "/a>";		
			title.extractRule.add(one);
		}
		{
			SubStringExtractRule one = new SubStringExtractRule();
			one.begin = ">";
			one.end = "<";		
			title.extractRule.add(one);
		}
		
		engine.fieldList.add(title);
				
		return engine;
	}

}
