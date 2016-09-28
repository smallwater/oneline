package com.smart.spider.cnfol;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.smart.spider.data.meta.Article;
import com.smart.spider.data.meta.ContentType;
import com.smart.spider.data.meta.Message;
import com.smart.spider.http.HttpClientManage;
import com.smart.spider.util.DateUtil;
import com.smart.spider.util.HelperUtil;
import com.smart.spider.util.NewsSiteCommandService;

public class CnfolFinanceCommandService extends NewsSiteCommandService {
	
	private List<String> urlList = new ArrayList<String>();
	private static int maxPageCount = 100;
	
	public CnfolFinanceCommandService(){
		CommandName = "com.smart.spider.cnfol.fund";

		Description = "中金在线";

		Author = "smart";
	}

	@Override
	public void Init(String[] args) {
		urlList.add("http://fund.cnfol.com/jijindongtai/");
		urlList.add("http://fund.cnfol.com/jijinjiaodian/");
		urlList.add("http://fund.cnfol.com/jijinkanshi/");
		urlList.add("http://fund.cnfol.com/jijinshiping/");
		urlList.add("http://fund.cnfol.com/jijinxuetang/");
		urlList.add("http://fund.cnfol.com/jijinlicai/");
		urlList.add("http://fund.cnfol.com/jijinzhongcang/");
		urlList.add("http://fund.cnfol.com/touzicelue/");
		urlList.add("http://fund.cnfol.com/hongguanjingji/");
		urlList.add("http://fund.cnfol.com/haiwaijijin/");
		urlList.add("http://fund.cnfol.com/shebaonianjin/");
		
		
		
	}

	@Override
	public void Exec(String[] args) {

		if (null != args && args.length >= 2) {

			try {
				maxPageCount = Integer.parseInt(args[1]);

			} catch (Exception e) {

				e.printStackTrace();

			}
		}

		dataProvider.Open();

		for (String url : urlList) {

			try {

				ExtractHtmlContent(url);

			} catch (Exception e) {

				logger.error(e);

			}

		}

		dataProvider.Close();

	}
	
	/**
	 * 获取文章的链接，标题，来源
	 * @param url
	 * @throws IOException 
	 */
	private void ExtractHtmlContent(String url){
		for (int i = 1; i <= maxPageCount; i++) {
			String urlItem ="";
			if(i==1){
				urlItem = url+"index.shtml";
			}else{
				urlItem = url+"index_"+CnfolUtil.getpageNum(i)+".shtml";
			}
			
			
			if(CnfolUtil.getCnfolFinanceNextPage(urlItem)){
				String htmlContent = HttpClientManage.GetInstance().GetRequest(urlItem, CnfolUtil.connect(urlItem));
				Document document = Jsoup.parse(htmlContent);
				
				Elements ele_ul = document.select("div.lb.l30>li");
				String mixMes = document.title().replaceAll("[ ]", "").replaceAll("-", "_");
				
				for (int k = 0; k < ele_ul.size(); k++) {
					String urlPath = ele_ul.get(k).select("a").attr("href");// 链接
					String urlname = ele_ul.get(k).select("a").text();// 标题
					getURLWords(mixMes, urlPath, urlname);
				}
			}
		}
	}
	

	
	private void getURLWords(String mixMes, String urlPath, String urlname){
		String htmlContent = HttpClientManage.GetInstance().GetRequest(urlPath, "utf-8");
		Document document = Jsoup.parse(htmlContent);
		
		String Page = document.select("div.wzy2L5").text().replaceAll(" ", "");
		int urlPageNum=1;//文章页数
		if(StringUtils.isNotEmpty(Page)){
			urlPageNum = Integer.parseInt(Page.substring(1,Page.indexOf("页")).trim());
		}
		
		String sbwords = "";
		String sbwords_html = "";
		
		Map<String,String> map_sbwords = new HashMap<String, String>();
		
		
		if(urlPageNum==1){
			sbwords = document.getElementById("__content").select("div").text().replaceAll("　+", "");//内容
			sbwords_html = document.getElementById("__content").select("div").html();
		}else{
			map_sbwords = getSbWords(urlPath,urlPageNum);
			sbwords = map_sbwords.get("words");
			sbwords_html = map_sbwords.get("wordshtml");
		}
		
		
		Message message = new Message();
		message.SiteName = "中金在线";
		message.SpiderName = this.CommandName;
		message.InternalCategory = "网络媒体_综合门户_中金在线";
		message.Timestamp = System.currentTimeMillis();
		message.contentType = ContentType.Article;

		Article article = new Article();
		article.Title =urlname;
		message.ExternalCategory = mixMes;
		message.Url = urlPath;
		message.UrlHash = HelperUtil.ToMd5(message.Url);
		
		Element time_temp=document.getElementById("pubtime_baidu");
		if(time_temp!=null){
			article.PublishTime = DateUtil.toDateTime(time_temp.text().replaceAll(" ", " "),"yyyy-MM-ddHH:mm");//发布时间 yyyy-mm-dd HH:mm:ss
		}else{
			article.PublishTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		
		article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);
		
		article.ContentText = sbwords;//文章内容
		article.ContentHtml = sbwords_html;//带HTML格式的文章内容
		
		article.CommentCount = 0;
		article.ZhuanFaCount = 0;
		article.PraiseCount = 0;
		article.ReadCount = 0;
		
		Element form_temp = document.getElementById("source_baidu");
		if(form_temp!=null){
			article.Referrer = form_temp.text().replace("来源：", "");//来源
		}else{
			article.Referrer = "";
		}
		Element author_temp=document.getElementById("author_baidu");
		if(author_temp!=null){
			article.Author=author_temp.text().replace("作者：", "").replaceAll("　", "");//作者
		}else{
			article.Author="暂无";
		}
		
		
		message.content = article;
		
		if(StringUtils.isNotEmpty(article.ContentText)){
			dataProvider.Send(message);
		}
		
		

	}

	private Map<String,String> getSbWords(String urlPath, int urlPageNum){
		Map<String,String> map_sbwords = new HashMap<String, String>();
		StringBuffer sbwords = new StringBuffer();
		StringBuffer sbwords_html = new StringBuffer();
		String sb_url = "";
		for (int i = 1; i <= urlPageNum; i++) {
		sb_url = CnfolUtil.getsbUrl(urlPath,i);
		
		String htmlContent = HttpClientManage.GetInstance().GetRequest(sb_url, CnfolUtil.connect(sb_url));
		Document document = Jsoup.parse(htmlContent);
		
		sbwords.append(document.getElementById("__content").select("div").text().replaceAll("　+", ""));
		sbwords_html.append(document.getElementById("__content").select("div").html());
		}
		map_sbwords.put("words", CnfolUtil.replaces(sbwords.toString(),urlPageNum));
		map_sbwords.put("wordshtml", sbwords_html.toString());
		return map_sbwords;
	}


}
