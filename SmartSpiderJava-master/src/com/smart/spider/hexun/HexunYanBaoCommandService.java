package com.smart.spider.hexun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

public class HexunYanBaoCommandService extends NewsSiteCommandService {

	private List<String> urlList = new ArrayList<String>();

	// 爬取页面 爬取尾页
	private int crawl_page, lastPage, endPage = 0;
	private int beginPage = 1;
	// 链接地址， 链接标题， 发布时间， 作者， 文章来源 板块名称
	private String urlTitle, dateline, author, articlePath = "";
	private String domain = "http://yanbao.stock.hexun.com/";
	private String yanbaoregex = "http://yanbao.stock.hexun.com/[0-9a-zA-Z_-]+.shtml";
	private int Counturl = 0; // 计数器

	/**
	 * 获取一级目录中的列表底部页码url，二级目录
	 * 
	 * @param url
	 * @throws IOException
	 */
	public void getListPage(String url) {

		try {
			endPage = hexuUtils.GetInstance().hexuPageNums(url);

			// 爬取页码，如果为0则爬取所有，如果不为0则抓取指定页数
			if (crawl_page > 0) {
				lastPage = crawl_page;
			} else {
				// 默认设置100页
				if (endPage > 200) {
					endPage = 200;
				}
				// 指定的列表的最后一页
				lastPage = endPage;
			}

			for (int i = beginPage; i <= lastPage; i++) {

				String listPages = url.split("_")[0] + "_" + i + ".shtml";

				getListUrl(listPages);

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

			Elements hrefList = doc.select("div.sub_cont").select("div.fxx_table").select("a.fxx_wb").select("a");

			for (int i = 0; i < hrefList.size(); i++) {

				String yanbaourl = domain + hrefList.get(i).attr("href");

				if (regurl(yanbaourl) == true) {

					if (htmlAnalysis(yanbaourl) == false) {
						continue;
					}
				}
			}
		} catch (Exception e) {

		}

	}

	/***
	 * 
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

			// 标题
			urlTitle = doc.select("div.content").select("div.yj_bglc").select("h3.tit_04").text();

			// 获取发布时间
			dateline = doc.select("div.content").select("div.yj_bglc").select("span").get(0).text();
			dateline = dateline.replaceAll("日", "").replaceAll("[年月]", "-");

			// 获取发布来源
			articlePath = doc.select("div.content").select("div.yj_bglc").select("a").get(1).text();

			// 网页正文获取第一规则
			int docSize = doc.select("div.content").select("div.yj_bglc").select("p").size();

			// 作者
			author = doc.select("div.content").select("div.yj_bglc").select("p").select("a").get(2).text();

			// 文章正文
			for (int i = 1; i < docSize - 4; i++) {
				sbwords.append(doc.select("div.content").select("div.yj_bglc").select("p").get(i).text().trim());
			}

			// 文章内容htmlsbwordshtml
			String sbwordshtml = doc.select("div.content").select("div.yj_bglc").toString();

			if (articlePath.equals("") || dateline.equals("") || sbwords.toString().equals("") || author.equals("")) {
				return false;
			}

			Message message = new Message();

			message.SiteName = "和讯";
			message.SpiderName = this.CommandName;
			message.InternalCategory = "网络媒体_综合门户_和讯_研报";
			message.ExternalCategory = "和讯_研报";
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
			article.PublishTime = DateUtil.toDateTime(dateline, "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss");
			article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);

			message.content = article;

			Counturl++;
			dataProvider.Send(message);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public HexunYanBaoCommandService() {

		this.CommandName = "com.smart.spider.hexun.yanbao";

		this.Description = "和讯研报";

		this.Author = "smart";

	}

	/***
	 * 
	 * Description: 网站正则顾虑器
	 * </p>
	 * 
	 * @param url
	 * @return
	 */
	public boolean regurl(String url) {
		// 研报标准页面过滤
		Pattern regex = Pattern.compile(yanbaoregex, Pattern.CASE_INSENSITIVE);

		Matcher matcher = regex.matcher(url);

		if (matcher.find()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void Init(String[] args) {
		// TODO Auto-generated method stub
		urlList.add("http://yanbao.stock.hexun.com/listnews1_1.shtml");
		urlList.add("http://yanbao.stock.hexun.com/listnews2_1.shtml");
		urlList.add("http://yanbao.stock.hexun.com/listnews3_1.shtml");
		urlList.add("http://yanbao.stock.hexun.com/listnews4_1.shtml");
		urlList.add("http://yanbao.stock.hexun.com/listnews5_1.shtml");
		urlList.add("http://yanbao.stock.hexun.com/listnews6_1.shtml");
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
