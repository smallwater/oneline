package com.smart.spider.ifeng;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.smart.spider.data.meta.Article;
import com.smart.spider.data.meta.ContentType;
import com.smart.spider.data.meta.Message;
import com.smart.spider.util.DateUtil;
import com.smart.spider.util.HelperUtil;
import com.smart.spider.util.NewsSiteCommandService;

/**
 * 股吧
 * 
 * @author Administrator
 * 
 */
public class IfengStockReportService extends NewsSiteCommandService {

	private String url = "";
	private int maxPageCount = 361;
	private String externalCategory = "";
	private String npText = "下一页";

	public IfengStockReportService() {
		CommandName = "com.smart.spider.ifeng.report";

		Description = "凤凰网财经_证券_股吧";

		Author = "smart";
	}

	@Override
	public void Init(String[] args) {

		url = "http://finance.ifeng.com/report/";
	}

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

	/**
	 * 解析网页内容
	 */
	private void ExtractHtmlContent() {
		try {
			List<String> pageUrl = new ArrayList<String>();
			pageUrl = getReportUrl();
			for (String tempUrl : pageUrl) {
				// 取得所有的URL
				getUrlPathNameDate(maxPageCount, tempUrl);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * url列表 http://app.finance.ifeng.com/report/type.php?t=8
	 * 
	 * @return
	 */
	private List<String> getReportUrl() {

		List<String> pageUrl = new ArrayList<String>();
		String reportUrl = url;
		String subReportUrl = "";
		if (IfengSpliderUtil.spliderIfengReportUrlisExist(reportUrl)) {
			Document doc = null;
			doc = IfengSpliderUtil.getDocument(reportUrl);
			Elements tempLes = doc.select("div.title");
			for (int i = 0; i < tempLes.size(); i++) {
				subReportUrl = tempLes.get(i).select("a").attr("href");
				pageUrl.add(subReportUrl);
			}
		}

		return pageUrl;

	}

	private void getUrlPathNameDate(int pageNo, String subUrl) {

		// url筛选规则
		String spliderUrl = "";
		int i = 0;
		// http://app.finance.ifeng.com/report/type.php?t=8
		spliderUrl = subUrl;
		spliderUrl = getUrlPathNameDate(spliderUrl);

		while (!spliderUrl.isEmpty()) {
			i = i + 1;
			if (pageNo != 0 && i > pageNo) {
				break;
			}
			spliderUrl = getUrlPathNameDate(spliderUrl);

		}

	}

	private String getUrlPathNameDate(String pageurl) {

		String spliderUrl = "";
		boolean findFlag = false;
		boolean isFlag = false;
		Document tempdoc = null;
		Elements nextPageEle = null;
		String nextPageText = "";
		String nextPageUrl = "";
		String urlPath = "";
		spliderUrl = pageurl;

		// url筛选规则
		// http://guba.finance.ifeng.com/viewthread.php?tid=9513164
		Pattern stockregex = Pattern.compile("http://guba.finance.ifeng.com/viewthread.php\\?tid=[0-9]+",
				Pattern.CASE_INSENSITIVE);

		if (IfengSpliderUtil.spliderIfengReportSubUrlisExist(spliderUrl)) {
			Document doc = null;
			doc = IfengSpliderUtil.getDocument(spliderUrl);

			Elements les = doc.select("div.newsHybg>table.list2").select("td[width=560]");
			for (int j = 0; j < les.size(); j++) {
				try {
					urlPath = les.get(j).select("a").attr("href");
					Matcher stockm = stockregex.matcher(urlPath);
					findFlag = stockm.find();
					if (findFlag) {
						tempdoc = IfengSpliderUtil.getDocument(urlPath);
						isFlag = getURLWoreds_1(urlPath, tempdoc);
					}
				} catch (Exception e) {
					logger.error(e);
				}

			}
			// 是否有下一页
			nextPageEle = doc.select("div.newsHybg>table.list2").select("td[colspan=15]").select("a");
			for (int i = 0; i < nextPageEle.size(); i++) {
				nextPageText = nextPageEle.get(i).text();
				if (nextPageText.equals(npText)) {
					nextPageUrl = nextPageEle.get(i).attr("href");
				}

			}
			if (null == nextPageUrl || nextPageUrl.isEmpty()) {
				nextPageUrl = "";
				return nextPageUrl;
			}
			String[] urlArray = spliderUrl.split("\\?");

			nextPageUrl = urlArray[0] + nextPageUrl;
			if (nextPageUrl.equals(spliderUrl)) {
				nextPageUrl = "";
			}
		}

		return nextPageUrl;
	}

	/**
	 * 筛选规则1
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
		Elements authorTimeEle = null;
		Elements vistEle = null;
		String vistStr = "";
		String vist = "";
		String reply = "";
		Elements docText = null;
		Elements docText_font = null;
		Elements docText_span = null;
		Elements nextPages = null;
		Elements nextPageNo = null;
		String nextPageUrl = "";
		Elements cpHrefs = null;
		List<String> repList = new ArrayList<String>();
		int nextPage = -1;

		try {
			//
			cpHrefs = doc.select("div.nav02>span>a");
			if (null != cpHrefs && !cpHrefs.isEmpty() && cpHrefs.size() > 1) {
				externalCategory = cpHrefs.get(0).text() + "_" + cpHrefs.get(1).text();
			}

			// 标题
			urlName = doc.getElementsByClass("lm").select("h3").text();

			// 作者&时间
			authorTimeEle = doc.getElementsByClass("nm").select("div.nm01");
			// 访问人数
			vistEle = doc.getElementsByClass("nm").select("div.nm02");
			// 作者&时间是否为空
			if (!authorTimeEle.isEmpty()) {
				// 时间
				timeData = authorTimeEle.get(0).text().split("发表于")[1];
				// 媒体来源
				authorSource = authorTimeEle.get(0).text().split("发表于")[0];
			}
			if (timeData.isEmpty()) {
				return false;
			}
			if (authorSource.isEmpty()) {
				authorSource = "";
			}
			// 访问量
			if (!vistEle.isEmpty()) {
				vistStr = vistEle.text();
				if (!vistStr.isEmpty()) {
					vist = vistStr.substring(vistStr.indexOf("访问：") + "访问：".length(), vistStr.indexOf("回复：")).trim();
					reply = vistStr.substring(vistStr.indexOf("回复：") + "回复：".length()).trim();
				}
			}
			if (vist.isEmpty()) {
				vist = "0";
			}
			if (reply.isEmpty()) {
				reply = "0";
			}

			// 正文
			docText = doc.select("div#artical_real");
			if (null == docText || docText.isEmpty()) {
				return true;
			}
			// 过滤font
			docText_font = doc.select("div#artical_real").select("font");
			int temp_i = docText_font.size();
			for (int temp_ii = 0; temp_ii < temp_i; temp_ii++) {
				if (docText_font.get(temp_ii).attr("style").contains("font-size:0px")) {
					repList.add(docText_font.get(temp_ii).text());
				}
			}
			// 过滤span
			docText_span = doc.select("div#artical_real").select("span");
			int temp_j = docText_span.size();
			for (int temp_jj = 0; temp_jj < temp_j; temp_jj++) {
				if (docText_span.get(temp_jj).attr("style").contains("display:none")) {
					repList.add(docText_span.get(temp_jj).text());
				}
			}
			String docStr = docText.text();
			for (String fr : repList) {
				docStr = docStr.replace(fr, "");
			}
			sbwords.append(docStr.replaceAll(Jsoup.parse("&nbsp;").text(), " "));
			sbwords1.append(docStr.replaceAll(Jsoup.parse("&nbsp;").text(), " "));

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
			article.CommentCount = Integer.parseInt(reply);
			article.ZhuanFaCount = 0;
			article.PraiseCount = 0;
			article.ReadCount = Integer.parseInt(vist);
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
	 * 取得子页的正文内容
	 * 
	 * @param nextPageUrl
	 *            子页URL
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
	 * 取得子页的正文Html
	 * 
	 * @param nextPageUrl
	 *            子页URL
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
