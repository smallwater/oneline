package com.smart.spider.tencent;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;

import com.smart.spider.data.meta.Article;
import com.smart.spider.data.meta.ContentType;
import com.smart.spider.data.meta.Message;
import com.smart.spider.http.*;
import com.smart.spider.util.DateUtil;
import com.smart.spider.util.HelperUtil;
import com.smart.spider.util.NewsSiteCommandService;

public class FinanceGunDongCommandService extends NewsSiteCommandService {

	private int beginPage = 1;
	private int endPage = 44;
	private int crawl_page, lastPage = 0;
	private int Counturl = 0; // 计数器

	// 当前网址， 文章标题， 发布时间， 作者， 采集时间 文章来源
	private String urlPath, urlTitle, dateline, author, articlePath = "";

	/**
	 * 获取要腾讯滚动中需要爬取的页面url
	 * 
	 * @param url
	 * @throws IOException
	 */
	public void getListPage() {

		try {
			// 爬取页码，如果为0则爬取所有，如果不为0则抓取指定页数
			if (crawl_page > 0) {
				lastPage = crawl_page;
			} else {
				// 指定的列表的最后一页
				lastPage = endPage;
			}

			for (int i = beginPage; i <= lastPage; i++) {

				String listPage = "http://roll.finance.qq.com/interface/roll.php?0.17304278223842617&cata=&site=finance&date=&page="
						+ i + "&mode=1&of=json";

				String listPageValue = TencentUtils.GetInstance().rollGetRequest(listPage);

				Matcher m = Pattern.compile("http[\\s\\S]+?.htm").matcher(listPageValue);
				while (m.find()) {

					String htmlurl = m.group(0).replace("\\", "");

					if (htmlAnalysis(htmlurl) == false) {
						continue;
					}
				}
			}
		} catch (Exception e) {
		}
	}

	/***
	 * 
	 * Title: htmlAnalysis
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

			Document doc = HttpClientManage.GetInstance().GetDocument(urlPath, "GBK", 5000, 5000);

			// 页面不存在
			if (doc == null) {
				return false;
			}

			// 限制502 Bad Gateway
			if (doc.select("div.title").text().equals("502 Bad Gateway")) {
				return false;
			}

			// 404您访问的页面找不回来了！
			if (doc.select("div.title").select("h1").text().equals("404您访问的页面找不回来了！")) {
				return false;
			}
			if (doc.select("div#errorTitle").size() > 0) {
				return false;
			}

			// 发布时间
			dateline = doc.select("div#C-Main-Article-QQ").select("span.pubTime.article-time").text();
			dateline = dateline.replaceAll("日", "").replaceAll("[年月]", "-");

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

			// 网页正文获取第一规则
			int docSize = doc.select("div#Cnt-Main-Article-QQ").select("p").size();
			if (docSize == 0) {
				return false;
			}
			for (int i = 0; i < docSize; i++) {
				sbwords.append(doc.select("div#Cnt-Main-Article-QQ").select("p").get(i).text());
			}

			// 网页正文获取第二规则
			if (sbwords.toString().equals("")) {
				sbwords.append(doc.select("div#Cnt-Main-Article-QQ").select("td").text());
			}

			// 文章作者第一规则 ：判断第一个P标签
			if (doc.select("div#C-Main-Article-QQ").select("div#Cnt-Main-Article-QQ").select("p").size() > 0) {
				author = doc.select("div#C-Main-Article-QQ").select("div#Cnt-Main-Article-QQ").select("p").get(0)
						.text();
				author = author.toString().replace("（", "").replace("）", "").replace("　", "").trim();
			}

			if (author.length() > 5) {
				author = "";
			}

			// 文章作者第二规则 ：判断最后一个P标签
			if (author.equals("")) {
				author = doc.select("div#C-Main-Article-QQ").select("div#Cnt-Main-Article-QQ").select("p")
						.get(docSize - 1).text();
				author = author.toString().replace("（", "").replace("）", "").replace("　", "").trim();
				if (author.length() > 5) {
					author = "";
				}
			}

			// 文章作者第三规则
			if (author.equals("")) {
				author = doc.select("div#C-Main-Article-QQ").select("div.hd").select("span.auth.color-a-3").select("a")
						.text();
			}

			// 文章内容html
			String sbwordshtml = doc.select("div#Cnt-Main-Article-QQ").toString();

			if (articlePath.equals("") || dateline.equals("") || sbwords.toString().equals("")
					|| sbwordshtml.equals("")) {
				return false;
			}

			Message message = new Message();
			message.SiteName = "腾讯财经";
			message.SpiderName = this.CommandName;
			message.InternalCategory = "网络媒体_综合门户_腾讯_腾讯财经_滚动首页";
			message.ExternalCategory = "腾讯财经_滚动首页";
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

	public FinanceGunDongCommandService() {

		this.CommandName = "com.smart.spider.tencent.gundong";
		this.Description = "腾讯财经_滚动首页";
		this.Author = "smart";

	}

	@Override
	public void Init(String[] args) {
		// TODO Auto-generated method stub

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

		try {

			getListPage();

		} catch (Exception e) {
			logger.error(e);
		}

		long endTime = System.currentTimeMillis();
		long runTime = (endTime - startTime) / 1000 / 60;
		dataProvider.Close();

	}

}
