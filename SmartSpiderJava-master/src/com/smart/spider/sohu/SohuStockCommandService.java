package com.smart.spider.sohu;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.smart.spider.data.meta.Article;
import com.smart.spider.data.meta.ContentType;
import com.smart.spider.data.meta.Message;
import com.smart.spider.http.HttpClientManage;
import com.smart.spider.util.DateUtil;
import com.smart.spider.util.HelperUtil;
import com.smart.spider.util.NewsSiteCommandService;

public class SohuStockCommandService extends NewsSiteCommandService {

	private List<String> urlList = new ArrayList<String>();
	private static int maxPageCount = 100;

	public SohuStockCommandService() {
		CommandName = "com.smart.spider.sohu.news";

		Description = "搜狐";

		Author = "smart";
	}

	@Override
	public void Init(String[] args) {
		urlList.add("http://stock.sohu.com/news/");
		urlList.add("http://stock.sohu.com/shichang/");
		urlList.add("http://stock.sohu.com/shangshigongsi/");
		urlList.add("http://stock.sohu.com/gegufengyun/");
		urlList.add("http://stock.sohu.com/ipoxinwen/");
		urlList.add("http://stock.sohu.com/stock_scrollnews.shtml");
		urlList.add("http://stock.sohu.com/renwu/");
		urlList.add("http://business.sohu.com/gskb/");
		urlList.add("http://business.sohu.com/gsbg/");
		urlList.add("http://business.sohu.com/s2004/2066/s222393324.shtml");
		urlList.add("http://business.sohu.com/gscx/");
		urlList.add("http://business.sohu.com/gsfz/");
		urlList.add("http://business.sohu.com/s2011/ipo/");
		urlList.add("http://business.sohu.com/s2011/shangshigongsi/");
		urlList.add("http://business.sohu.com/nengyuan/");
		urlList.add("http://business.sohu.com/industry/");
		urlList.add("http://business.sohu.com/qiche/");
		urlList.add("http://business.sohu.com/fangdichan/");
		urlList.add("http://business.sohu.com/keji/");
		urlList.add("http://business.sohu.com/lingshou/");
		urlList.add("http://business.sohu.com/jrjg/");
		urlList.add("http://business.sohu.com/jqgg/");
		urlList.add("http://business.sohu.com/simu/");
		urlList.add("http://business.sohu.com/c241863626/");
		urlList.add("http://business.sohu.com/jrqj/");
		urlList.add("http://business.sohu.com/hgjj/");
		urlList.add("http://money.sohu.com/yh/");
		urlList.add("http://money.sohu.com/yhlc/");
		urlList.add("http://money.sohu.com/zzb/");
		urlList.add("http://money.sohu.com/yhcp/");
		urlList.add("http://money.sohu.com/wzyh1/");
		urlList.add("http://money.sohu.com/dfb/");
		urlList.add("http://money.sohu.com/yhyj/");
		urlList.add("http://money.sohu.com/yhfw/");
		urlList.add("http://money.sohu.com/xtdt/");
		urlList.add("http://money.sohu.com/qsjhlc/");
		urlList.add("http://fund.sohu.com/smdt/");
		urlList.add("http://fund.sohu.com/fund_scrollnews.shtml");
		urlList.add("http://fund.sohu.com/hyfx/");

	}

	@Override
	public void Exec(String[] args) {

		if (null != args && args.length >= 2) {

			try {
				maxPageCount = Integer.parseInt(args[1]);

			} catch (Exception e) {

				e.printStackTrace();

			}
		}

		dataProvider.Open();

		for (String url : urlList) {

			try {

				ExtractHtmlContent(url);

			} catch (Exception e) {

				logger.error(e);

			}

		}

		dataProvider.Close();

	}

