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

public class SinaFinanceGeGuCommandService extends NewsSiteCommandService {

	private String url = "http://finance.sina.com.cn/column/ggdp.shtml";

	// 链接地址，链接标题，发布时间，文件内容 获取发布来源
	private String urlPath, urlTitle, dateline, articlePath = "";
	private int Counturl = 0; // 计数器

	private void getListPage() {

		Pattern bolgregex = Pattern.compile("http://blog.sina.com.cn/s/[0-9a-zA-Z_-]", Pattern.CASE_INSENSITIVE);

		try {

			// 爬取页码，如果为0则爬取所有，如果不为0则抓取指定页数
			Document doc = HttpClientManage.GetInstance().GetDocument(url, "GBK");

			Elements les = doc.getElementsByClass("list_009").select("li");
			for (int j = 0; j < les.size(); j++) {
				urlPath = les.select("a").get(j).attr("href");// 链接
				urlTitle = les.select("a").get(j).text();// 链接的内容
				Matcher bolgm = bolgregex.matcher(urlPath);

				// 只要blog的url，其余的过滤掉
				if (bolgm.find()) {
					if (getURLWoreds(urlPath, urlTitle) == false) {
						continue;
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

			String sbwordshtml = " ";

			Document doc = HttpClientManage.GetInstance().GetDocument(urlPath, "utf-8", 5000, 5000);

			// 页面不存在
			if (doc == null) {
				return false;
			}

			// 发布时间第一规则
			if (!doc.select("div#articlebody").select("span.time.SG_txtc").text().equals("")) {
				dateline = doc.select("div#articlebody").select("span.time.SG_txtc").text().replace("(", "")
						.replace(")", "");
			}

			// 发布时间第二规则
			if (dateline == null || dateline.equals("")) {
				dateline = doc.select("div.artinfo").select("span.time").get(0).text();
			}

			if (dateline == null || dateline.equals("")) {
				dateline = doc.select("div.page-info>span.time-source").text();
				dateline = dateline.split(" ")[0];
			}
			dateline = dateline.replaceAll("日", "").replaceAll("[年月]", "-");

			// 获取发布来源
			articlePath = doc.select("div.artInfo>span#media_name").text();
			if (articlePath == null || articlePath.equals("")) {
				String[] temp1 = doc.select("div.page-info>span.time-source").text().split(" ");
				if (temp1.length >= 2) {
					articlePath = doc.select("div.page-info>span.time-source").text().split(" ")[1];// 获取发布来源
				}
			}

			// 网页正文获取,获取div标签
			int docSize = doc.select("div#sina_keyword_ad_area2>div").size();

			// 正文获取第一规则
			for (int i = 0; i < docSize; i++) {
				sbwords.append(doc.select("div#sina_keyword_ad_area2>div").get(i).text().trim());
				// 文章内容htmlsbwordshtml
				sbwordshtml = doc.select("div#sina_keyword_ad_area2").toString();
			}

			// 正文获取第二规则
			if (sbwords.toString().equals("")) {
				int docSize1 = doc.select("div#sina_keyword_ad_area2>font").size();

				for (int i = 0; i < docSize1; i++) {
					sbwords.append(doc.select("div#sina_keyword_ad_area2>font").get(i).text());
				}
			}

			// 正文获取第三规则
			if (sbwords.toString().equals("")) {
				int docSize1 = doc.select("div#sina_keyword_ad_area2>p").size();

				for (int i = 0; i < docSize1; i++) {
					sbwords.append(doc.select("div#sina_keyword_ad_area2>p").get(i).text());
				}
			}

			if (dateline.equals("") || sbwords.toString().equals("") || sbwordshtml.equals("")) {
				return false;
			}

			Message message = new Message();
			message.SiteName = "新浪财经";
			message.SpiderName = this.CommandName;
			message.InternalCategory = "网络媒体_综合门户_新浪_新浪财经_个股";
			message.ExternalCategory = "新浪财经_个股";
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

	public SinaFinanceGeGuCommandService() {

		this.CommandName = "com.smart.spider.sina.finance.gegu";
		this.Description = "新浪_财经_个股";
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
