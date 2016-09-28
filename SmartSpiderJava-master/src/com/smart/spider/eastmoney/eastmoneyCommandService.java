package com.smart.spider.eastmoney;

import java.util.ArrayList;
import java.util.List;

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

/**
 * 
 * 东方财富新闻抓取_55
 * 
 * @author smart
 *
 */
public class eastmoneyCommandService extends NewsSiteCommandService {

	private List<String> urlList = new ArrayList<String>();
	private int maxPageCount = 100;

	public eastmoneyCommandService() {

		CommandName = "com.smart.spider.eastmoney.news";

		Description = "东方财富网";

		Author = "smart";

	}

	@Override
	public void Init(String[] args) {

		/*
		 * 东方财富网->财经 首页 > 财经频道 > 要闻 > 财经导读 首页 > 财经频道 > 要闻 > 产经新闻 首页 > 财经频道 > 要闻
		 * > 产业经济 首页 > 财经频道 > 要闻 > 证券要闻 首页 > 财经频道 > 要闻 > 要闻精华 首页 > 财经频道 > 要闻 >
		 * 上市公司 首页 > 财经频道 > 要闻 > 金融资本 首页 > 财经频道 > 要闻 > 国际经济 首页 > 财经频道 > 要闻 >
		 * 国内经济 首页 > 财经频道 > 要闻 > 宏观经济 首页 > 财经频道 > 评论 > 经济时评 首页 > 财经频道 > 评论 >
		 * 经济学人 首页 > 财经频道 > 评论 > 评论精华 首页 > 财经频道 > 评论 > 商业观察 首页 > 财经频道 > 评论 >
		 * 商界精英 首页 > 财经频道 > 评论 > 投资名家 首页 > 财经频道 > 评论 > 股市评论 首页 > 财经频道 > 评论 >
		 * 政府官员 首页 > 财经频道 > 评论 > 产业透视 首页 > 财经频道 > 评论 > 商学院
		 */
		urlList.add("http://finance.eastmoney.com/news/ccjdd");
		urlList.add("http://finance.eastmoney.com/news/ccjxw");
		urlList.add("http://finance.eastmoney.com/news/ccyjj");
		urlList.add("http://finance.eastmoney.com/news/czqyw");
		urlList.add("http://finance.eastmoney.com/news/cywjh");
		urlList.add("http://finance.eastmoney.com/news/cssgs");
		urlList.add("http://finance.eastmoney.com/news/cjrzb");
		urlList.add("http://finance.eastmoney.com/news/cgjjj");
		urlList.add("http://finance.eastmoney.com/news/cgnjj");
		urlList.add("http://finance.eastmoney.com/news/chgjj");
		urlList.add("http://finance.eastmoney.com/news/cjjsp");
		urlList.add("http://finance.eastmoney.com/news/cjjxr");
		urlList.add("http://finance.eastmoney.com/news/cpljh");
		urlList.add("http://finance.eastmoney.com/news/csygc");
		urlList.add("http://finance.eastmoney.com/news/csyjy");
		urlList.add("http://finance.eastmoney.com/news/ctzmj");
		urlList.add("http://finance.eastmoney.com/news/cgspl");
		urlList.add("http://finance.eastmoney.com/news/czfgy");
		urlList.add("http://finance.eastmoney.com/news/ccyts");
		urlList.add("http://finance.eastmoney.com/news/csxy");

		/*
		 * 东方财富网->股票->市场 首页 > 股票频道 > 市场 > 板块聚焦 首页 > 股票频道 > 市场 > 晨会纪要 首页 > 股票频道 >
		 * 市场 > 大盘分析 首页 > 股票频道 > 市场 > 股市直播 首页 > 股票频道 > 市场 > 机构观点 首页 > 股票频道 > 市场
		 * > 名家专栏 首页 > 股票频道 > 市场 > 市场策略 首页 > 股票频道 > 市场 > 市场精华 首页 > 股票频道 > 市场 >
		 * 市场数据 首页 > 股票频道 > 市场 > 三板
		 */
		urlList.add("http://stock.eastmoney.com/news/cbkjj");
		urlList.add("http://stock.eastmoney.com/news/cchjy");
		urlList.add("http://stock.eastmoney.com/news/cdpfx");
		urlList.add("http://stock.eastmoney.com/news/cgszb");
		urlList.add("http://stock.eastmoney.com/news/cjggd");
		urlList.add("http://stock.eastmoney.com/news/cmjzl");
		urlList.add("http://stock.eastmoney.com/news/csccl");
		urlList.add("http://stock.eastmoney.com/news/cscjh");
		urlList.add("http://stock.eastmoney.com/news/cscsj");
		urlList.add("http://stock.eastmoney.com/news/csb");

		/*
		 * 东方财富网->股票->个股 首页 > 股票频道 > 个股 > 个股导读 首页 > 股票频道 > 个股 > 个股精华 首页 > 股票频道 >
		 * 个股 > 个股点睛 首页 > 财经频道 > 个股 > 个股新闻 首页 > 股票频道 > 个股 > 公司评级 首页 > 股票频道 > 个股
		 * > 公司研究 首页 > 股票频道 > 个股 > 活跃股点评
		 */
		urlList.add("http://stock.eastmoney.com/news/cggdd");
		urlList.add("http://stock.eastmoney.com/news/cggjh");
		urlList.add("http://stock.eastmoney.com/news/cggdj");
		urlList.add("http://stock.eastmoney.com/news/cgsxw");
		urlList.add("http://stock.eastmoney.com/news/cgspj");
		urlList.add("http://stock.eastmoney.com/news/cgsyj");
		urlList.add("http://stock.eastmoney.com/news/chygdp");

		/*
		 * 东方财富网->股票->行业 首页 > 股票频道 > 行业 > 行业导读 首页 > 股票频道 > 行业 > 行业研究
		 */
		urlList.add("http://stock.eastmoney.com/news/chydd");
		urlList.add("http://stock.eastmoney.com/news/chyyj");

		/*
		 * 东方财富网->股票->主力 首页 > 股票频道 > 主力 > 主力精华 首页 > 股票频道 > 主力 > 主力动态 首页 > 股票频道 >
		 * 主力 > 主力持仓 首页 > 股票频道 > 主力 > 主力论市 首页 > 股票频道 > 主力 > 公募基金 首页 > 股票频道 > 主力
		 * > 游资私募 首页 > 股票频道 > 主力 > QFII 首页 > 股票频道 > 主力 > 社保基金 首页 > 股票频道 > 主力 >
		 * 保险资金 首页 > 股票频道 > 主力 > 券商信托
		 */
		urlList.add("http://stock.eastmoney.com/news/czljh");
		urlList.add("http://stock.eastmoney.com/news/czldt");
		urlList.add("http://stock.eastmoney.com/news/czlcc");
		urlList.add("http://stock.eastmoney.com/news/czlls");
		urlList.add("http://stock.eastmoney.com/news/cgmjj");
		urlList.add("http://stock.eastmoney.com/news/cyzsm");
		urlList.add("http://stock.eastmoney.com/news/cqfii");
		urlList.add("http://stock.eastmoney.com/news/csbjj");
		urlList.add("http://stock.eastmoney.com/news/cbxzj");
		urlList.add("http://stock.eastmoney.com/news/cqsxt");

		/*
		 * 东方财富网->股票->新股 首页 > 股票频道 > 新股 > 新股要闻 首页 > 股票频道 > 新股 > 新股策略 首页 > 股票频道 >
		 * 新股 > 新股评论
		 */
		urlList.add("http://stock.eastmoney.com/news/cxgyw");
		urlList.add("http://stock.eastmoney.com/news/cxgcl");
		urlList.add("http://stock.eastmoney.com/news/cxgpl");

		/*
		 * 东方财富网->股票->三板 首页 > 股票频道 > 三板 > 三板导读 首页 > 股票频道 > 三板 > 三板评论 首页 > 股票频道 >
		 * 三板 > 三板动态
		 */
		urlList.add("http://stock.eastmoney.com/news/csbdd");
		urlList.add("http://stock.eastmoney.com/news/csbpl");
		urlList.add("http://stock.eastmoney.com/news/csbdt");

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

		for (int i = 1; i <= maxPageCount; i++) {

			String urlItem = url + "_" + i + ".html";

			if (eastmoneyUtil.getEastMoneyNextPage(urlItem, i)) {

				String htmlContent = HttpClientManage.GetInstance().GetRequest(urlItem, "gb2312");

				Document document = Jsoup.parse(htmlContent);

				int tempUlCount = document.select("div.listBox>div.list>ul").size();

				for (int k = 0; k < tempUlCount; k++) {

					Elements articleLi = document.select("div.listBox>div.list>ul").get(k).select("li");

					for (int j = 0; j < articleLi.size(); j++) {

						try {

							Message message = new Message();
							message.SiteName = "东方财富网";
							message.SpiderName = "com.smart.spider.eastmoney.news";
							message.InternalCategory = "网络媒体_综合门户_东方财富网";
							message.Timestamp = System.currentTimeMillis();
							message.contentType = ContentType.Article;

							Article article = new Article();

							article.Title = articleLi.get(j).select("a").text().trim();

							{
								message.ExternalCategory = document.getElementById("Column_Navigation").text()
										.substring(0,
												document.getElementById("Column_Navigation").text().lastIndexOf(">"))
										.replaceAll(" ", "").replaceAll(">", "_");
							}

							{
								message.Url = articleLi.get(j).select("a").attr("href");
								message.UrlHash = HelperUtil.ToMd5(message.Url);
							}

							{
								Document articleDocument = HttpClientManage.GetInstance().GetDocument(message.Url, "gb2312");
								
								//2015年11月26日 16:27
								String PublishTimeText = articleDocument.select("div.Info>span").get(0).text();								
								article.PublishTime = DateUtil.toDateTime(PublishTimeText, "yyyy年MM月dd日 HH:mm");
								article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);
								
								article.ContentText = articleDocument.getElementById("ContentBody").select("p").text();
								article.ContentHtml = articleDocument.getElementById("ContentBody").select("p").html();
							}

							article.CommentCount = 0;
							article.ZhuanFaCount = 0;
							article.PraiseCount = 0;
							article.ReadCount = 0;

							message.content = article;

							dataProvider.Send(message);

						} catch (Exception e) {

							logger.error(e);

						}
					}
				}
			}
		}
	}
}