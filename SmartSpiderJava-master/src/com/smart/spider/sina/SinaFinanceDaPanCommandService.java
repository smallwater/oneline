package com.smart.spider.sina;

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

public class SinaFinanceDaPanCommandService extends NewsSiteCommandService {

	// 链接地址，链接标题，发布时间，文件内容
	private String urlPath, urlTitle, dateline, articlePath = "";
	private String url = "http://roll.finance.sina.com.cn/finance/zq1/gsjsy/index.shtml";
	private int beginPage = 1;
	private int endPage = 300;
	private int crawl_page, lastPage = 0;
	private int Counturl = 0; // 计数器

	private void getListPage() {

		// 爬取页码，如果为0则爬取所有，如果不为0则抓取指定页数
		if (crawl_page > 0) {
			lastPage = crawl_page;
		} else {
			// 指定的列表的最后一页
			lastPage = endPage;
		}

		Pattern stockregex = Pattern.compile("http://finance.sina.com.cn/stock/jsy/[0-9a-zA-Z_-]+/[0-9a-zA-Z_-]+.shtml",
				Pattern.CASE_INSENSITIVE);

		try {

			for (int i = beginPage; i <= lastPage; i++) {

				String spliderUrl = url + "index_" + i + ".shtml";

				Document doc = HttpClientManage.GetInstance().GetDocument(spliderUrl, "GBK");

				Elements les = doc.getElementsByClass("list_009").select("li");
				for (int j = 0; j < les.size(); j++) {
					urlPath = les.select("a").get(j).attr("href");// 链接
					urlTitle = les.select("a").get(j).text();// 链接的内容

					// 只要财经大盘的url，其余的过滤掉
					Matcher stockm = stockregex.matcher(urlPath);
					if (stockm.find()) {
						if (getURLWoreds(urlPath, urlTitle) == false) {
							continue;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 
	 * <p>
	 * Title: getURLWoreds
	 * </p>
	 * <p>
	 * Description: 解析网页的内容
	 * </p>
	 * 
	 * @param urlPath
	 * @param urlName
	 */
	private boolean getURLWoreds(String urlPath, String urlTitle) {

		try {

			StringBuffer sbwords = new StringBuffer(); // 网页正文

			String str = " ";

			Document doc = HttpClientManage.GetInstance().GetDocument(urlPath, "GBK", 5000, 5000);

			// 页面不存在
			if (doc == null) {
				return false;
			}

			// 获取发布时间
			dateline = "";
			if (doc.select("div#wrapOuter>div.wrap-inner>div.page-info>span.time-source").text() != "") {
				str = doc.select("div#wrapOuter>div.wrap-inner>div.page-info>span.time-source").text();
				dateline = str.substring(0, 18).replaceAll("日", "").replaceAll("[年月]", "-");
			}

			// 获取发布来源第一规则
			if (doc.select("div#wrapOuter>div.wrap-inner>div.page-info>span.time-source").select("a")
					.toString() != "") {
				articlePath = doc.select("div#wrapOuter>div.wrap-inner>div.page-info>span.time-source").select("a")
						.text();

			}
			// 获取发布来源第二规则
			if (articlePath.equals("")) {
				if (doc.select("div#wrapOuter>div.wrap-inner>div.page-info>span.time-source").select("span")
						.size() > 1) {
					articlePath = doc.select("div#wrapOuter>div.wrap-inner>div.page-info>span.time-source")
							.select("span").get(1).text();
				}
			}

			// 网页正文获取
			int docSize = doc.select("div#artibody>p").size();
			for (int i = 1; i < docSize; i++) {
				sbwords.append(doc.select("div#artibody>p").get(i).text().trim());
			}

			if (sbwords.toString().equals("")) {
				return false;
			}

			// 文章内容html
			String sbwordshtml = doc.select("div#artibody").toString();

			if (articlePath.equals("") || dateline.equals("") || sbwords.toString().equals("")
					|| sbwordshtml.equals("")) {
				return false;
			}

			Message message = new Message();
			message.SiteName = "新浪财经";
			message.SpiderName = this.CommandName;
			message.InternalCategory = "网络媒体_综合门户_新浪_新浪财经_大盘";
			message.ExternalCategory = "新浪财经_大盘";
			message.Timestamp = System.currentTimeMillis();
			message.Url = urlPath;
			message.UrlHash = HelperUtil.ToMd5(message.Url);
			message.contentType = ContentType.Article;

			Article article = new Article();
			article.Title = urlTitle;
			article.Author = "";
			article.Referrer = articlePath;
			article.ContentText = sbwords.toString();
			article.ContentHtml = sbwordshtml;
			article.CommentCount = 0;
			article.ZhuanFaCount = 0;
			article.PraiseCount = 0;
			article.ReadCount = 0;
			article.PublishTime = DateUtil.toDateTime(dateline, "yyyy-MM-ss HH:mm");
			article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);

			message.content = article;

			Counturl++;
			dataProvider.Send(message);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public SinaFinanceDaPanCommandService() {

		this.CommandName = "com.smart.spider.sina.finance.dapan";
		this.Description = "新浪_财经_大盘";
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
