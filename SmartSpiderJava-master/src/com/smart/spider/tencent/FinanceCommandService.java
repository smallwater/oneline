package com.smart.spider.tencent;

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

public class FinanceCommandService extends NewsSiteCommandService {

	private List<String> urlList = new ArrayList<String>();

	private int beginPage = 1;

	private int endPage = 42;

	private int crawl_page, lastPage = 0;// 爬取页面 截止页面

	private int Counturl = 0; // 计数器

	// 链接地址， 链接标题， 发布时间， 文章来源
	private String urlPath, urlTitle, dateline, articlePath, author = "";

	/**
	 * 获取要腾讯财经公司中需要爬取的页面url
	 * 
	 * @param url
	 * @throws IOException
	 */
	public void getListPage(String url) {

		try {

			// 爬取页码，如果为0则爬取所有，如果不为0则抓取指定页数
			if (crawl_page > 0) {
				lastPage = crawl_page;
			} else {
				// 指定的列表的最后一页
				lastPage = endPage;
			}

			for (int i = beginPage; i <= lastPage; i++) {

				String listPage = url + i + ".htm";

				Document doc = HttpClientManage.GetInstance().GetDocument(listPage, "gb2312", 5000, 5000);

				Elements mixMes = doc.select("a");

				if (mixMes.size() < 5) {
					continue;
				}

				for (int j = 0; j < mixMes.size(); j++) {

					String aurl = mixMes.get(j).attr("href");

					urlPath = "http://finance.qq.com" + aurl.toString();

					if (htmlAnalysis(urlPath) == false) {
						continue;
					}

				}
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
	public boolean htmlAnalysis(String urlPath) {

		try {

			StringBuffer sbwords = new StringBuffer(); // 文章内容

			Document doc = HttpClientManage.GetInstance().GetDocument(urlPath, "gb2312", 5000, 5000);

			// 页面不存在
			if (doc == null) {
				return false;
			}
			if (doc.select("title").text().equals("502 Bad Gateway")) {
				return false;
			}
			if (doc.select("title").select("h1").text().equals("404您访问的页面找不回来了！")) {
				return false;
			}

			// 发布时间
			dateline = doc.select("div#C-Main-Article-QQ").select("span.pubTime.article-time").text();
			if (dateline.equals("")) {
				dateline = "";
			}
			dateline = dateline.replaceAll("日", "").replaceAll("[年月]", "-");

			// 文章作者
			author = doc.select("div#C-Main-Article-QQ").select("div.hd").select("span.auth.color-a-3").select("a")
					.text();
			if (author.equals("")) {
				author = "";
			}

			// 文章标题
			urlTitle = doc.select("div#C-Main-Article-QQ").select("div.hd").select("h1").text();

			// 文章来源第一规则
			articlePath = doc.select("div#C-Main-Article-QQ").select("div.hd").select("span.where.color-a-1")
					.select("a").text();

			// 文章来源第二规则
			if (articlePath.equals("")) {
				articlePath = doc.select("div#C-Main-Article-QQ").select("div.hd").select("span.where.color-a-1")
						.text();
			}

			// 文章内容
			int docSize = doc.select("div#C-Main-Article-QQ").select("div#Cnt-Main-Article-QQ").select("p").size();
			for (int i = 0; i < docSize; i++) {
				sbwords.append(doc.select("div#C-Main-Article-QQ").select("div#Cnt-Main-Article-QQ").select("p").get(i)
						.text().trim());
			}

			// 文章内容html
			String sbwordshtml = doc.select("div#C-Main-Article-QQ").select("div#Cnt-Main-Article-QQ").toString();

			if (articlePath.equals("") || dateline.equals("") || sbwords.toString().equals("")
					|| sbwordshtml.equals("")) {
				return false;
			}

			Message message = new Message();
			message.SiteName = "腾讯财经";
			message.SpiderName = this.CommandName;
			message.InternalCategory = "网络媒体_综合门户_腾讯_腾讯财经_公司/宏观/金融";
			message.ExternalCategory = "腾讯财经_公司/宏观/金融";
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
			article.PublishTime = DateUtil.toDateTime(dateline, "yyyy-MM-dd HH:mm");
			article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);

			message.content = article;

			Counturl++;
			dataProvider.Send(message);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public FinanceCommandService() {

		this.CommandName = "com.smart.spider.tencent.ghj";
		this.Description = "腾讯财经_公司/宏观/金融";
		this.Author = "smart";

	}

	@Override
	public void Init(String[] args) {

		// 公司 ， 宏观 ， 金融
		urlList.add("http://finance.qq.com/c/gsbdlist_");
		urlList.add("http://finance.qq.com/c/hgjjllist_");
		urlList.add("http://finance.qq.com/c/jrscllist_");

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
		long startTime = System.currentTimeMillis();

		for (String url : urlList) {

			try {

				getListPage(url);

			} catch (Exception e) {
				logger.error(e);
			}
		}

		long endTime = System.currentTimeMillis();
		long runTime = (endTime - startTime) / 1000 / 60;
		dataProvider.Close();
	}
}