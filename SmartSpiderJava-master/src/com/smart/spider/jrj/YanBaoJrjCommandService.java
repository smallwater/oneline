package com.smart.spider.jrj;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.smart.spider.data.meta.Article;
import com.smart.spider.data.meta.ContentType;
import com.smart.spider.data.meta.Message;
import com.smart.spider.http.HttpClientManage;
import com.smart.spider.util.DateUtil;
import com.smart.spider.util.HelperUtil;
import com.smart.spider.util.NewsSiteCommandService;

/**
 * 
 * 金融界
 * 
 * @author smart
 *
 */
public class YanBaoJrjCommandService extends NewsSiteCommandService {

	private int maxPageCount = 100;
	private int inUrlCount = 0; // 抓取的条数
	private int outUrlCount = 0; // 未抓取的条数

	public YanBaoJrjCommandService() {

		this.CommandName = "com.smart.spider.jrj.yanbao";
		this.Description = "金融界_财经频道_研报";
		this.Author = "smart";

	}

	@Override
	public void Init(String[] args) {
	}

	@Override
	public void Exec(String[] args) {

		if (args != null && args.length > 1) {

			try {

				maxPageCount = Integer.parseInt(args[1]);

			} catch (Exception e) {

				e.printStackTrace();

			}
		}

		try {

			int pageTotal = 0;

			String returnJsonStr = getEveryPage(String.valueOf(1));

			dataProvider.Open();

			Matcher m = Pattern.compile("\\[(?:(?:'[^']*',){5})(?:'([^']*)')")
					.matcher(returnJsonStr.replace("\"", "'"));

			while (m.find()) {

				getURLWords("http://istock.jrj.com.cn/article,yanbao," + m.group(1) + ".html");

			}

			Matcher pageMatch = Pattern.compile("(?<=total':)[0-9]+(?=\\})").matcher(returnJsonStr.replace("\"", "'"));

			while (pageMatch.find()) {

				pageTotal = (int) Math.ceil(Double.valueOf(pageMatch.group()) / 20);

			}

			if (maxPageCount > pageTotal || maxPageCount == 0) {

				maxPageCount = pageTotal;
			}
			for (int i = 2; i <= maxPageCount; i++) {

				returnJsonStr = getEveryPage(String.valueOf(i));

				Matcher urlMatch = Pattern.compile("\\[(?:(?:'[^']*',){5})(?:'([^']*)')")
						.matcher(returnJsonStr.replace("\"", "'"));

				while (urlMatch.find()) {

					getURLWords("http://istock.jrj.com.cn/article,yanbao," + urlMatch.group(1) + ".html");

				}
			}
			logger.info("com.mfniu.spider.jrj.yanbao 共抓取url数量:" + inUrlCount + "条,未抓取：" + outUrlCount + "条");
			dataProvider.Close();

		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * @Title: getEveryPage @Description: 获取每页的 url 返回json格式数据 @param
	 * page @return String @throws
	 */

	public String getEveryPage(String page) {
		// 放参数 规则根据和讯网 滚动抓取 http://roll.hexun.com/
		Document doc = null;
		try {
			StringBuffer url = new StringBuffer(
					"http://stock.jrj.com.cn/action/yanbao/getAllYanBaoList.jspa?vname=yanbaolist&ps=20&orgCode=-1&dateInterval=365");
			url.append("&page=").append(page).append("&_dc=").append(new Date().getTime());
			String htmlContent = HttpClientManage.GetInstance().GetRequest(url.toString(), "gb2312");
			doc = Jsoup.parse(htmlContent);
		} catch (Exception e) {
			logger.error(e);
		}
		return doc.text();
	}

	/**
	 * @Title: getURLWords @Description: 根据每页的url 获取相应 格式 (网络媒体 综合门户 和讯 滚动
	 *         和讯网_新闻 采集时间 发布时间 作者 当前网址 文章标题 文章来源 文章内容)的数据获取文章内容 @param
	 *         urlPath @return void @throws
	 */

	public void getURLWords(String urlPath) {
		String locationPath = "";
		// urlPath="http://istock.jrj.com.cn/article,yanbao,29515660.html";
		try {
			Document doc = HttpClientManage.GetInstance().GetDocument(urlPath.toString(), "gb2312", 5000, 5000);

			if (doc == null) {
				outUrlCount = outUrlCount + 1;
				return;
			}
			Message message = new Message();
			message.SiteName = "和讯网";
			message.SpiderName = this.CommandName;
			message.InternalCategory = "和讯网_新闻 _网络媒体";
			message.Timestamp = System.currentTimeMillis();
			message.Url = urlPath;
			message.UrlHash = HelperUtil.ToMd5(message.Url);
			message.contentType = ContentType.Article;

			Article article = new Article();

			// article.Author = doc.select("span#author_baidu").text();// 获取作者
			article.PublishTime = doc.select("p.title").text().split("发表于")[1].trim();// 获取发布时间
			article.Title = doc.select("h1.fl").text();// 获取文章标题
			article.ContentText = doc.select("p.article-p").text();// 获取文章内容
			article.ContentHtml = doc.select("p.article-p").html();
			message.ExternalCategory = doc.select("p.fl").text().replace(">", "_");
			article.CommentCount = 0;
			article.ZhuanFaCount = 0;
			article.PraiseCount = 0;
			article.ReadCount = 0;

			// 2015-11-26 18:41:50
			article.PublishTime = DateUtil.toDateTime(article.PublishTime);
			article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);
			message.content = article;
			// 排除图片新闻，特征：内容短
			if (article.ContentText.length() < 15) {
				outUrlCount = outUrlCount + 1;
				return;
			}
			inUrlCount = inUrlCount + 1;
			dataProvider.Send(message);
		} catch (Exception e) {
			logger.error(e + urlPath);
		}
	}
}
