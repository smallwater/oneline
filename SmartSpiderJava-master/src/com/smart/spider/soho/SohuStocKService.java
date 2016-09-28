package com.smart.spider.soho;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.smart.spider.data.meta.Article;
import com.smart.spider.data.meta.ContentType;
import com.smart.spider.data.meta.Message;
import com.smart.spider.http.HttpClientManage;
import com.smart.spider.util.DateUtil;
import com.smart.spider.util.HelperUtil;
import com.smart.spider.util.NewsSiteCommandService;

public class SohuStocKService extends NewsSiteCommandService {

	private List<String> urlList = new ArrayList<String>();

	private int beginPage = 1;

	private int endPage = 100;

	private int crawl_page, lastPage = 0;// 爬取页面 截止页面

	// 链接地址， 链接标题， 发布时间， 文章来源
	private String urlPath, urlTitle, publishTime, articlePath, author = "";

	/**
	 * 获取要腾讯财经公司中需要爬取的页面url
	 * 
	 * @param url
	 * @throws IOException
	 */
	public void getListPages(String url) {

		try {

			String listPage = "";

			int sohuLastPageNums = SohuUtils.GetInstance().SohuLastPageNums(url);

			int index = sohuLastPageNums;

			// 爬取页码，如果为0则爬取所有，如果不为0则抓取指定页数
			if (crawl_page > 0 && crawl_page < endPage) {
				lastPage = crawl_page;
			} else {
				// 指定的列表的最后一页
				lastPage = endPage;
			}

			for (int i = sohuLastPageNums; i >= 1; i--) {
				if (sohuLastPageNums == index) {
					listPage = url + "index.shtml";
				} else {
					listPage = url + "index_" + sohuLastPageNums + ".shtml";
				}
				sohuLastPageNums--;

				if (lastPage == 0) {
					break;
				}

				SohuUtils.GetInstance().WriteText(listPage.toString());

				Document doc = HttpClientManage.GetInstance().GetDocument(listPage, "GBK");

				if (doc == null) {
					continue;
				}

				Elements mixMes = doc.select("div.f14list").select("a");

				for (int j = 0; j < mixMes.size(); j++) {

					urlPath = mixMes.get(j).attr("href");

					SohuUtils.GetInstance().WriteText(urlPath.toString());

					if (htmlAnalysis(urlPath) == false) {
						continue;
					}

				}

				lastPage--;
			}

		} catch (Exception e) {

			logger.error(e);

		}
	}

	/***
	 * 
	 * <p>
	 * Title: htmlAnalysis
	 * </p>
	 * <p>
	 * Description: 网页内容的解析
	 * </p>
	 * 
	 * @param url
	 * @throws IOException
	 */
	public boolean htmlAnalysis(String urlPath) throws Exception {

		StringBuffer sbwords = new StringBuffer(); // 文章内容

		Document doc = HttpClientManage.GetInstance().GetDocument(urlPath, "gb2312");

		if (doc == null) {
			return false;
		}

		// 发布时间
		publishTime = doc.select("div#pubtime_baidu").text().replaceAll("日", "").replaceAll("[年月]", "-");
		if (publishTime.equals("")) {
			publishTime = "";
		}

		// 文章标题
		urlTitle = doc.select("div.content-box.clear").select("h1").text();
		if (urlTitle.equals("")) {
			urlTitle = "";
		}

		// 文章来源
		articlePath = doc.select("div.source").select("span#source_baidu").text().split("：")[1];
		if (articlePath.equals("")) {
			articlePath = "";
		}

		// 文章内容第一规则
		int docSize = doc.select("div#contentText").select("p").size();

		if (docSize != 0) {
			for (int i = 0; i < docSize; i++) {
				sbwords.append(doc.select("div#contentText").select("p").get(i).text().trim());
			}
		}

		// 文章内容第二规则

		sbwords.append(doc.select("div#contentText"));
		if (sbwords.equals("")) {

		}

		System.out.println(sbwords);

		// 文章内容html
		String sbwordshtml = doc.select("div#contentText").toString();

		// 文章作者第一规则
		if (!doc.select("div.source").select("span#author_baidu").text().equals("")) {
			author = doc.select("div.source").select("span#author_baidu").text().split("：")[1].toString();
		} else {
			// 文章作者第二规则
			if (!doc.select("div#contentText").select("p").get(docSize - 1).text().equals("")) {
				author = doc.select("div#contentText").select("p").get(docSize - 1).text();
				if (author.length() < 10) {
					author.subSequence(author.indexOf("：") + 3, author.length());
				} else {
					author = "";
				}
			}
		}

		Message message = new Message();
		message.SiteName = "搜狐证券";
		message.SpiderName = this.CommandName;
		message.InternalCategory = "网络媒体_综合门户_搜狐_搜狐证券_要闻";
		message.ExternalCategory = "搜狐证券_要闻";
		message.Timestamp = System.currentTimeMillis();
		message.Url = urlPath;
		message.UrlHash = HelperUtil.ToMd5(message.Url);
		message.contentType = ContentType.Article;

		Article article = new Article();
		article.Title = urlTitle;
		article.Author = author;
		article.Referrer = articlePath;
		article.ContentText = sbwords.toString();
		article.ContentHtml = sbwordshtml;
		article.CommentCount = 0;
		article.ZhuanFaCount = 0;
		article.PraiseCount = 0;
		article.ReadCount = 0;
		article.PublishTime = DateUtil.toDateTime(publishTime, "yyyy-MM-dd HH:mm");
		article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);

		message.content = article;

		dataProvider.Send(message);

		return true;
	}

	public SohuStocKService() {

		this.CommandName = "com.smart.spider.soho.stock";
		this.Description = "搜狐证券_要闻";
		this.Author = "smart";

	}

	@Override
	public void Init(String[] args) {

		// 搜狐证券_要闻
		urlList.add("http://stock.sohu.com/news/");
		// urlList.add("http://stock.sohu.com/kuaixun/");

	}

	@Override
	public void Exec(String[] args) {

		if (null != args && args.length > 1) {

			try {

				crawl_page = Integer.parseInt(args[1]);

			} catch (Exception e) {

				e.printStackTrace();

			}

		}

		dataProvider.Open();

		for (String url : urlList) {

			try {

				getListPages(url);

			} catch (Exception e) {
				logger.error(e);
			}
		}

		dataProvider.Close();
	}
}