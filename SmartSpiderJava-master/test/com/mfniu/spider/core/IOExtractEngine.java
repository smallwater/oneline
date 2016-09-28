package com.mfniu.spider.core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.smart.spider.core.Content;
import com.smart.spider.core.ExtractEngine;
import com.smart.spider.core.ExtractEngineManage;
import com.smart.spider.core.Field;
import com.smart.spider.core.FieldResult;
import com.smart.spider.core.LabelRule;
import com.smart.spider.core.SubStringExtractRule;
import com.thoughtworks.xstream.XStream;

public class IOExtractEngine {

	public static void main(String[] args) {
		try {

			// 提取文本数据
			String context = " ";
			// 设定获取字段Title
			String Title = "title";
			// 设定获取字段Body
			String Body = "body";
			// 数据容器，用来接收数据
			String len;
			// url
			// String url =
			// "http://news.sina.com.cn/c/nd/2015-09-07/doc-ifxhqhuf8149417.shtml";
			// 正确
			String url = "http://finance.sina.com.cn/stock/jsy/20151104/095423673111.shtml";
			// 文件路径

			String inputfile = "F:/xinlang/caijing/060923670447.shtml";

			// 写入文本防止乱码
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputfile), "GBK"));

			while ((len = br.readLine()) != null) {
				context = len;
			}

			Init(url, context);

		} catch (Exception e) {

		}
	}

	// 初始化引擎及提取数据
	public static void Init(String url, String context) {

		ExtractEngineManage extractEngineManage = new ExtractEngineManage();

		String xml = getEngineXml();

		// 初始化引擎
		extractEngineManage.Init(xml);

		Content content = new Content();

		content.text = context;

		List<FieldResult> result = new ArrayList<FieldResult>();

		result = extractEngineManage.exec(url, content);

		StringBuilder htmlstr = new StringBuilder();

		for (FieldResult f : result) {

			// String str = f.dataValue.toString()+" : ";
			//
			// htmlstr.append(str);

			System.out.print(f.title + " : ");
			System.out.println(f.dataValue);

		}

		System.out.println(htmlstr);
	}

	// 得到新浪新闻的xml引擎
	public static String getEngineXml() {

		List<ExtractEngine> tempEngineList = new ArrayList<ExtractEngine>();

		// 初始化
		tempEngineList.add(getXinLangEngine());

		// 序列化
		XStream xStream = new XStream();

		String xml = xStream.toXML(tempEngineList);

		return xml;
	}

	// 新浪新闻
	public static ExtractEngine getXinLangEngine() {
		ExtractEngine engine = new ExtractEngine();

		// engine.Filter =
		// "http://news.sina.com.cn/c/nd/[0-9a-zA-Z_-]+/[0-9a-zA-Z_-]+.shtml";
		// 新浪财经网url过滤
		engine.Filter = "http://finance.sina.com.cn/stock/jsy/[0-9a-zA-Z_-]+/[0-9a-zA-Z_-]+.shtml";

		{
			Field title = new Field();
			title.title = "标题";
			title.dataName = "title";
			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = "<title";
				one.end = "</title>";
				title.extractRule.add(one);
			}
			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = ">";
				one.end = "|";
				title.extractRule.add(one);
			}

			engine.fieldList.add(title);
		}

		{
			Field title = new Field();
			title.title = "时间";
			title.dataName = "time";
			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = "time-source";
				one.end = "media_name";
				title.extractRule.add(one);
			}
			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = ">";
				one.end = "<";
				title.extractRule.add(one);
			}

			{
				LabelRule lable = new LabelRule();
				title.extractRule.add(lable);
			}

			engine.fieldList.add(title);
		}

		{
			Field title = new Field();
			title.title = "内容";
			title.dataName = "title";

			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = "<body>";
				one.end = "</body>";
				title.extractRule.add(one);
			}

			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = "<!-- 窄通 end -->";
				one.end = "<!-- 文章关键字 begin -->";
				title.extractRule.add(one);
			}

			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = "<p>";
				one.end = "</div>";
				title.extractRule.add(one);
			}

			{
				LabelRule lable = new LabelRule();
				title.extractRule.add(lable);
			}
			engine.fieldList.add(title);
		}

		{
			Field title = new Field();
			title.title = "来源";
			title.dataName = "source";

			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = "来源";
				one.end = "article-editor";
				title.extractRule.add(one);
			}

			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = "：";
				one.end = "<";
				title.extractRule.add(one);
			}

			{
				LabelRule lable = new LabelRule();
				title.extractRule.add(lable);
			}
			engine.fieldList.add(title);
		}

		{
			Field title = new Field();
			title.title = "作者";
			title.dataName = "author";

			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = "article-editor";
				one.end = "news_weixin_ercode clearfix";
				title.extractRule.add(one);
			}

			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = ">";
				one.end = "</p>";
				title.extractRule.add(one);
			}

			{
				LabelRule lable = new LabelRule();
				title.extractRule.add(lable);
			}
			engine.fieldList.add(title);
		}

		return engine;
	}
}
