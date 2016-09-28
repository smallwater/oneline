/**
 * 
 */
package com.smart.spider.jrj;

import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smart.spider.data.DataProvider;
import com.smart.spider.data.meta.Article;
import com.smart.spider.data.meta.ContentType;
import com.smart.spider.data.meta.Message;
import com.smart.spider.http.HttpClientManage;
import com.smart.spider.util.DateUtil;
import com.smart.spider.util.HelperUtil;

/**
 * @author 页面处理
 *
 */
public class AnalyseJrjFactory {

	private final Logger logger = LoggerFactory.getLogger(AnalyseJrjFactory.class);
	private DataProvider dataProvider;
	private String netUrl = "http://stock.jrj.com.cn/xwk/";
	private String CommandName = "";
	private int total = 0;
	private int inUrlCount = 0; // 抓取的条数
	private int outUrlCount = 0; // 未抓取的条数

	public AnalyseJrjFactory(DataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	public void exec(int totalPage, String description, String CommandName) {
		this.CommandName = CommandName;
		dataProvider.Open();
		// http://stock.jrj.com.cn/xwk/201511/20151130_1.shtml
		for (int i = 0; i < totalPage; i++) {
			if (total >= totalPage) {
				break;
			}
			String dateStr = DateUtil.GetDateTimeByPattern(new Date(), "yyyyMM", i);
			String dateTimeStr = DateUtil.GetDateTimeByPattern(new Date(), "yyyyMMdd", i);

			ExtractHtmlContent(netUrl + dateStr + "/" + dateTimeStr, totalPage);
		}
		logger.info(description + " 共抓取url数量:" + inUrlCount + "条,未抓取：" + outUrlCount + "条");
		dataProvider.Close();

	}

	private void ExtractHtmlContent(String urlRoot, int totalPage) {
		String url = "";
		for (int i = 1; i <= totalPage; i++) {

			url = urlRoot + "_" + i + ".shtml";

			if (total >= totalPage) {
				break;
			}
			try {
				Document document = HttpClientManage.GetInstance().GetDocument(url, "gb2312", 5000, 10000);
				Elements les = document.select("ul.list>li");
				if (les.size() > 0) {
					total = total + 1;
				}
				for (int j = 0; j < les.size(); j++) {
					if (les.get(j).select("a").size() > 0) {
						String urlPath = les.get(j).select("a").get(1).attr("href");// 链接
						String urlName = les.get(j).select("a").get(1).text();// 链接的内容
						getURLWords(urlPath, url + "@" + j);
					}
				}
				if ("下一页".equals(document.getElementsByClass("none").text().trim())) {
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
	}

	public void getURLWords(String urlPath, String parentUrlAndOrder) {

		Message message = new Message();
		message.SiteName = "金融界";
		message.SpiderName = this.CommandName;
		message.InternalCategory = "网络媒体_综合门户_金融界";

		message.Timestamp = System.currentTimeMillis();
		message.Url = urlPath;
		message.UrlHash = HelperUtil.ToMd5(message.Url);
		message.contentType = ContentType.Article;

		Article article = new Article();

		try {
			Document doc = HttpClientManage.GetInstance().GetDocument(urlPath, "gbk", 5000, 10000);
			if (doc == null) {
				outUrlCount = outUrlCount + 1;
				return;
			}
			Elements les = doc.select("p.inftop>span");
			System.out.println(doc.text());
			if (les.size() > 2) {
				article.PublishTime = les.get(0).text().trim();
				article.Referrer = les.get(1).text().substring(3);
				if (les.get(2).text().contains("作者")) {
					article.Author = les.get(2).text().substring(3);
				}
				message.ExternalCategory = doc.select("div.cbox").text().replace(">", "_").replace("_ 正文", "");
				article.Title = doc.select("div.titmain>h1").text().trim();
				article.ContentText = doc.getElementsByClass("texttit_m1").text().trim();
				article.ContentHtml = doc.getElementsByClass("texttit_m1").html();

			} else {
				article.PublishTime = doc.getElementById("pubtime_baidu").text();
				article.Referrer = doc.getElementById("source_baidu").text().substring(3).trim();
				if (doc.getElementById("author_baidu").text().contains("作者")) {
					article.Author = doc.getElementById("author_baidu").text().substring(3);
				}
				message.ExternalCategory = doc.select("div.newsGuide").text().replace(">", "_").replace("_ 正文", "");
				article.Title = doc.getElementsByClass("newsLeft").text();
				article.ContentText = doc.select("div.textmain.tmf14.jrj-clear").text();
				article.ContentHtml = doc.select("div.textmain.tmf14.jrj-clear").html();

			}

			article.CommentCount = 0;
			article.ZhuanFaCount = 0;
			article.PraiseCount = 0;
			article.ReadCount = 0;
			message.content = article;
			article.PublishTime = DateUtil.toDateTime(article.PublishTime, "yyyy-MM-dd HH:mm");
			article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);
			inUrlCount = inUrlCount + 1;
			dataProvider.Send(message);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
}
