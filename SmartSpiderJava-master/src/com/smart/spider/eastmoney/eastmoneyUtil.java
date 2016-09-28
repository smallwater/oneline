package com.smart.spider.eastmoney;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.smart.spider.http.HttpClientManage;

public class eastmoneyUtil {
	
	public static boolean getEastMoneyNextPage(String url,int i) {
		String htmlContent = HttpClientManage.GetInstance().GetRequest(url,"gb2312");

		Document document = Jsoup.parse(htmlContent);
		Elements target_a = document.getElementsByClass("Page").select("a");//获取最大页数
		if(target_a.get(target_a.size()-1).text().equals("下一页")){
			return true;
		}else if(target_a.get(0).text().equals("上一页")&&(Integer.parseInt(target_a.get(0).attr("href").split("[_.]")[1])-i)==-1){
			return true;
		}else{
			return false;
		}
		
	}

}
