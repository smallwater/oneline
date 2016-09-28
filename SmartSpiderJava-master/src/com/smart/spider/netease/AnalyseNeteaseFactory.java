package com.smart.spider.netease;

import org.apache.log4j.LogManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.smart.spider.data.DataProvider;
import com.smart.spider.data.meta.Article;
import com.smart.spider.data.meta.ContentType;
import com.smart.spider.data.meta.Message;
import com.smart.spider.http.HttpClientManage;
import com.smart.spider.util.DateUtil;
import com.smart.spider.util.HelperUtil;

/**
 * @author 用于处理全景网的页面处理
 *
 */
public class AnalyseNeteaseFactory {

	private final org.apache.log4j.Logger logger = LogManager.getLogger(AnalyseNeteaseFactory.class);
	private DataProvider dataProvider;
	private String netUrl = "http://money.163.com/special/";
	private String CommandName = "";
	private int inUrlCount = 0; // 抓取的条数
	private int outUrlCount = 0; // 未抓取的条数

	public AnalyseNeteaseFactory(DataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	public void getHtmlText(String urlAbs, int totalPage, String description, String CommandName) {
		this.CommandName = CommandName;
		dataProvider.Open();

		String[] arrUrl = urlAbs.split(",");

		for (int i = 0; i < arrUrl.length; i++) {

			ExtractHtmlContent(netUrl + arrUrl[i], totalPage);

		}
		logger.info(description + " 共抓取url数量:" + inUrlCount + "条,未抓取：" + outUrlCount + "条");
		dataProvider.Close();

	}

	private void ExtractHtmlContent(String urlRoot, int totalPage) {
		boolean isEndFalg = true;
		String url = "";
		for (int i = 0; i <= totalPage; i++) {

			try {

				if (i == 0) {
					url = urlRoot + ".html";
				} else if (i == 1) {
					continue;
				} else if (i < 10) {
					url = urlRoot + "_0" + i + ".html";
				} else {
					url = urlRoot + "_" + i + ".html";
				}
				if (isEndFalg) {
					Document document = HttpClientManage.GetInstance().GetDocument(url, "gb2312", 5000, 10000);
					if (document == null) {
						outUrlCount = outUrlCount + 20;
						return;
					}
					Elements pageEnd = document.getElementsByAttributeValue("title", "最后一页");
					Elements pageEnd2 = document.select("div.pageList.lrBothMar4.martop15");
					if (pageEnd2.size() > 0) {
						if ("#".equals(pageEnd2.select("a").eq(pageEnd2.select("a").size() - 1).attr("href"))) {
							isEndFalg = false;
						}
						;
					}

					if (pageEnd.size() == 0 && pageEnd2.size() == 0) {
						isEndFalg = false;
					}
					Elements titles = document.getElementsByClass("item_top");
					String headTemp = document.select("div.nav_cur_index").text().replaceAll(">", "_").replaceAll(" ",
							"");
					if (titles.size() == 0) {
						titles = document.getElementsByClass("article");
						headTemp = document.select("p.navText").text().replaceAll(">", "_").replaceAll(" ", "");
					}
					for (int j = 0; j < titles.size(); j++) {
						String urlPath = titles.get(j).select("a").attr("href");// 链接
						String urlName = titles.get(j).select("a").text();// 链接的内容
						getURLWords(urlPath, urlName, headTemp);
					}
				} else {
					outUrlCount = outUrlCount + 1;
					break;
				}
			} catch (Exception e) {
				logger.error(e + "@" + url);
			}
		}
	}

	/**
	 * @Title: getURLWords @Description: TODO @param urlPath 网页路径 @param
	 *         headTemp 输出头 @param parentUrl 父页面 +@+第几条 方便查找报错页面是否解析错误 @return
	 *         void @throws
	 */

	public void getURLWords(String urlPath, String urlName, String headTemp) {

		Message message = new Message();
		message.SiteName = "网易";
		message.SpiderName = CommandName;
		message.InternalCategory = "网络媒体_综合门户_网易";
		message.ExternalCategory = headTemp;
		message.Timestamp = System.currentTimeMillis();
		message.Url = urlPath;
		message.UrlHash = HelperUtil.ToMd5(message.Url);
		message.contentType = ContentType.Article;

		Article article = new Article();

		try {

			Document docHtml = HttpClientManage.GetInstance().GetDocument(urlPath, "gb2312", 5000, 10000);

			if (docHtml == null) {
				outUrlCount = outUrlCount + 1;
				return;
			}

			// 页面404，图片新闻 去除
			if (!urlPath.contains("photoview") && "".equals(docHtml.select("div.er-cnt").text())
					&& !docHtml.html().contains("http://money.163.com/special/special.html")) {

				if (docHtml.select("div.ep-time-soure.cDGray").size() != 0) {
					article.Referrer = docHtml.select("div.ep-time-soure.cDGray").text().split("来源:")[1];// 获取发布来源
					article.PublishTime = docHtml.select("div.ep-time-soure.cDGray").text().split("来源:")[0];
					article.Title = docHtml.select("#h1title").text();
					/*
					 * article.Author=doc.select("div.title_3").text().split(
					 * "作者")[1 ].substring(1).trim();
					 */
					article.ContentText = docHtml.getElementsByClass("end-Text").text();
					article.ContentHtml = docHtml.getElementsByClass("end-Text").html();
				} else {
					article.Author = docHtml.select("meta[name=author]").attr("content");

					article.Referrer = "网易博客";

					// 发布时间
					{
						String publishTimeText = docHtml.select("span.pleft").text();
						if (null != publishTimeText && !"".equals(publishTimeText) && publishTimeText.length() >= 19) {
							publishTimeText = publishTimeText.substring(0, 19);
						}
						article.PublishTime = publishTimeText;
					}

					article.Title = docHtml.select("span.tcnt").text();
					article.ContentText = Jsoup
							.parse(docHtml.getElementsByAttributeValue("name", "content").attr("value")).text();
					article.ContentHtml = Jsoup
							.parse(docHtml.getElementsByAttributeValue("name", "content").attr("value")).select("body")
							.html();
				}

				article.CommentCount = 0;
				article.ZhuanFaCount = 0;
				article.PraiseCount = 0;
				article.ReadCount = 0;

				{
					// 2015-11-26 15:18:13
					article.PublishTime = DateUtil.toDateTime(article.PublishTime);
					article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);
				}

				message.content = article;
				inUrlCount = inUrlCount + 1;
				// logger.info(message.ExternalCategory+"@"+urlPath+"@"+article.Referrer+"@"+article.PublishTime+"@"+article.Author+"@"+article.Title+"@"+article.ContentText.substring(0,
				// 50));
				dataProvider.Send(message);
			}
		} catch (Exception e) {
			logger.error(e + "@" + urlPath);
		}
	}

	/**
	 * @Title: subStrUrl @Description: 截取 / 方法 @param @param url @param @param
	 *         count @param @return @return String @throws
	 */

	public String subStrUrl(String url, int count) {

		String str = url.substring(0, url.lastIndexOf("/"));

		if (count > 0) {
			return subStrUrl(str, --count);
		}

		return url;
	}
}
