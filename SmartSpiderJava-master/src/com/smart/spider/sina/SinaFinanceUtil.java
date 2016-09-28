package com.smart.spider.sina;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.smart.spider.http.HttpClientManage;

public class SinaFinanceUtil {
	public static boolean getNextPage(String url) {
		String htmlContent = HttpClientManage.GetInstance().GetRequest(url, "gb2312");
		Document document = Jsoup.parse(htmlContent);
		if (document.getElementsByClass("list_009").select("li").size() <= 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断是否存在下一页
	 * 
	 * @param spliderUrl
	 * @return
	 */
	public static boolean spliderUrlisExist(String spliderUrl) {
		try {
			Document doc = Jsoup.connect(spliderUrl).timeout(1000).get();
			if (doc.getElementsByClass("list_009").select("li").size() <= 0) {
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Document connect(String spliderUrl) {
		Document doc = null;
		try {
			doc = Jsoup.connect(spliderUrl)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31")
					.timeout(3000).get();
		} catch (Exception e) {
		}
		return doc;
	}

}
