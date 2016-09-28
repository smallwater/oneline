package com.smart.spider.hexun;

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

public class HexunStockCommandService extends NewsSiteCommandService {

	private List<String> urlList = new ArrayList<String>();
	private int maxPageCount = 100;

	public HexunStockCommandService() {

		CommandName = "com.smart.spider.hexun.news";

		Description = "和讯新闻";

		Author = "smart";

	}

	@Override
	public void Init(String[] args) {

		/**
		 * 个股-股票频道-和讯网 上市公司-股票频道-和讯网 行业-股票频道-和讯网
		 */
		urlList.add("http://stock.hexun.com/stock");
		urlList.add("http://stock.hexun.com/company");
		urlList.add("http://stock.hexun.com/industry");
		urlList.add("http://stock.hexun.com/zhul");
		urlList.add("http://stock.hexun.com/market");
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

		String htmlContent = HttpClientManage.GetInstance().GetRequest(url, "GBK");

		Document document = Jsoup.parse(htmlContent);

		if (document.select("div.stockul").size() > 0 && document.select("div.stockul") != null) {

			if (document.select("div.stockul").select("div.fr").size() > 0
					&& document.select("div.stockul").select("div.fr") != null) {

				Elements urles = document.select("div.stockul").select("div.fr");

				for (int i = 0; i < urles.size(); i++) {

					if (urles.get(i).select("a").size() > 0) {

						String subUrl = urles.get(i).select("a").attr("href");

						this.getURLpageLists(subUrl);
					}

				}
			}

		}

	}

	private void getURLpageLists(String subUrl) {

		HexunStockUtil pageInfo = new HexunStockUtil();

		Document document = Jsoup.parse(HttpClientManage.GetInstance().GetRequest(subUrl, "gb2312"));

		String mixMes = document.select("div.breadcrumbs>div").get(0).select("a").text().replaceAll(" ", "_");

		pageInfo.setUrl(subUrl);
		pageInfo.init();
		String articleUrl = pageInfo.Url;

		pageInfo.next();

		while (StringUtils.isNotEmpty(articleUrl)) {

			try {

				getArticleList(articleUrl, mixMes);

			} catch (Exception e) {

				e.printStackTrace();

			} finally {

				articleUrl = pageInfo.next();

			}
		}

	}

	private void getArticleList(String articleUrl, String mixMes) {
		Document document = Jsoup.parse(HttpClientManage.GetInstance().GetRequest(articleUrl, "gb2312"));
		Elements hrefList = document.select("div.mainboxcontent").select("div").get(1).select("div.temp01")
				.select("ul");
		for (int j = 0; j < hrefList.size(); j++) {
			Elements mixList = hrefList.get(j).select("li");
			for (int i = 0; i < mixList.size(); i++) {
				getURLWords(mixList.get(i).select("a").attr("href"), mixList.get(i).select("a").text(), mixMes);
			}
		}

	}

	private void getURLWords(String urlPath, String urlname, String mixMes) {
		Document document = Jsoup.parse(HttpClientManage.GetInstance().GetRequest(urlPath, "gb2312"));
		if (null != document || !("").equals(document)) {

			try {
				String words = "";
				String words1 = "";
				String date_form = "";
				String dateline = "";
				if (stokOrTg(urlPath)) {// 获取方式不一样，stock

					if (document.select("div.title_desc").size() > 0 && document.select("div.title_desc") != null) {
						if (document.select("div.title_desc>span#artibodyDesc").size() > 0
								&& document.select("div.title_desc>span#artibodyDesc") != null) {
							date_form = document.select("div.title_desc>span#artibodyDesc").text();

							String[] temp1 = date_form.split("来源：");
							dateline = temp1[0].trim();// 获取发布时间
							if (temp1.length >= 2) {
								String fromPath = temp1[1].trim();// 获取发布来源

								String[] _t = fromPath.split("作者");
								if (_t.length >= 1) {
									fromPath = _t[0].trim() != null ? _t[0].trim() : "";
								}
							}
							words = document.select("div.art_context").text();
							words1 = document.select("div.art_context").html();
						}
					}
				} else {
					if (document.select("div.newsCon>div").size() > 0 && (document.select("div.newsCon>div")) != null) {
						String _s = document.select("div.newsCon>div").get(0).text();
						if (_s.contains("阅读")) {
							String[] temp2 = _s.split("阅读");
							if (temp2.length >= 2) {
								dateline = temp2[1].trim() != null ? temp2[1].trim() : "";// 获取发布时间
							}
						}
					}
					words = document.select(".newsBox").select(".mt20").select("p").text();
				}

				Message message = new Message();
				message.SiteName = "和讯";
				message.SpiderName = this.CommandName;
				message.InternalCategory = "网络媒体_综合门户_和讯新闻网";
				message.Timestamp = System.currentTimeMillis();
				message.contentType = ContentType.Article;
				message.ExternalCategory = mixMes;

				Article article = new Article();

				article.Title = urlname;
				message.Url = urlPath;
				message.UrlHash = HelperUtil.ToMd5(message.Url);

				// 2015-11-26 14:57:59
				{
					String publishTimeText = dateline.replaceAll("[年月]", "-").replace("日", " ");
					if(publishTimeText.split(":").length<=2){
						publishTimeText = publishTimeText+":01";
					}
					article.PublishTime = DateUtil.toDateTime(publishTimeText, "yyyy-MM-dd HH:mm:ss");
					article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);
				}
				article.ContentText = words;
				article.ContentHtml = words1;

				article.CommentCount = 0;
				article.ZhuanFaCount = 0;
				article.PraiseCount = 0;
				article.ReadCount = 0;

				message.content = article;

				System.out.println(article);

				dataProvider.Send(message);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean stokOrTg(String urlPath) {

		if (urlPath.substring(7, urlPath.indexOf(".")).equals("stock")) {

			return true;

		}

		return false;
	}

}
