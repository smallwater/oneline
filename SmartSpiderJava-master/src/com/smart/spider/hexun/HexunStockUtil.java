package com.smart.spider.hexun;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.smart.spider.http.HttpClientManage;

public class HexunStockUtil {

	/**
	 * http://stock.hexun.com/focus/index-2111.html
	 * http://stock.hexun.com/focus/index.html
	 */
	public String Url = "";
	
	/**
	 * 默认结束页
	 */
	public int defaultEndPage=100;

	/**
	 * 开始页/当前页
	 */
	public int Start = 1;

	/**
	 * 强制终止页
	 */
	public int End = 1;

	/**
	 * 总页数
	 */
	public int PageCount = 40;

	private int totalCount = 0;

	public void setUrl(String url) {

		if (url.endsWith("/") == false) {

			url = url + "/";

		}

		if (url.endsWith(".html") == false) {

			this.Url = url + "index.html";

		} else {

			this.Url = url;

		}

	}

	public String next() {

		if (End == totalCount) {
			totalCount = 0;
			return "";
		}

		if (Start == 0) {
			return "";
		}

		String pageUrl = Url.replace(".html", "-" + Start + ".html");

		Start -= 1;

		totalCount += 1;

		return pageUrl;
	}

	public void init() {

		try {

			Document document = Jsoup.parse(HttpClientManage.GetInstance().GetRequest(Url,"gb2312"));

			int endlength = document.select("div.mainboxcontent").select("div")
					.get(1).select("div.listdh").select("script").html()
					.indexOf(";");

			String a = document.select("div.mainboxcontent").select("div")
					.get(1).select("div.listdh").select("script").html();

			PageCount = Integer.parseInt(a.substring(
					16 + "hxPage.maxPage".length(), endlength));

			Start = PageCount;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean getNextPage(String spliderUrl){
		Document document = Jsoup.parse(HttpClientManage.GetInstance().GetRequest(spliderUrl,"gb2312"));
			if (document.getElementsByClass("list_009").select("li").size() <= 0) {
				return false;
			} else {
				return true;
			}
	}


	
}
