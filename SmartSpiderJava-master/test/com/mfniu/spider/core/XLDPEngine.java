package com.mfniu.spider.core;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.smart.spider.core.Content;
import com.smart.spider.core.ExtractEngine;
import com.smart.spider.core.ExtractEngineManage;
import com.smart.spider.core.Field;
import com.smart.spider.core.FieldResult;
import com.smart.spider.core.LabelRule;
import com.smart.spider.core.SubStringExtractRule;
import com.thoughtworks.xstream.XStream;

public class XLDPEngine {

	private static String homeurl = "http://roll.finance.sina.com.cn/finance/zq1/gsjsy/";// 爬取的domain(大盘)
	private static int page = 1;// 爬取页数
	// private static String charset = "utf-8"; //网页编码
	private static String charset = "gbk"; // 网页编码

	public static void main(String[] args) {
		try {
			String listurl = " ";
			String urlhtml = " ";
			String spliderUrl = "";

			for (int i = 1; i <= page; i++) {
				spliderUrl = homeurl + "index_" + i + ".shtml";
				Document doc = Jsoup.connect(spliderUrl).get();
				Elements el = doc.getElementsByClass("list_009");
				Elements les = el.select("li");
				for (int j = 0; j < les.size(); j++) {
					// 获取网页url
					listurl = les.select("a").get(j).attr("href");
					System.out.println(listurl);
					// 获取网页内容
					urlhtml = Jsoup.connect(les.select("a").get(j).attr("href")).get().html();
					// 进入网页初始化引擎
					Init(listurl, urlhtml);
				}
			}

		} catch (Exception e) {

		}
	}

	/***
	 * 
	 * <p>
	 * Title: Init
	 * </p>
	 * <p>
	 * Description: 初始化引擎
	 * </p>
	 * 
	 * @param url
	 * @param context
	 */
	public static void Init(String url, String context) {

		String kafka_value = ""; // 传送到kafka中的值

		ExtractEngineManage extractEngineManage = new ExtractEngineManage();

		String xml = getEngineXml();

		// 初始化引擎
		extractEngineManage.Init(xml);

		Content content = new Content();

		content.text = context;

		List<FieldResult> result = new ArrayList<FieldResult>();

		result = extractEngineManage.exec(url, content);

		// 删选匹配不到的url

		if (result == null) {
			return;
		}

		// 页面的值依次放入集合中
		ArrayList<String> arrayList = new ArrayList<String>();

		arrayList.add(url + String.valueOf((char) 1));

		for (FieldResult f : result) {

			System.out.print(f.title + " : ");

			// 判断作者长度大于6则，作者为空
			if (f.title.equals("作者") & f.dataValue.length() > 6) {
				f.dataValue = " ";
			}

			System.out.println(f.dataValue);

			arrayList.add(f.dataValue.replaceAll("</?[a-zA-Z]+[^><]*>", "") + String.valueOf((char) 1));

		}

		for (String list : arrayList) {
			kafka_value = kafka_value + list;
		}

		System.out.println("kafka_value=====》" + kafka_value.toString());
		System.out.println("====================================================================");
	}

	/***
	 * 
	 * <p>
	 * Title: getEngineXml
	 * </p>
	 * <p>
	 * Description: 加载对应的网页规则引擎
	 * </p>
	 * 
	 * @return
	 */
	public static String getEngineXml() {

		List<ExtractEngine> tempEngineList = new ArrayList<ExtractEngine>();

		// 初始化
		tempEngineList.add(getXinLangEngine());

		// 序列化
		XStream xStream = new XStream();

		String xml = xStream.toXML(tempEngineList);

		return xml;
	}

	/***
	 * 
	 * <p>
	 * Title: getXinLangEngine
	 * </p>
	 * <p>
	 * Description: 制定新浪财经过滤引擎
	 * </p>
	 * 
	 * @return
	 */
	public static ExtractEngine getXinLangEngine() {
		ExtractEngine engine = new ExtractEngine();
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

			{
				LabelRule lable = new LabelRule();
				title.extractRule.add(lable);
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
			title.dataName = "content";

			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = "<body>";
				one.end = "</body>";
				title.extractRule.add(one);
			}

			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = "<!--wapdump end-->";
				one.end = "<!-- 文章关键字 begin -->";
				title.extractRule.add(one);
			}

			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = "<p>";
				one.end = "iv>";
				title.extractRule.add(one);
			}

			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = "<p>";
				one.end = "</d";
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
				one.begin = "time-source";
				one.end = "weibo-card-dropdown";
				title.extractRule.add(one);
			}
			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = "media_name";
				one.end = "<";
				title.extractRule.add(one);
			}

			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = ">";
				one.end = "&nbsp;";
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
				one.begin = "<body>";
				one.end = "</body>";
				title.extractRule.add(one);
			}

			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = "<!--wapdump end-->";
				one.end = "<!-- 文章关键字 begin -->";
				title.extractRule.add(one);
			}

			{
				SubStringExtractRule one = new SubStringExtractRule();
				one.begin = "<p>";
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