	private void ExtractHtmlContent(String url) {
		List<String> urlList = SohuUtil.getNextBUrl(url);

		for (int i = 0; i < urlList.size(); i++) {
			String htmlContent = HttpClientManage.GetInstance().GetRequest(urlList.get(i),
					SohuUtil.connect(urlList.get(i)));

			Document document = Jsoup.parse(htmlContent);
			Elements pageNums = document.select("div.pages").select("script");
			if (pageNums.size() > 0) {
				String temp1 = pageNums.toString().split("showPages = ")[1];
				int showPages = Integer.parseInt(temp1.substring(0, temp1.indexOf(";")).replaceAll("\\s*", ""));// 最大页数

				String temp = pageNums.toString().split("maxPage = ")[1];
				int maxPage = Integer.parseInt(temp.substring(0, temp.indexOf(";")).replaceAll("\\s*", ""));// 页数

				if (showPages > maxPageCount) {// 获取最大循环页数
					showPages = maxPageCount;
				}
				String wordsurl = "";
				for (int j = 0; j < showPages; j++) {
					String url_temp = urlList.get(i);
					if (j == 0) {
						wordsurl = url_temp;
					} else {
						int pageNum = maxPage - j;
						if (pageNum >= 1) {
							wordsurl = url_temp + "index_" + pageNum + ".shtml";
						} else {
							break;
						}

					}
					getUrlPath(wordsurl);
				}
			} else {
				getUrlPath(url);
			}

		}
	}

	private void getUrlPath(String url) {
		String htmlContent = HttpClientManage.GetInstance().GetRequest(url, SohuUtil.connect(url));

		Document document = Jsoup.parse(htmlContent);

		Elements ele_li = document.getElementsByClass("f14list").select("ul>li");
		for (int i = 0; i < ele_li.size(); i++) {
			Elements ele_li_a = document.getElementsByClass("f14list").select("ul>li").get(i).select("a");
			if (ele_li_a.size() > 0) {
				getwords(ele_li_a.attr("href"), ele_li_a.text());
			}

		}

	}

	private void getwords(String urlPath, String urlname) {// 文章链接,文章名称

		String htmlContent = HttpClientManage.GetInstance().GetRequest(urlPath, SohuUtil.connect(urlPath));

		Document document = Jsoup.parse(htmlContent);

		String mixMes_temp = document.select("div#mypos>span").text().replaceAll(" +", "").replaceAll("-", ">");
		String mixMes = "搜狐_财经_" + mixMes_temp.substring(mixMes_temp.lastIndexOf(">") + 1);

		Message message = new Message();
		message.SiteName = "搜狐";
		message.SpiderName = this.CommandName;
		message.InternalCategory = "网络媒体_综合门户_搜狐";
		message.Timestamp = System.currentTimeMillis();
		message.contentType = ContentType.Article;

		Article article = new Article();
		article.Title = urlname;
		message.ExternalCategory = mixMes;
		message.Url = urlPath;
		message.UrlHash = HelperUtil.ToMd5(message.Url);
		article.PublishTime = DateUtil.toDateTime(document.getElementById("pubtime_baidu").text().replaceAll(" ", " "),
				"yyyy-MM-dd HH:mm:ss");// 发布时间
		article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);

		if (document.getElementById("contentText").children().size() > 0) {
			article.ContentText = document.getElementById("contentText").child(0).text().replaceAll("　+", "");// 文章内容
			article.ContentHtml = document.getElementById("contentText").child(0).html();// 带HTML格式的文章内容
		}

		article.CommentCount = 0;
		article.ZhuanFaCount = 0;
		article.PraiseCount = 0;
		article.ReadCount = 0;
		if (document.getElementById("author_baidu") != null) {
			article.Author = document.getElementById("author_baidu").text().replaceAll("作者：", "");// 作者
		} else {
			article.Author = "暂无";
		}

		article.Referrer = document.getElementById("media_span").text();//// 文章来源

		message.content = article;

		if (StringUtils.isNotEmpty(article.ContentText)) {
			dataProvider.Send(message);
		}

	}

}
