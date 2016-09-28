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
 * 其他市场
 * 
 * @author smart
 * 
 */
public class IfengStockQtscService extends NewsSiteCommandService {

	private String url = "";
	private String splidFlag = "_";
	private int maxPageCount = 173;
	private boolean isTempFlag = false;
	private String externalCategory = "";

	public IfengStockQtscService() {
		CommandName = "com.smart.spider.ifeng.qtsc";

		Description = "凤凰网财经_证券_其他市场";

		Author = "smart";
	}

	@Override
	public void Init(String[] args) {

		url = "http://finance.ifeng.com/cmppdyn/422/470/";
	}

	@Override
	public void Exec(String[] args) {

		if (null != args && args.length >= 2) {
			try {
				maxPageCount = Integer.parseInt(args[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			maxPageCount = 0;
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

	/**
	 * 解析网页内容
	 */
	private void ExtractHtmlContent() {

		try {
			int i = 1;
			String url = "";
			url = getUrlPathNameDate(i);

			while (!url.isEmpty()) {
				i = i + 1;
				if (maxPageCount != 0 && i > maxPageCount) {
					break;
				}

				url = getUrlPathNameDate(url);
			}

		} catch (Exception e) {
			logger.error(e);
		}
	}

	private String getUrlPathNameDate(int pageNo) {

		String pageUrl = "";
		String spliderUrl = "";
		// http://finance.ifeng.com/cmppdyn/419/467/2/dynlist.html
		spliderUrl = url + pageNo + "/" + "dynlist.html";
		pageUrl = getUrlPathNameDate(spliderUrl);

		return pageUrl;
	}

	private String getUrlPathNameDate(String pageurl) {

		String tempUrl = "";
		String urlPath = "";
		String spliderUrl = "";
		boolean findFlag = false;
		boolean isFlag = false;
		Document tempdoc = null;
		Elements templis = null;
		// url过滤规则
		// http://finance.ifeng.com/a/20151109/14060919_0.shtml
		Pattern stockregex = Pattern.compile("http://finance.ifeng.com/a/[0-9a-zA-Z_-]+/[0-9a-zA-Z_-]+.shtml",
				Pattern.CASE_INSENSITIVE);
		spliderUrl = pageurl;

		if (IfengSpliderUtil.spliderIfengJshqUrlisExist(spliderUrl)) {
			Document doc = null;
			doc = IfengSpliderUtil.getDocument(spliderUrl);
			Elements tempLes = doc.select("div.searchDiv02>div.theLogo>div.theCurrentnav.cDGray");
			if (!isTempFlag) {
				for (int i = 0; i < tempLes.size(); i++) {
					externalCategory = tempLes.get(i).select("a").text().replace(" ", splidFlag);
					isTempFlag = true;
				}
			}

			Elements les = doc.select("div.list03>ul");
			for (int j = 0; j < les.size(); j++) {
				templis = les.get(j).select("li");
				for (int templi = 0; templi < templis.size(); templi++) {
					try {
						urlPath = templis.get(templi).select("span>a").attr("href");
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

			tempUrl = doc.select("div.next>a").get(1).attr("href");

			if (tempUrl.equals(spliderUrl)) {
				tempUrl = "";
			}
		}
		return tempUrl;
	}

	/**
	 * 筛选规则1
	 * 
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
		Elements media = null;
		Elements media_a = null;
		Elements nextPages = null;
		Elements nextPageNo = null;
		String nextPageUrl = "";
		int nextPage = -1;

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
				return false;
			}

			// 媒体来源
			media = doc.select("div#artical_sth>p.p_time>span>span.ss03");
			if (null != media && media.size() > 0 && null != media.get(0)) {
				media_a = media.get(0).select("a");
				if (null != media_a && !media_a.isEmpty()) {
					mediaName = media_a.text();
				} else {
					mediaName = media.get(0).text();
				}
			} else {
				mediaName = "";
			}

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
			//2015-12-02 正文没有取到的场合
			if(docSize ==0 ){
				sbwords.append(doc.select("div#main_content").text().trim());
				sbwords1.append(doc.select("div#main_content").html());
			}
			// 是否有下一页
			nextPages = doc.select("div.an>div.next>table>tbody>tr>td>div.pageNum");
			if (null != nextPages && nextPages.size() > 0) {
				try {
					nextPageNo = nextPages.select("a");
					for (int npn = 0; npn < nextPageNo.size(); npn++) {
						nextPageUrl = nextPageNo.get(npn).attr("href");
						nextPage = Integer.parseInt(nextPageNo.get(npn).text());
						sbwords.append(GetNextPageInfo(nextPageUrl, nextPage));
						sbwords1.append(GetNextPageHtml(nextPageUrl, nextPage));
					}

				} catch (NumberFormatException e) {
					nextPage = -1;
					e.printStackTrace();
				}

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

	/**
	 * 筛选规则2
	 * 
	 * @param doc
	 * @return
	 */
	private boolean getURLWoreds_2(String urlPath, Document doc) {
		StringBuffer sbwords = new StringBuffer();
		StringBuffer sbwords1 = new StringBuffer();
		String timeData = "";
		String mediaName = "";
		String authorSource = "";
		String urlName = "";
		Elements author = null;
		Element media = null;
		Elements nextPages = null;
		Elements nextPageNo = null;
		String nextPageUrl = "";
		int nextPage = -1;

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
			// 作者
			author = doc.select("div#artical_sth>p>span");
			if (null != author && author.size() > 1) {
				// 时间
				timeData = author.get(0).text();
				// 媒体来源
				media = author.get(1);
				if (media.select("a").isEmpty()) {
					mediaName = author.get(1).text();
				} else {
					mediaName = author.get(1).select("a").text();
				}
				if (mediaName.isEmpty()) {
					mediaName = "";
				}
				if (author.size() > 2 && null != author.get(2)) {
					authorSource = author.get(2).text();
				} else {
					authorSource = "";
				}

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
			//2015-12-02 正文没有取到的场合
			if(docSize ==0 ){
				sbwords.append(doc.select("div#artical_real").text().trim());
				sbwords1.append(doc.select("div#artical_real").html());
			}
			// 是否有下一页
			nextPages = doc.select("div.an>div.next>table>tbody>tr>td>div.pageNum");
			if (null != nextPages && nextPages.size() > 0) {
				try {
					nextPageNo = nextPages.select("a");
					for (int npn = 0; npn < nextPageNo.size(); npn++) {
						nextPageUrl = nextPageNo.get(npn).attr("href");
						nextPage = Integer.parseInt(nextPageNo.get(npn).text());
						sbwords.append(GetNextPageInfo(nextPageUrl, nextPage));
						sbwords1.append(GetNextPageHtml(nextPageUrl, nextPage));
					}

				} catch (NumberFormatException e) {
					nextPage = -1;
					e.printStackTrace();
				}

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

	/**
	 * 获取下一页内容
	 * 
	 * @param nextPageUrl
	 *            下一页URL
	 * @param nextPageNo
	 *            页码
	 * @return
	 */
	private String GetNextPageInfo(String nextPageUrl, int nextPageNo) {

		StringBuffer sbwords = new StringBuffer();

		try {
			Document doc = IfengSpliderUtil.getDocument(nextPageUrl);
			// 正文
			int docSize = doc.select("div#main_content>p").size();
			for (int i = 0; i < docSize; i++) {
				sbwords.append(doc.select("div#main_content>p").get(i).text().trim());
			}
			int docSize_1 = doc.select("div#artical_real>p").size();

			for (int i = 0; i < docSize_1; i++) {
				sbwords.append(doc.select("div#artical_real>p").get(i).text().trim());
			}
		} catch (Exception e) {
			logger.error(e);
		}

		return sbwords.toString();
	}

	/**
	 * 获取下一页HTML
	 * 
	 * @param nextPageUrl
	 *            下一页URL
	 * @param nextPageNo
	 *            页码
	 * @return
	 */
	private String GetNextPageHtml(String nextPageUrl, int nextPageNo) {

		StringBuffer sbwords = new StringBuffer();

		try {

			Document doc = IfengSpliderUtil.getDocument(nextPageUrl);
			// 正文
			int docSize = doc.select("div#main_content>p").size();
			for (int i = 0; i < docSize; i++) {
				sbwords.append(doc.select("div#main_content>p").get(i).html());
			}
			int docSize_1 = doc.select("div#artical_real>p").size();

			for (int i = 0; i < docSize_1; i++) {
				sbwords.append(doc.select("div#artical_real>p").get(i).html());
			}

		} catch (Exception e) {
			logger.error(e);
		}

		return sbwords.toString();
	}

}
