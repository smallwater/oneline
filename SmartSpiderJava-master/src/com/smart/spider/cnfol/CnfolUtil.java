package com.smart.spider.cnfol;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.smart.spider.http.HttpClientManage;

public class CnfolUtil {
	public static boolean getCnfolNextPage(String url) {
		String htmlContent = HttpClientManage.GetInstance().GetRequest(url,	connect(url));
		Document document = Jsoup.parse(htmlContent);

		String nxtPagetemp = document.select("div.NewsLstPage>a").get(0).text();
		if (nxtPagetemp.equals("下一页")) {
			return true;
		}
		return false;
	}

	public static String getpageNum(int i) {
		String i_temp = "";
		if (i < 10) {
			i_temp = "0" + i;
			return i_temp;
		} else {
			return "" + i + "";
		}
	}

	
	
	public static boolean getCnfolFinanceNextPage(String urlItem){
		String htmlContent = HttpClientManage.GetInstance().GetRequest(urlItem,
				"utf-8");

		Document document = Jsoup.parse(htmlContent);

		String nxtPagetemp = document.select("div.wzy3L>a").get(0).text();
		if (nxtPagetemp.equals("下一页")) {
			return true;
		}
		return false;
	}

	public static String getsbUrl(String urlPath, int i) {
		String url = "";
		if (i == 1) {
			url = urlPath;
		} else {
			url = urlPath.substring(0, urlPath.lastIndexOf(".")) + "_" + i
					+ ".shtml";
		}
		return url;
	}

	public static String replaces(String sbwords, int urlPageNum) {
		String rep_temp = "";
		for (int i = 1; i <= urlPageNum; i++) {
			rep_temp = "第" + i + "页";
			sbwords = sbwords.replace(rep_temp, "");
		}
		return sbwords;
	}
	
	public static String connect(String spliderUrl) {
		Document doc = null;
		try {
			doc = Jsoup
					.connect(spliderUrl)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31")
					.timeout(3000).get();
		} catch (Exception e) {
		}
		return doc.charset().toString();
	}

}
