package com.smart.spider.hexun;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;

import com.smart.spider.data.meta.Article;
import com.smart.spider.data.meta.ContentType;
import com.smart.spider.data.meta.Message;
import com.smart.spider.http.HttpClientManage;
import com.smart.spider.util.DateUtil;
import com.smart.spider.util.HelperUtil;
import com.smart.spider.util.NewsSiteCommandService;

/**
 * 
 * 全景网新闻抓取
 * 
 * @author smart
 *
 */
public class HeXunRollCommandService extends NewsSiteCommandService {

	private int maxPageCount = 500;
	private int inUrlCount = 0;     //抓取的条数
	private int outUrlCount = 0;    //未抓取的条数

	public HeXunRollCommandService() {

		this.CommandName = "com.smart.spider.hexun.roll";
		this.Description = "和讯_滚动";
		this.Author = "smart";

	}

	@Override
	public void Init(String[] args) {
	}

	@Override
	public void Exec(String[] args) {

		if (args != null && args.length > 1) {

			try {

				maxPageCount = Integer.parseInt(args[1]);

			} catch (Exception e) {

				e.printStackTrace();

			}
		}

		try {

			int pageTotal = 0;

			String returnJsonStr = getEveryPage(String.valueOf(1));

			dataProvider.Open();

			Matcher m = Pattern.compile("(?<=titleLink:')[^']+").matcher(returnJsonStr);

			while (m.find()) {

				if (m.group().indexOf("http://cn.reuters.feedsportal.com/") == -1&&!m.group().substring(7, m.group().indexOf(".")).equals("tg")) {

					getURLWords(m.group());

				}else{
					
					outUrlCount=outUrlCount+1;
					
				}
			}

			Matcher pageMatch = Pattern.compile("(?<=sum:')[^']+(?=')").matcher(returnJsonStr);

			while (pageMatch.find()) {

				pageTotal = (int) Math.ceil(Double.valueOf(pageMatch.group())/30);

			}

			if (maxPageCount > pageTotal || maxPageCount == 0) {

				maxPageCount = pageTotal;
			}
			for (int i = 2; i <= maxPageCount; i++) {

				returnJsonStr = getEveryPage(String.valueOf(i));

				Matcher urlMatch = Pattern.compile("(?<=titleLink:')[^']+").matcher(returnJsonStr);

				while (urlMatch.find()) {
					
					if (urlMatch.group().indexOf("http://cn.reuters.feedsportal.com/") == -1&&!urlMatch.group().substring(7, urlMatch.group().indexOf(".")).equals("tg")) {

						getURLWords(urlMatch.group());

					}else{
						
						outUrlCount=outUrlCount+1;
						
					}

				}
			}
		    logger.info("com.mfniu.spider.hexun.roll 共抓取url数量:"+inUrlCount+"条,未抓取："+outUrlCount+"条");
			dataProvider.Close();

		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * @Title: getEveryPage @Description: 获取每页的 url 返回json格式数据 @param
	 *         page @return String @throws
	 */

	public String getEveryPage(String page) {
		// 放参数 规则根据和讯网 滚动抓取 http://roll.hexun.com/
		Document doc=null;
		try {
			StringBuffer url = new StringBuffer("http://roll.hexun.com/roolNews_listRool.action");
			url.append("?type=all&ids=100,101,103,125,105,124,162,194,108,122,121,119,107,116,114,115,182,120,169,170,177,180,118,190,200,155,130,117,153,106");
			//url.append("&date=").append(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			url.append("&page=").append(page);
			url.append("&tempTime=").append(new Date().getTime());
			 doc = HttpClientManage.GetInstance().GetDocument(url.toString(), "UTF-8", 5000, 10000);
		} catch (Exception e) {
			logger.error(e);
		}
		return doc.text();
	}

	/**
	 * @Title: getURLWords @Description: 根据每页的url 获取相应 格式 (网络媒体 综合门户 和讯 滚动
	 *         和讯网_新闻 采集时间 发布时间 作者 当前网址 文章标题 文章来源 文章内容)的数据获取文章内容 @param
	 *         urlPath @return void @throws
	 */

	public void getURLWords(String urlPath) {
		String locationPath = "";
		try {
			
			Document doc= HttpClientManage.GetInstance().GetDocument(urlPath.toString(), "gb2312", 5000, 10000);
			if(doc==null){
				outUrlCount=outUrlCount+1;
            	return;
			}
			Message message = new Message();
			message.SiteName = "和讯";
			message.SpiderName = this.CommandName;
			message.InternalCategory = "和讯网_新闻 _网络媒体";
			message.Timestamp = System.currentTimeMillis();
			message.Url = urlPath;
			message.UrlHash = HelperUtil.ToMd5(message.Url);
			message.contentType = ContentType.Article;

			Article article = new Article();


			if (doc.select("span#pubtime_baidu").size()>0) {
				article.PublishTime = doc.select("span#pubtime_baidu").text();// 获取发布时间
			}
			if (doc.select("span#author_baidu").size()>0) {
				article.Author = doc.select("span#author_baidu").text().split("作者：")[1];// 获取作者
			}
			if (doc.select("span#source_baidu").size()>0) {
				article.Referrer = doc.select("span#source_baidu").text().split("来源：")[1];// 获取作者
			}
			if (doc.select("div.art_title").size()>0) {
				article.Title = doc.select("div.art_title").text();// 获取文章标题
			}
			if (doc.select("div.art_context").size()>0) {
				article.ContentText = doc.select("div.art_context").text();// 获取文章内容
				article.ContentHtml = doc.select("div.art_context").html();
			}
			if (doc.select("div#page_navigation>a").size()>0) {
				locationPath = doc.select("div#page_navigation").text().replace(">", "_").replace("_ 正文", "");
			}
			message.ExternalCategory =locationPath;
			article.CommentCount = 0;
			article.ZhuanFaCount = 0;
			article.PraiseCount = 0;
			article.ReadCount = 0;

			// 2015-11-26 18:41:50
			article.PublishTime = DateUtil.toDateTime(article.PublishTime);
			article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);
			//排除图片新闻，特征：内容短
            if(article.ContentText.length()<15){
            	outUrlCount=outUrlCount+1;
            	return;
            }
			
			message.content = article;
			inUrlCount=inUrlCount+1;
			//logger.info(message.ExternalCategory+"@"+urlPath+"@"+article.Referrer+"@"+article.PublishTime+"@"+article.Author+"@"+article.Title+"@"+article.ContentText.substring(0, 50));
			dataProvider.Send(message);
		} catch (Exception e) {
			logger.error(e+urlPath);
		}
	}
}
