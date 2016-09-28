package com.smart.spider.sina;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.smart.spider.data.meta.Article;
import com.smart.spider.data.meta.ContentType;
import com.smart.spider.data.meta.Message;
import com.smart.spider.http.HttpClientManage;
import com.smart.spider.util.DateUtil;
import com.smart.spider.util.HelperUtil;
import com.smart.spider.util.NewsSiteCommandService;

public class SinaFinanceCommandService extends NewsSiteCommandService {

	private List<String> urlList = new ArrayList<String>();
	private int maxPageCount = 100;

	public SinaFinanceCommandService() {

		CommandName = "com.smart.spider.sina.finance";

		Description = "新浪财经新闻";

		Author = "smart";

	}

	@Override
	public void Init(String[] args) {
		/**
		 * 财经-新股-IPO被否解析 财经-新股-PE动态 财经-证券-三板市场 财经-券商-投行业界 财经-新股-研究报告定位
		 */
		urlList.add("http://roll.finance.sina.com.cn/finance/xg/ipobfjx");
		urlList.add("http://roll.finance.sina.com.cn/finance/xg/pedt");
		urlList.add("http://roll.finance.sina.com.cn/finance/zq1/sbsc");
		urlList.add("http://roll.finance.sina.com.cn/finance/qs4/txyj");
		urlList.add("http://roll.finance.sina.com.cn/finance/xg/yjbgdw");

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

	private void ExtractHtmlContent(String url) {

		for (int i = 1; i <= maxPageCount; i++) {

			url = url + "/index_" + i + ".shtml";

			if (SinaFinanceUtil.getNextPage(url)) {

				String htmlContent = HttpClientManage.GetInstance().GetRequest(url, "gb2312");

				Document document = Jsoup.parse(htmlContent);

				String mixMes = document.select("div#page>div#Main>div.Main_b1>div.crumb").text().replaceAll(">", "_")
						.replaceAll(" ", "");

				for (int k = 0; k < document.getElementsByClass("list_009").size(); k++) {

					Elements les = document.getElementsByClass("list_009").get(k).select("li");

					for (int j = 0; j < les.size(); j++) {

						String urlPath = les.get(j).select("a").attr("href");// 链接

						String urlName = les.get(j).select("a").text();// 链接的内容

						getURLWords(urlPath, urlName, mixMes);
					}
				}

			} else {

				return;
			}

		}

	}

	private void getURLWords(String urlPath, String urlName, String mixMes) {

		StringBuffer sbwords = new StringBuffer();

		StringBuffer sbwords1 = new StringBuffer();

		Message message = new Message();
		message.SiteName = "新浪财经";
		message.SpiderName = this.CommandName;
		message.InternalCategory = "网络媒体_综合门户_新浪财经";
		message.Timestamp = System.currentTimeMillis();
		message.contentType = ContentType.Article;
		message.ExternalCategory = mixMes;
		message.Url = urlPath;
		message.UrlHash = HelperUtil.ToMd5(message.Url);

		Article article = new Article();
		article.Title = urlName;
		article.CommentCount = 0;
		article.ZhuanFaCount = 0;
		article.PraiseCount = 0;
		article.ReadCount = 0;

		String htmlContent = HttpClientManage.GetInstance().GetRequest(urlPath,
				SinaFinanceUtil.connect(urlPath).charset().toString());

		Document document = Jsoup.parse(htmlContent);

		String dateline = document.select("div.artInfo>span#pub_date").text();// 获取发布时间

		if (dateline == null || dateline.equals("")) {

			dateline = document.select("div.page-info>span.time-source").text();

			dateline = dateline.split(" ")[0];

		}

		{
			// 2015-06-05 03:19
			// String publishTimeText = dateline.replaceAll("日",
			// "").replaceAll("[年月]", "-");
			String publishTimeText = dateline.replaceAll("日", "").replaceAll("[年月]", "-").replaceAll(" ", "")
					.replaceAll(" ", "");
			article.PublishTime = DateUtil.toDateTime(publishTimeText, "yyyy-MM-ddHH:mm");
			article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);
		}

		String fromPath = document.select("div.artInfo>span#media_name").text();// 获取发布来源
		if (fromPath == null || fromPath.equals("")) {
			String[] temp1 = document.select("div.page-info>span.time-source").text().split(" ");
			if (temp1.length >= 2) {
				fromPath = document.select("div.page-info>span.time-source").text().split(" ")[1];// 获取发布来源
			}
		}

		fromPath = fromPath.replaceAll("微博", "").trim();
		article.Referrer = fromPath;
		int docSize = document.select("div#artibody>p").size();

		for (int i = 1; i < docSize; i++) {
			sbwords.append(document.select("div#artibody>p").get(i).text().trim());
			sbwords1.append(document.select("div#artibody>p").get(i).html());
		}

		article.ContentText = sbwords.toString();
		article.ContentHtml = sbwords1.toString();

		message.content = article;

		if (StringUtils.isNotEmpty(article.ContentText)) {

			dataProvider.Send(message);

		}

	}

}
