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

public class StockYanBaoCommandService extends NewsSiteCommandService {

	private int beginPage = 0;

	private int endPage = 100;

	private int crawl_page, lastPage = 0;// 爬取页面 截止页面

	private int Counturl = 0; // 计数器
	// 链接地址， 链接标题， 发布时间，文章来源
	private String urlTitle, dateline, articlePath, author = "";

	public void domain() {

		try {
			// 爬取页码，如果为0则爬取所有，如果不为0则抓取指定页数
			if (crawl_page > 0) {
				lastPage = crawl_page;
			} else {
				// 指定的列表的最后一页
				lastPage = endPage;
			}

			for (int i = beginPage; i < lastPage; i++) {
				// 投资策略
				String celvurl = "http://message.finance.qq.com/report/get_report_search.php?n=50&seq=" + i * 50
						+ "&format=json&r=0.041256827069446445";
				getListUrl(celvurl);
				// 最新研报
				String yanbaourl = "http://message.finance.qq.com/report/get_report_search.php?n=50&seq=" + i * 50
						+ "&format=json&r=0.7166092321276665";
				getListUrl(yanbaourl);
			}
		} catch (Exception e) {
			logger.error("腾讯证券_研报-->" + e);
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
	public void getListUrl(String yburl) throws Exception {

		try {
			String listPageValue = TencentUtils.GetInstance().yanbaoGetRequest(yburl);

			Matcher m = Pattern.compile("id\":\"[\\s\\S]+?\"").matcher(listPageValue);

			while (m.find()) {

				String htmlnum = m.group(0);

				if (htmlnum.length() == 13) {

					String htmlurl = "http://bbs.qq.com/finance/" + htmlnum.subSequence(5, 12) + ".html";

					if (htmlAnalysis(htmlurl) == false) {
						continue;
					}

				}
			}
		} catch (Exception e) {
			logger.error("腾讯证券_研报-->" + e);
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

			Document doc = HttpClientManage.GetInstance().GetDocument(urlPath, "gbk", 5000, 5000);

			// 页面不存在
			if (doc == null) {
				return false;
			}
			if (doc.select("title").text().equals("502 Bad Gateway")) {
				return false;
			}
			if (doc.select("title").text().equals("404 Not Found")) {
				return false;
			}
			if (doc.select("title").select("h1").text().equals("404您访问的页面找不回来了！")) {
				return false;
			}

			// 文章作者
			author = doc.select("div#contdc").select("span.lan3.uline").get(0).text();

			// 发布时间
			String datelineTemp = doc.select("div#contdc").select("h2").text();

			dateline = "";

			Matcher m = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}[:0-9]*").matcher(datelineTemp);
			while (m.find()) {
				dateline = m.group(0);
			}

			dateline = dateline.replaceAll("日", "").replaceAll("[年月]", "-");

			// 文章标题
			urlTitle = doc.select("div#main_content_div").select("span").get(0).text();

			// 文章来源
			String articlePathtemp = doc.select("div#main_content_div").select("span").get(3).text();
			articlePath = articlePathtemp.substring(articlePathtemp.indexOf("研究机构：") + 5,
					articlePathtemp.indexOf("分析师："));

			// 文章内容
			int docSize = doc.select("div#main_content_div").select("div.ArticleCnt").select("p").size();
			for (int i = 0; i < docSize; i++) {
				sbwords.append(
						doc.select("div#main_content_div").select("div.ArticleCnt").select("p").get(i).text().trim());
			}

			// 文章内容html
			String sbwordshtml = doc.select("div#main_content_div").select("div.ArticleCnt").toString();

			if (articlePath.equals("") || dateline.equals("") || sbwords.toString().equals("") || author.equals("")) {
				return false;
			}

			Message message = new Message();
			message.SiteName = "腾讯财经";
			message.SpiderName = this.CommandName;
			message.InternalCategory = "网络媒体_综合门户_腾讯_腾讯财经_研究报告";
			message.ExternalCategory = "腾讯财经_研究报告";
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

	public StockYanBaoCommandService() {

		this.CommandName = "com.smart.spider.tencent.yanbao";
		this.Description = "腾讯证券_研报";
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

			domain();

		} catch (Exception e) {
			logger.error(e);
		}

		long endTime = System.currentTimeMillis();
		long runTime = (endTime - startTime) / 1000 / 60;
		dataProvider.Close();

	}
}
