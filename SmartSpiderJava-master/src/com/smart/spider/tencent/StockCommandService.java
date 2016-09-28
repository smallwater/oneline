package com.smart.spider.tencent;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.smart.spider.data.meta.Article;
import com.smart.spider.data.meta.ContentType;
import com.smart.spider.data.meta.Message;
import com.smart.spider.http.HttpClientManage;
import com.smart.spider.util.DateUtil;
import com.smart.spider.util.HelperUtil;
import com.smart.spider.util.NewsSiteCommandService;

public class StockCommandService extends NewsSiteCommandService {

	private int beginPage = 1;
	private int crawl_page, lastPage = 0;// 爬取页面 截止页面
	private int endPage = 45;
	private int Counturl = 0; // 计数器

	// 链接地址， 链接标题， 发布时间 文章来源
	private String urlTitle, dateline, articlePath, author = "";

	/***
	 * 
	 * <p>
	 * Title: firstlist
	 * </p>
	 * <p>
	 * Description:获得网页中更多的url，一级目录
	 * </p>
	 * 
	 * @throws Exception
	 * 
	 * @throws IOException
	 */
	public void domain(String url) throws Exception {

		Document doc = HttpClientManage.GetInstance().GetDocument(url, "gb2312");

		Elements urles = doc.select("div.mini_nav").select("li");

		for (int j = 1; j <= 5; j++) {
			if (j == 2) {
				continue;
			}
			String urls = urles.get(j).select("a").attr("href");
			getListPage(urls);
		}
	}

	/**
	 * 获取一级目录中的列表底部页码url，二级目录
	 * 
	 * @param url
	 * @throws IOException
	 */
	public void getListPage(String listurl) {

		try {
			// 爬取页码，如果为0则爬取所有，如果不为0则抓取指定页数
			if (crawl_page > 0) {
				lastPage = crawl_page;
			} else {
				// 指定的列表的最后一页
				lastPage = endPage;
			}

			for (int i = beginPage; i <= lastPage; i++) {

				if (i > 1) {
					getListUrl(listurl.substring(0, listurl.length() - 4) + "_" + i + ".htm");
				} else {
					getListUrl(listurl);
				}
			}
		} catch (Exception e) {
			logger.error("腾讯证券_要闻/大盘/板块/公司-->" + e);
		}
	}

	/**
	 * 获取二级目录中的列表url，三级目录
	 * 
	 * @param articleUrl
	 *            获取文章列表,当前列表的的每个url
	 * @param mixMes
	 * @throws IOException
	 */
	public void getListUrl(String listurl) {

		try {
			Document doc = HttpClientManage.GetInstance().GetDocument(listurl, "gb2312", 5000, 5000);

			Elements hrefList = doc.select("div.mod.newslist").select("a");

			for (int i = 0; i < hrefList.size(); i++) {

				String htmlurl = hrefList.get(i).attr("href");

				if (htmlAnalysis(htmlurl) == false) {
					continue;
				}

			}
		} catch (Exception e) {
			logger.error("腾讯证券_要闻/大盘/板块/公司-->" + e);
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

			Document doc = HttpClientManage.GetInstance().GetDocument(urlPath, "gb2312", 3000, 3000);

			// 页面不存在
			if (doc == null) {
				return false;
			}

			if (doc.select("title").text().equals("404-1")) {
				return false;
			}

			// 限制502 Bad Gateway
			if (doc.select("title").text().equals("502 Bad Gateway")) {
				return false;
			}

			// 404您访问的页面找不回来了！
			if (doc.select("title").select("h1").text().equals("404您访问的页面找不回来了！")) {
				return false;
			}

			// 发布时间第一规则
			dateline = doc.select("div#C-Main-Article-QQ").select("span.pubTime.article-time").text();

			// 发布时间第二规则
			if (dateline.equals("")) {
				dateline = doc.select("div#C-Main-Article-QQ").select("span.pubTime").text();
			}
			dateline = dateline.replaceAll("日", "").replaceAll("[年月]", "-");

			// 文章标题
			urlTitle = doc.select("div#C-Main-Article-QQ").select("div.hd").select("h1").text();

			// 文章作者第一规则
			author = doc.select("div#C-Main-Article-QQ").select("div.hd").select("span.auth.color-a-3").select("a")
					.text();
			// 文章作者第二规则
			if (author.equals("")) {
				author = doc.select("div#C-Main-Article-QQ").select("span.auth").text();
			}

			// 文章来源第一规则
			articlePath = doc.select("div#C-Main-Article-QQ").select("div.hd").select("span.where.color-a-1")
					.select("a").text();

			// 文章来源第二规则
			if (articlePath.equals("")) {
				articlePath = doc.select("div#C-Main-Article-QQ").select("div.hd").select("span.where.color-a-1")
						.text();
			}

			// 文章来源第三规则
			if (articlePath.equals("")) {
				articlePath = doc.select("div#C-Main-Article-QQ").select("span.where").text();
			}

			// 文章内容
			int docSize = doc.select("div#C-Main-Article-QQ").select("div#Cnt-Main-Article-QQ").select("p").size();
			for (int i = 0; i < docSize; i++) {
				sbwords.append(doc.select("div#C-Main-Article-QQ").select("div#Cnt-Main-Article-QQ").select("p").get(i)
						.text().trim());
			}

			// 文章内容html
			String sbwordshtml = doc.select("div#C-Main-Article-QQ").select("div#Cnt-Main-Article-QQ").toString();

			if (articlePath.equals("") || dateline.equals("") || sbwords.toString().equals("")) {
				return false;
			}

			Message message = new Message();
			message.SiteName = "腾讯证券";
			message.SpiderName = this.CommandName;
			message.InternalCategory = "网络媒体_综合门户_腾讯_腾讯证券_要闻/大盘/板块/公司";
			message.ExternalCategory = "腾讯证券_要闻/大盘/板块/公司";
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
			dateline = regurlTime(dateline);
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

	public StockCommandService() {

		this.CommandName = "com.smart.spider.tencent.stock";
		this.Description = "腾讯证券_要闻/大盘/板块/公司";
		this.Author = "smart";

	}

	public String regurlTime(String time) {

		String timesub = "";

		// 研报标准页面过滤
		Pattern regex = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}[0-9]{2}:[0-9]{2}[:0-9]*", Pattern.CASE_INSENSITIVE);

		Matcher matcher = regex.matcher(time);

		if (matcher.find()) {
			timesub = matcher.group(0);
			return matcher.group(0).subSequence(0, 10) + " " + timesub.subSequence(10, timesub.length()) + ":00";
		}

		return timesub = time;
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

			domain("http://stock.qq.com/");

		} catch (Exception e) {
			logger.error(e);
		}

		long endTime = System.currentTimeMillis();
		long runTime = (endTime - startTime) / 1000 / 60;
		dataProvider.Close();

	}
}
