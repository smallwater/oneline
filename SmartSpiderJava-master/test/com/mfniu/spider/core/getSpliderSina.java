package com.mfniu.spider.core;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class getSpliderSina {

	private static String url = "http://roll.finance.sina.com.cn/finance/zq1/gsjsy/";// 爬取的url
	private static int page = 1;// 爬取页数
	private static String filePath = "E://sinaSplider";

	public static void main(String[] args) throws IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();

		}

		String spliderUrl = "";

		for (int i = 1; i <= page; i++) {
			spliderUrl = url + "index_" + i + ".shtml";
			Document doc = Jsoup.connect(spliderUrl).get();
			Elements el = doc.getElementsByClass("list_009");
			Elements les = el.select("li");
			for (int j = 0; j < les.size(); j++) {
				System.out.println(les.select("a").get(j).attr("href"));
			}
		}

	}

}
