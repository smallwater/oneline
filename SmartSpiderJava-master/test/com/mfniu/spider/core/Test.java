package com.mfniu.spider.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Test {

	private static String homeurl = "http://roll.finance.sina.com.cn/finance/zq1/gsjsy/";// 爬取的url
	private static int page = 1;// 爬取页数

	public static void main(String[] args) {

		try {

			// String str1 = "新浪网登载此";
			// String str2 = "新浪网登载此";
			// String str3 = "";
			//
			// str3 = str1+String.valueOf((char) 1 )+str1;
			//
			// System.out.println(str3.toString());

			// String strHtml="进入<a href=http://guba.sina.com.cn target= _blank
			// suda-uatrack= key=suda_1028_gub 【新浪财经股吧】</a>讨论"; //HTML文本代码
			// String strClear=strHtml.replaceAll( ".*?(.*?)<\\/body>", "$1");
			// //读出body内里所有内容
			// strClear=strClear.replaceAll("</?[a-zA-Z]+[^><]*>","");//保留br标签和p标签
			// System.out.println(strClear);//输出结果

			String listurl = " ";
			String urlhtml = " ";
			String spliderUrl = "";

			for (int i = 1; i <= page; i++) {
				spliderUrl = homeurl + "index_" + i + ".shtml";
				Document doc = Jsoup.connect(spliderUrl).get();
				Elements el = doc.getElementsByClass("list_009");
				Elements les = el.select("li");
				for (int j = 0; j < 1; j++) {
					listurl = les.select("a").get(j).attr("href");
					System.out.println(listurl);
					urlhtml = Jsoup.connect(les.select("a").get(j).attr("href")).get().html();
					System.out.println(urlhtml.replaceAll("</?[a-zA-Z]+[^><]*>", ""));
				}
			}

		} catch (Exception e) {

		}

	}

	public static void tes() {

		String url_str = "http://news.sina.com.cn/c/zs/2015-11-02/doc-ifxkhqea2948866.shtml";

		String s = new String(url_str);

		String a[] = s.split("//");

		String b[] = a[1].split("/");

		for (int i = 0; i < b.length; i++) {
			System.out.println(b[i]);
		}

		System.out.println(url_str);

	}

}
