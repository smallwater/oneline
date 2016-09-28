package com.smart.spider.hexun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;

import com.smart.spider.data.meta.Article;
import com.smart.spider.data.meta.ContentType;
import com.smart.spider.data.meta.Message;
import com.smart.spider.http.HttpClientManage;
import com.smart.spider.util.DateUtil;
import com.smart.spider.util.HelperUtil;
import com.smart.spider.util.NewsSiteCommandService;

public class HexunXinGuCommandService extends NewsSiteCommandService {

	private String xinguregex = "http://stock.hexun.com/[0-9a-zA-Z_-]+/[0-9a-zA-Z_-]+.html";
	private List<String> urlList = new ArrayList<String>();
	// 链接地址， 链接标题， 发布时间， 标题导航， 文章来源 板块名称
	private String urlTitle, dateline, articlePath, author = "";
	private int Counturl = 0; // 计数器

	// 爬取页面
	private int crawl_page, lastPage, endPages = 0;

	/**
	 * 获取一级目录中的列表底部页码url，二级目录
	 * 
	 * @param url
	 * @throws IOException
	 */
	public void getListPage(String url) {

		try {

			endPages = hexuUtils.GetInstance().hexuQuanShangPageNums(url);

			// 爬取页码，如果为0则爬取所有，如果不为0则抓取指定页数
			if (crawl_page > 0) {
				lastPage = crawl_page;
			} else {
				// 默认设置100页
				if (endPages > 100) {
					endPages = 100;
				}
				// 指定的列表的最后一页
				lastPage = endPages;
			}

			// 指定的列表的最后一页
			String indexurl = "";

			int indexnum = url.indexOf("index");

			// 新股比较特别，第一页为http://stock.hexun.com/ipo/
			if (indexnum < 0) {
				indexnum = 0;
				indexurl = url;
			} else {
				indexurl = url.substring(0, indexnum);
			}

			int index = endPages;

			String listurl = " ";
			for (int i = endPages; i > 0; i--) {
				if (endPages == index) {
					endPages--;
					listurl = indexurl;
					getListUrl(listurl);
				} else {

					listurl = indexurl + "index-" + i + ".html";
					getListUrl(listurl);

				}
				lastPage--;
				if (lastPage == 0) {
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
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
	public void getListUrl(String articleUrl) {
		try {

			Document doc = HttpClientManage.GetInstance().GetDocument(articleUrl, "GBK", 5000, 5000);

			Elements hrefList = doc.select("div#content").select("div.conentbox").select("div#mainbox")
					.select("div.temp01").select("a");

			for (int i = 0; i < hrefList.size(); i++) {

				String xinguurl = hrefList.get(i).attr("href");

				if (regurl(xinguurl) == true) {
					if (htmlAnalysis(xinguurl) == false) {
						continue;
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
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

			StringBuffer sbwords = new StringBuffer(); // 网页正文

			Document doc = HttpClientManage.GetInstance().GetDocument(urlPath, "GBK", 5000, 5000);

			// 页面不存在
			if (doc == null) {
				return false;
			}
			if (doc.select("div.errorBack").size() > 0) {
				return false;
			}

			// 标题
			urlTitle = doc.select("div#artibodyTitle").text();

			// 获取发布时间第一规则
			dateline = "";
			if (!doc.select("span#pubtime_baidu").text().equals("")) {
				dateline = doc.select("span#pubtime_baidu").text();
			}

			// 获取发布时间第二规则
			if (dateline.equals("") && !doc.select("div#artInfo").text().equals("")) {
				dateline = doc.select("div#artInfo").text();
			}

			// 获取发布时间第三规则
			if (dateline.equals("") && doc.select("span#artibodyDesc").select("span.gray").size() > 0) {
				dateline = doc.select("span#artibodyDesc").select("span.gray").get(0).text();
			}

			// 获取发布时间第四规则
			if (dateline.equals("") && doc.select("div#artibodyTitle").select("span.gray").size() > 0) {
				dateline = doc.select("div#artibodyTitle").select("span.gray").get(0).text();
			}

			dateline = dateline.replaceAll("日", "").replaceAll("[年月]", "-");

			// 获取发布来源第一规则
			articlePath = "";
			if (!doc.select("span#source_baidu").text().equals("")) {
				articlePath = doc.select("span#source_baidu").text().split("：")[1].toString();
			}

			// 获取发布来源第二规则
			if (articlePath.equals("")) {
				articlePath = doc.select("span#artibodyDesc").select("a").text();
			}

			// 获取发布来源第三规则
			if (articlePath.equals("") && doc.select("span#artibodyDesc").text().indexOf("来源：") > 0) {
				articlePath = doc.select("span#artibodyDesc").text();
				articlePath = articlePath.substring(articlePath.indexOf("来源：") + 3, articlePath.length());
				if (articlePath.indexOf("作者：") > 0) {
					articlePath = articlePath.substring(0, articlePath.indexOf("作者："));
				}
			}

			if (articlePath.equals("")) {
				return false;
			}

			// 文章作者第一规则
			author = "";
			if (!doc.select("span#author_baidu").text().equals("")) {
				author = doc.select("span#author_baidu").text().split("：")[1].toString();
			}

			// 文章作者第二规则
			if (author.equals("")) {
				if (doc.select("span#artibodyDesc").select("span.gray").size() > 2
						&& !doc.select("span#artibodyDesc").select("span.gray").get(2).text().equals("")) {
					author = doc.select("span#artibodyDesc").select("span.gray").get(2).text().split("：")[1].toString();
				}
			}

			// 网页正文获取 第一规则
			int docSize = doc.select("div#artibody").select("p").size();

			for (int i = 0; i < docSize; i++) {
				sbwords.append(doc.select("div#artibody").select("p").get(i).text().trim());
			}

			// 网页正文获取第二规则
			if (sbwords.toString().equals("")) {
				sbwords = sbwords.append(doc.select("div#artibody").text().toString());
			}

			// 文章内容html
			String sbwordshtml = doc.select("div#contentar").select("div#mainleftcont").select("div#artibody")
					.select("p").toString();

			if (sbwordshtml.equals("")) {
				sbwordshtml = doc.select("div#mainer").select("div.main_L").select("div#artibody").toString();
			}

			if (articlePath.equals("") || dateline.equals("") || sbwords.toString().equals("")
					|| articlePath.equals("")) {
				return false;
			}

			Message message = new Message();
			message.SiteName = "和讯";
			message.SpiderName = this.CommandName;

			message.InternalCategory = "网络媒体_综合门户_和讯_新股_劵商";
			message.ExternalCategory = "和讯_新股_劵商";
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
			article.ReadCount = 0; // 2015-12-02 12:21:59
			dateline = regurlTime(dateline);
			article.PublishTime = DateUtil.toDateTime(dateline, "yyyy-MM-dd HH:mm:ss");
			article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);

			message.content = article;

			Counturl++;
			dataProvider.Send(message);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public HexunXinGuCommandService() {

		this.CommandName = "com.smart.spider.hexun.xingu";

		this.Description = "和讯_新股_劵商";

		this.Author = "smart";

	}

	/***
	 * 
	 * <p>
	 * Title: regurl
	 * </p>
	 * <p>
	 * Description: 网站正则顾虑器
	 * </p>
	 * 
	 * @param url
	 * @return
	 */
	public boolean regurl(String url) {
		// 研报标准页面过滤
		Pattern regex = Pattern.compile(xinguregex, Pattern.CASE_INSENSITIVE);

		Matcher matcher = regex.matcher(url);

		if (matcher.find()) {
			return true;
		} else {
			return false;
		}
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

		urlList.add("http://stock.hexun.com/ipo/");
		urlList.add("http://stock.hexun.com/shengou/");
		urlList.add("http://stock.hexun.com/nss/index.html");
		urlList.add("http://stock.hexun.com/qsdx/index.html");
		urlList.add("http://stock.hexun.com/jgdt/index.html");
		urlList.add("http://stock.hexun.com/qsrw/index.html");
		urlList.add("http://stock.hexun.com/scdt/index.html");
		urlList.add("http://stock.hexun.com/jggg/index.html");

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