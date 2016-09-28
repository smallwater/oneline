package com.smart.spider.ifeng;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.smart.spider.http.HttpClientManage;

public class IfengSpliderUtil {

	public static Document getDocument(String urlPath) {
		Document doc = null;
		try {
			String htmlContent = HttpClientManage.GetInstance().GetRequest(urlPath);

			doc = Jsoup.parse(htmlContent);
		} catch (Exception e) {

			e.printStackTrace();
			doc = null;
		}
		return doc;
	}

	public static boolean spliderIfengHongguanUrlisExist(String url) {
		String htmlContent = HttpClientManage.GetInstance().GetRequest(url);
		Document document = Jsoup.parse(htmlContent);
		if (document.getElementById("list01").select("h3").size() <= 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断凤凰网大盘分析&新股要闻是否存在下一页
	 * 
	 * @param spliderUrl
	 * @return
	 */
	public static boolean spliderIfengJshqUrlisExist(String url) {
		String htmlContent = HttpClientManage.GetInstance().GetRequest(url);
		Document document = Jsoup.parse(htmlContent);
		if (document.select("div.list03>ul").size() <= 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断凤凰网股吧：http://finance.ifeng.com/report/
	 * 
	 * @param spliderUrl
	 * @return
	 */
	public static boolean spliderIfengReportUrlisExist(String url) {
		String htmlContent = HttpClientManage.GetInstance().GetRequest(url);
		Document document = Jsoup.parse(htmlContent);
		if (document.select("div.title").size() <= 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断凤凰网股吧内部公司研究，策略研究等内部是否有内容
	 * http://app.finance.ifeng.com/report/type.php?t=1
	 * http://app.finance.ifeng.com/report/type.php?t=2
	 * http://app.finance.ifeng.com/report/type.php?t=3
	 * http://app.finance.ifeng.com/report/type.php?t=4
	 * http://app.finance.ifeng.com/report/type.php?t=5
	 * http://app.finance.ifeng.com/report/type.php?t=8
	 * http://app.finance.ifeng.com/report/type.php?t=11
	 * http://app.finance.ifeng.com/report/type.php?t=12
	 * 
	 * @param spliderUrl
	 * @return
	 */
	public static boolean spliderIfengReportSubUrlisExist(String url) {
		String htmlContent = HttpClientManage.GetInstance().GetRequest(url);
		Document document = Jsoup.parse(htmlContent);
		Elements tds = null;
		tds = document.select("div.newsHybg>table.list2");
		if (tds.select("td[width=560]").size() <= 0) {
			return false;
		} else {
			return true;
		}
	}

}
