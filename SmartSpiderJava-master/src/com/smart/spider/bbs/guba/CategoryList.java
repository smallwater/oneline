package com.smart.spider.bbs.guba;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.smart.spider.http.HttpClientManage;

/**
 * 股吧论坛全部主题
 * 
 * @author smart
 *
 */
public class CategoryList {

	private String _geguUrl = "http://guba.eastmoney.com/remenba.aspx";
	private String _zhutiUrl = "http://guba.eastmoney.com/remenba.aspx?type=2";
	private String _hangyeUrl = "http://guba.eastmoney.com/remenba.aspx?type=3";
	private String _gainianUrl = "http://guba.eastmoney.com/remenba.aspx?type=4";

	/**
	 * 股吧分类列表
	 */
	public List<TopicPageInfo> DataList = new ArrayList<TopicPageInfo>();

	/**
	 * 初始化
	 */
	public void Init() {
		
		DataList.addAll(0, HtmlParse(_gainianUrl, "gbboxb", "概念吧"));
		DataList.addAll(0, HtmlParse(_hangyeUrl, "gbboxb", "行业吧"));
		DataList.addAll(0, HtmlParse(_zhutiUrl, "gbboxb", "主题吧"));
		DataList.addAll(0, HtmlParse(_geguUrl, "gbboxb", "个股吧"));
	}

	private List<TopicPageInfo> HtmlParse(String url, String bodyClass, String category) {

		List<TopicPageInfo> result = new ArrayList<TopicPageInfo>();

		Document document = HttpClientManage.GetInstance().GetDocument(url);

		Elements contentBody = document.getElementsByClass(bodyClass);

		for (Element element : contentBody) {

			Elements links = element.getElementsByTag("a");

			for (Element e : links) {

				TopicPageInfo topic = new TopicPageInfo();
				topic.Title = e.text();
				topic.Url = e.attr("href");
				topic.Category = category;

				if (topic.Url.startsWith("http://") == false) {
					topic.Url = "http://guba.eastmoney.com/" + topic.Url;
				}

				result.add(topic);
			}

		}

		return result;
	}

}
