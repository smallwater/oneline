package com.smart.spider.jrj;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * 全景网新闻抓取
 * 
 * @author smart
 *
 */
public class BaoGaoJrjCommandService extends NewsSiteCommandService {

	private int maxPageCount = 100;
	private int inUrlCount = 0; // 抓取的条数
	private int outUrlCount = 0; // 未抓取的条数

	public BaoGaoJrjCommandService() {

		this.CommandName = "com.smart.spider.jrj.baogao";
		this.Description = "金融界_财经频道_报告";
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

			String returnJsonStr = getEveryPage("1");
			dataProvider.Open();

			// Matcher m
			// =Pattern.compile("(\\d{9,13}[a-z]\\w+).*?\\d+.\\d+.*?(\\d{5,6})").matcher(returnJsonStr);
			Matcher m = Pattern.compile("(?<=\\[')[a-zA-Z0-9]+(?=',)").matcher(returnJsonStr);

			while (m.find()) {

				// getURLWords("http://stock.jrj.com.cn/share,"+m.group(2)+",ggcontent.shtml?discId="+m.group(1));
				getURLWords("http://stock.jrj.com.cn/action/gggg/getTimeBystcode.jspa?vname=ggggList&discId="
						+ m.group() + "&_=" + new Date().getTime());

			}

			Matcher pageMatch = Pattern.compile("(?<=total:)[0-9]+(?=,)").matcher(returnJsonStr);

			while (pageMatch.find()) {

				pageTotal = (int) Math.ceil(Double.valueOf(pageMatch.group()) / 20);

			}

			if (maxPageCount > pageTotal || maxPageCount == 0) {

				maxPageCount = pageTotal;
			}
			for (int i = 2; i <= maxPageCount; i++) {

				String returnStr = getEveryPage(String.valueOf(i));

				Matcher urlMatch = Pattern.compile("(?<=\\[')[a-zA-Z0-9]+(?=',)").matcher(returnStr);

				while (urlMatch.find()) {

					getURLWords("http://stock.jrj.com.cn/action/gggg/getTimeBystcode.jspa?vname=ggggList&discId="
							+ urlMatch.group() + "&_=" + new Date().getTime());

				}
			}
			logger.info(this.CommandName + " 共抓取url数量:" + inUrlCount + "条,未抓取：" + outUrlCount + "条");
			dataProvider.Close();

		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * @Title: getEveryPage @Description: 获取每页的 url 返回json格式数据 @param
	 *         page @return String @throws
	 */

	public String getEveryPage(String page) {
		// 放参数 规则根据和讯网 滚动抓取 http://roll.hexun.com/
		Document doc = null;
		try {
			StringBuffer url = new StringBuffer(
					"http://stock.jrj.com.cn/action/getNoticeListByDiffCondition.jspa?vname=_notic_list&psize=20");
			url.append("&page=").append(page).append("&_dc=").append(new Date().getTime());
			doc = HttpClientManage.GetInstance().GetDocument(url.toString(), "gb2312", 5000, 10000);
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
		try {
			String doc = HttpClientManage.GetInstance().GetRequest(urlPath, 5000, 10000);
			if (doc == null) {
				outUrlCount = outUrlCount + 1;
				return;
			}
			Message message = new Message();
			message.SiteName = "金融界";
			message.SpiderName = this.CommandName;
			message.InternalCategory = "金融界_新闻 _网络媒体";
			message.Timestamp = System.currentTimeMillis();
			message.Url = urlPath;
			message.UrlHash = HelperUtil.ToMd5(message.Url);
			message.contentType = ContentType.Article;

			Article article = new Article();
			if (doc.indexOf("title\":") != -1) {// 页面未返回正确结果
				article.Title = doc.substring(doc.indexOf("title\":") + 8, doc.indexOf("txtContent\":") - 3);// 获取文章标题
			} else {
				outUrlCount = outUrlCount + 1;
				return;
			}
			article.PublishTime = doc.substring(doc.indexOf("declaredate\":") + 14, doc.indexOf("declaredate\":") + 33);// 获取发布时间
			article.ContentText = doc.substring(doc.indexOf("txtContent\":") + 13, doc.indexOf("},\"data\""))
					.replaceAll("\\\\r\\\\n", "");// 获取文章内容
			article.ContentHtml = doc.substring(doc.indexOf("txtContent\":") + 13, doc.indexOf("},\"data\""))
					.replaceAll("\\\\r\\\\n", "");

			message.ExternalCategory = "金融界首页_行情中心";
			article.CommentCount = 0;
			article.ZhuanFaCount = 0;
			article.PraiseCount = 0;
			article.ReadCount = 0;

			// 2015-11-26 18:41:50
			article.PublishTime = DateUtil.toDateTime(article.PublishTime, "yyyy-MM-dd HH:mm:ss");
			article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);

			message.content = article;
			// logger.info(message.ExternalCategory
			// +"@"+message.Url+"@"+article.Referrer+"@"+article.PublishTime+"@"+article.Title+"@"+article.ContentText);
			// 排除图片新闻，特征：内容短
			if (article.ContentText.length() < 10) {
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
