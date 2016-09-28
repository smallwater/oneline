package com.smart.spider.ifeng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.smart.spider.data.meta.Article;
import com.smart.spider.data.meta.ContentType;
import com.smart.spider.data.meta.Message;
import com.smart.spider.util.DateUtil;
import com.smart.spider.util.HelperUtil;
import com.smart.spider.util.NewsSiteCommandService;

/**
 * 凤凰网财经宏观频道
 * 
 * @author smart
 * 
 */
public class IfengMacroscopicService extends NewsSiteCommandService {
	private String externalCategory = "凤凰网财经_宏观_政策";
	private String url = "";
	private int maxPageCount = 172;

	public IfengMacroscopicService() {
		CommandName = "com.smart.spider.ifeng.macroscopic";

		Description = "凤凰网财经_宏观频道";

		Author = "smart";
	}

	@Override
	public void Init(String[] args) {

		url = "http://finance.ifeng.com/cmppdyn/752/666/";
	}

	/**
	 * 
	 * *.jar com.mfniu.spider.ifeng.macroscopic 1
	 * 
	 */
	@Override
	public void Exec(String[] args) {

		int pageNm = 0;

		if (null != args && args.length >= 2) {
			try {
				pageNm = Integer.parseInt(args[1]);
				if (pageNm > 0) {
					maxPageCount = pageNm;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {

			dataProvider.Open();

			// 设置数据
			ExtractHtmlContent();

			dataProvider.Close();

		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void ExtractHtmlContent() {
		String spliderUrl = "";
		for (int i = 1; i <= maxPageCount; i++) {
			spliderUrl = url + i + "/" + "dynlist.html";
			getUrlPathNameDate(spliderUrl);
		}
	}

	/**
	 * 筛选列表内容
	 * 
	 * @param spliderUrl
	 */
	private void getUrlPathNameDate(String spliderUrl) {
		try {
			// url筛选规则
			Pattern stockregex = Pattern.compile("http://finance.ifeng.com/a/[0-9a-zA-Z_-]+/[0-9a-zA-Z_-]+.shtml",
					Pattern.CASE_INSENSITIVE);
			boolean findFlag = false;
			boolean isFlag = false;
			Document tempdoc = null;
			Document doc = null;
			String urlPath = "";

			if (IfengSpliderUtil.spliderIfengHongguanUrlisExist(spliderUrl)) {

				doc = IfengSpliderUtil.getDocument(spliderUrl);

				Elements les = doc.getElementById("list01").select("li");
				for (int j = 0; j < les.size(); j++) {
					try {
						urlPath = les.get(j).select("h3>a").attr("href");
						Matcher stockm = stockregex.matcher(urlPath);
						findFlag = stockm.find();
						if (findFlag) {
							tempdoc = IfengSpliderUtil.getDocument(urlPath);
							isFlag = getURLWoreds_1(urlPath, tempdoc);
							if (!isFlag) {
								getURLWoreds_2(urlPath, tempdoc);
							}
						}
					} catch (Exception e) {
						logger.error(e);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * 消息获取规则1：main_content
	 * 
	 * @param urlPath
	 * @param doc
	 * @return
	 */
	private boolean getURLWoreds_1(String urlPath, Document doc) {
		StringBuffer sbwords = new StringBuffer();
		StringBuffer sbwords1 = new StringBuffer();
		String timeData = "";
		String mediaName = "";
		String authorSource = "";
		String urlName = "";
		Elements author = null;
		try {
			Message message = new Message();
			message.InternalCategory = "网络媒体_综合门户_凤凰网";
			message.Timestamp = System.currentTimeMillis();
			message.contentType = ContentType.Article;
			message.ExternalCategory = externalCategory;
			message.Url = urlPath;
			message.UrlHash = HelperUtil.ToMd5(message.Url);
			message.SiteName = "凤凰网财经";
			message.SpiderName = this.CommandName;

			Article article = new Article();
			article.CommentCount = 0;
			article.ZhuanFaCount = 0;
			article.PraiseCount = 0;
			article.ReadCount = 0;

			// 标题
			urlName = doc.getElementById("artical_topic").text();
			// 时间
			timeData = doc.select("div#artical_sth>p.p_time>span.ss01").text();
			if (timeData.isEmpty()) {
				System.out.println("时间为空：" + urlPath);
				return false;
			}

			// 获取发布来源
			mediaName = doc.select("div#artical_sth>p.p_time>span>span.ss03").text();
			// 作者
			author = doc.select("div#artical_sth>p.p_time>span.ss04>span>span");
			if (null != author && author.size() > 0 && null != author.get(0)) {
				authorSource = author.get(0).text();
			} else {
				authorSource = "";
			}

			// 正文
			int docSize = doc.select("div#main_content>p").size();

			for (int i = 0; i < docSize; i++) {
				sbwords.append(doc.select("div#main_content>p").get(i).text().trim());
				sbwords1.append(doc.select("div#main_content>p").get(i).html());
			}
			// 2015-12-02正文无法取到的场合
			if (docSize == 0) {
				sbwords.append(doc.select("div#main_content").text().trim());
				sbwords1.append(doc.select("div#main_content").html());
			}
			// 标题
			article.Title = urlName;

			// 设置时间2015-11-26 14:19
			{
				String publishTime = timeData.replaceAll("日", "").replaceAll("[年月]", "-");
				article.PublishTime = DateUtil.toDateTime(publishTime, "yyyy-MM-dd HH:mm");
				article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);
			}
			// 设置发布来源
			article.Referrer = mediaName;
			// 作者
			article.Author = authorSource;
			// 正文-文章内容
			article.ContentText = sbwords.toString();
			// 正文-文章html
			article.ContentHtml = sbwords1.toString();
			// 消息内容
			message.content = article;
			// 发送消息
			dataProvider.Send(message);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 抓取规则2：正文用规则：artical_real
	 * 
	 * @param urlPath
	 * @param doc
	 * @return
	 */
	private boolean getURLWoreds_2(String urlPath, Document doc) {
		String timeData = "";
		String mediaName = "";
		String authorSource = "";
		String urlName = "";
		Elements author = null;
		Element media = null;
		StringBuffer sbwords = new StringBuffer();
		StringBuffer sbwords1 = new StringBuffer();

		try {
			Message message = new Message();
			message.InternalCategory = "网络媒体_综合门户_凤凰网";
			message.Timestamp = System.currentTimeMillis();
			message.contentType = ContentType.Article;
			message.ExternalCategory = externalCategory;
			message.Url = urlPath;
			message.UrlHash = HelperUtil.ToMd5(message.Url);
			message.SiteName = "凤凰网财经";
			message.SpiderName = this.CommandName;

			Article article = new Article();
			article.CommentCount = 0;
			article.ZhuanFaCount = 0;
			article.PraiseCount = 0;
			article.ReadCount = 0;

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

			// 标题
			urlName = doc.getElementById("artical_topic").text();
			// 时间&作者&来源
			author = doc.select("div#artical_sth>p>span");
			if (null != author && author.size() > 1) {
				// 时间
				timeData = author.get(0).text();
				// 来源
				media = author.get(1);
				if (media.select("a").isEmpty()) {
					mediaName = author.get(1).text();
				} else {
					mediaName = author.get(1).select("a").text();
				}
				authorSource = "";
			} else {
				timeData = sdf.format(new Date());
				mediaName = "";
				authorSource = "";
			}

			if (null == timeData) {
				return false;
			}
			// 正文
			int docSize = doc.select("div#artical_real>p").size();

			for (int i = 0; i < docSize; i++) {
				sbwords.append(doc.select("div#artical_real>p").get(i).text().trim());
				sbwords1.append(doc.select("div#artical_real>p").get(i).html());
			}
			// 2015-12-02正文无法取到的场合
			if (docSize == 0) {
				sbwords.append(doc.select("div#artical_real").text().trim());
				sbwords1.append(doc.select("div#artical_real").html());
			}
			// 标题
			article.Title = urlName;
			// 设置时间
			String publishTime = timeData.replaceAll("日", "").replaceAll("[年月]", "-");
			article.PublishTime = DateUtil.toDateTime(publishTime, "yyyy-MM-dd HH:mm");
			article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);
			// 设置发布来源
			article.Referrer = mediaName;
			// 作者
			article.Author = authorSource;
			// 正文-文章内容
			article.ContentText = sbwords.toString();
			// 正文-文章html
			article.ContentHtml = sbwords1.toString();
			// 消息内容
			message.content = article;
			// 发送消息
			dataProvider.Send(message);

		} catch (Exception e) {

			logger.error(e);

			return false;
		}
		return true;
	}

}
