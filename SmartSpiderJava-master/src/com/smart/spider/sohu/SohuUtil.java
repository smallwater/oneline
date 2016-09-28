package com.smart.spider.sohu;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.smart.spider.http.HttpClientManage;

public class SohuUtil {
	
	/**
	 * 查找每页“更多”标签的链接
	 * @param url
	 * @return
	 */
	public static List<String> getNextBUrl(String url){
		List<String> list = new ArrayList<String>();
		String b_url="";
		String htmlContent = HttpClientManage.GetInstance().GetRequest(url, connect(url));

		Document document = Jsoup.parse(htmlContent);
		
		Elements ele_h2 = document.select("div.lc>h2");
		if(ele_h2.text().contains("更多")){
			for (int i = 0; i < ele_h2.size(); i++) {
				b_url = ele_h2.get(i).select("a").get(0).attr("href");
				
				Elements ele_h2_temp = Jsoup.parse(HttpClientManage.GetInstance().GetRequest(b_url, "GBK")).select("div.lc>h2");
				if(ele_h2_temp.text().contains("更多")){
					for (int j = 0; j < ele_h2_temp.size(); j++) {
						b_url = ele_h2_temp.get(j).select("a").get(0).attr("href");
						list.add(b_url);
					}
				}else{
					list.add(b_url);
				}
			}
		}else{
			list.add(url);
		}
		
		
		return list;
		
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
