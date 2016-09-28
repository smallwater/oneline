package com.smart.spider.p5w;

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
 * @author 用于处理全景网的页面处理
 *
 */
public class AnalyseP5wFactory {

	private final Logger logger = LoggerFactory.getLogger(AnalyseP5wFactory.class);
	private DataProvider dataProvider;
	private String netUrl = "http://www.p5w.net/";
	private int inUrlCount = 0; // 抓取的条数
	private int outUrlCount = 0; // 未抓取的条数
	private String CommandName = "";

	public AnalyseP5wFactory(DataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	public void exec(String urlAbs, int totalPage, String description, String CommandName) {
		this.CommandName = CommandName;
		dataProvider.Open();
		String[] arrUrl = urlAbs.split(",");
		for (int i = 0; i < arrUrl.length; i++) {
			ExtractHtmlContent(netUrl + arrUrl[i] + "/", totalPage);
		}
		logger.info(CommandName + " 共抓取url数量:" + inUrlCount + "条,未抓取：" + outUrlCount + "条");
		dataProvider.Close();

	}

	private void ExtractHtmlContent(String urlRoot, int totalPage) {
		String url = "";
		boolean isNextFalg = true;// 是否存在下一页标识
		try {
			for (int i = 0; i <= totalPage - 1; i++) {
				if (i == 0) {
					url = urlRoot;
				} else {
					url = urlRoot + "index_" + i + ".htm";
				}
				String headTemp = "";
				Document document = HttpClientManage.GetInstance().GetDocument(url, "gb2312", 5000, 10000);
				if (document == null) {
					outUrlCount = outUrlCount + 20;
					continue;
				}
				if (isNextFalg) {
					if (i == Integer.valueOf(document.select("div.p5w-page").html().substring(
							document.select("div.p5w-page").html().indexOf("(") + 1,
							document.select("div.p5w-page").html().indexOf(","))) - 1) {
						isNextFalg = false;
					}
					headTemp = document.select("div.mbx").text().replaceAll(">", "_").replaceAll(" ", "");
					if ("".equals(headTemp)) {
						headTemp = document.select("div.dd").text().replaceAll(">", "_").replaceAll(" ", "");
					}
					for (int k = 0; k < document.getElementsByClass("sto-left-list").size(); k++) {
						Elements les = document.getElementsByClass("sto-left-list").get(k).select("li");
						for (int j = 0; j < les.size(); j++) {
							String urlPath = les.get(j).select("a").attr("href");// 链接
							String urlName = les.get(j).select("a").text();// 链接的内容
							if (urlPath.contains("http://")) {
								getURLWords(urlPath, headTemp, url + "@" + j);
							} else if (urlPath.indexOf("../../../") != -1) {
								getURLWords(subStrUrl(url, 4) + urlPath.substring(8), headTemp,
										url + "第" + (j + 1) + "条 " + urlName);
							} else if (urlPath.indexOf("../../") != -1) {
								getURLWords(subStrUrl(url, 3) + urlPath.substring(5), headTemp,
										url + "第" + (j + 1) + "条 " + urlName);
							} else if (urlPath.indexOf("../") != -1) {
								getURLWords(subStrUrl(url, 2) + urlPath.substring(2), headTemp,
										url + "第" + (j + 1) + "条 " + urlName);
							} else if (urlPath.indexOf("./") != -1) {
								getURLWords(subStrUrl(url, 1) + urlPath.substring(1), headTemp,
										url + "第" + (j + 1) + "条 " + urlName);
							}
						}
					}
				} else {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage() + "@url:" + url);
		}

	}

	/**
	 * @Title: getURLWords @Description: TODO @param urlPath 网页路径 @param
	 *         headTemp 输出头 @param parentUrl 父页面 +@+第几条 方便查找报错页面是否解析错误 @return
	 *         void @throws
	 */

	public void getURLWords(String urlPath, String headTemp, String parentUrlAndOrder) {
		// urlPath="http://www.p5w.net/stock/xingu/dingjia/201204/t20120410_24751.htm";
		Message message = new Message();
		message.SiteName = "全景网";
		message.SpiderName = this.CommandName;
		message.InternalCategory = "网络媒体_综合门户_全景网";
		message.ExternalCategory = headTemp;
		message.Timestamp = System.currentTimeMillis();
		message.Url = urlPath;
		message.UrlHash = HelperUtil.ToMd5(message.Url);
		message.contentType = ContentType.Article;

		Article article = new Article();

		try {
			// 页面404
			if (urlPath.indexOf("http://www.p5w.net/fund/zt") == -1
					&& urlPath.indexOf("http://www.p5w.net/special") == -1
					&& urlPath.indexOf("http://www.p5w.net/money/wylc/rel") == -1) {
				// System.out.println(urlPath+"==========================================@===========");
				Document doc = HttpClientManage.GetInstance().GetDocument(urlPath, "gb2312", 5000, 10000);
				if (doc == null) {
					outUrlCount = outUrlCount + 1;
					return;
				}
				if ("".equals(doc.select("div.bg404").text())) {

					if (!"".equals(doc.select("div.source").text().trim())) {

						article.Referrer = doc.select("div.source").text().split("发布时间")[0].split("来源：")[1];
						article.PublishTime = doc.select("div.source").text().split("发布时间")[1].substring(1, 18).trim();
						article.Author = doc.select("div.source").text().split("作者")[1].substring(1).trim();

					} else if (!"".equals(doc.select("div.title_3").text().trim())) {

						article.Referrer = doc.select("div.title_3").text().split("发布时间")[0].substring(4).trim();
						article.PublishTime = doc.select("div.title_3").text().split("发布时间")[1].substring(1, 18).trim();
						article.Author = doc.select("div.title_3").text().split("作者")[1].substring(1).trim();

					}

					article.Title = doc.getElementsByClass("title").text().trim();
					article.ContentText = doc.getElementsByClass("text").text().trim();
					article.ContentHtml = doc.getElementsByClass("text").html();
					article.CommentCount = 0;
					article.ZhuanFaCount = 0;
					article.PraiseCount = 0;
					article.ReadCount = 0;
					message.content = article;

					// 2015年11月26日 09:08
					article.PublishTime = DateUtil.toDateTime(article.PublishTime, "yyyy年MM月dd日 HH:mm");
					article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);

					inUrlCount = inUrlCount + 1;
					// logger.info(message.ExternalCategory+"@"+urlPath+"@"+article.Referrer+"@"+article.PublishTime+"@"+article.Author+"@"+article.Title+"@"+article.ContentText.substring(0,
					// 10));
					dataProvider.Send(message);

				} else {
					outUrlCount = outUrlCount + 1;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage() + urlPath);
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
