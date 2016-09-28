package com.smart.spider.bbs.guba;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.smart.spider.bbs.BBSEngine;
import com.smart.spider.data.ActiveMqDataProvider;
import com.smart.spider.data.meta.Message;
import com.smart.spider.http.HttpClientManage;

/**
 * 东方财富股吧抓取引擎
 * 
 * @author smart
 *
 */
public final class GuBaBBSEngine extends BBSEngine {

	private final Logger _logger = LogManager.getLogger(GuBaBBSEngine.class);
	private CategoryList _categoryList = new CategoryList();

	/**
	 * 是否提取评论信息，默认不提取
	 */
	public boolean isExtractCommentInfo = false;

	/**
	 * 是否提取用户信息，默认开启提取
	 */
	public boolean isExtractUserInfo = true;

	/**
	 * 是否提取主贴信息，默认开启提取
	 */
	public boolean isExtractArticleInfo = true;

	/**
	 * 是否提取股吧信息，默认开启提取
	 */
	public boolean isExtractTopicInfo = true;

	/**
	 * 构造函数
	 */
	public GuBaBBSEngine() {

		CommandName = "com.smart.spider.guba";

		Description = "东方财富网股吧";

		Author = "smart";

	}

	/**
	 * 初始化股吧分类目录
	 */
	@Override
	public void Init(String[] args) {

		try {

			//this.dataProvider = new KafkaDataProvider();
			this.dataProvider = new ActiveMqDataProvider();

			_categoryList.Init();

			statusIndex.Topic.TotalCount += _categoryList.DataList.size();
			statusIndex.Print(this._logger);

		} catch (Exception e) {

			_logger.error(e);

		}
	}

	/**
	 * 命令选项：
	 * 
	 * 程序 栏目标识 最多抓取页数
	 * 
	 * jar com.mfniu.spider.guba 0
	 * 
	 * 
	 * 抓取流程：
	 * 
	 * 1、逐个贴吧扫帖子列表
	 * 
	 * 2、抓取帖子主内容、阅读量、评论量、转发、赞
	 * 
	 * 3、抓取主贴作者用户信息
	 * 
	 * 4、逐条抓取评论信息
	 * 
	 * 5、抓取跟帖评论用户信息
	 */
	@Override
	public void Exec(String[] args) {

		int endPageIndex = 0;

		try {

			// 默认丢弃第一个参数
			if (null != args && args.length >= 2) {

				endPageIndex = Integer.parseInt(args[1]);
			}

		} catch (Exception e) {

			_logger.error(e);

		}

		try {

			this.dataProvider.Open();

			for (TopicPageInfo topic : _categoryList.DataList) {

				// 设置抓取结束页码
				topic.setEndPage(endPageIndex);

				// 初始化分页信息
				topic.InitPageInfo();

				statusIndex.Article.TotalCount += topic.SizeCount;
				statusIndex.Print(this._logger);

				// 抓取股吧栏目信息
				ExtractTopicInfo(topic);

				// 提取帖子列表
				ExtractTopicArticleList(topic);
			}

			this.dataProvider.Close();

			_logger.info("恭喜你，东方财富股吧抓取完毕");

		} catch (Exception e) {
			_logger.error(e);
		}
	}

	/**
	 * 获取股吧帖子列表，支持自动分页获取
	 * 
	 * @param topic
	 */
	private void ExtractTopicArticleList(TopicPageInfo topic) {

		List<ArticleInfo> articleInfoList = new ArrayList<ArticleInfo>();
		List<UserInfo> userInfoList = new ArrayList<UserInfo>();

		// 循环处理贴吧文章帖子列表
		for (; topic.Start <= topic.End;) {

			String currentUrl = topic.NextUrl();

			if (currentUrl == null | currentUrl == "") {
				break;
			}

			Document document = HttpClientManage.GetInstance().GetDocument(currentUrl);

			if (document == null) {
				continue;
			}

			Element contentElement = document.getElementById("articlelistnew");

			Elements links = contentElement.getElementsByTag("a");

			for (Element link : links) {

				try {

					String uid = link.attr("data-popper");

					if (uid == null | uid == "") {

						// 进入帖子信息提取规则
						ArticleInfo articleInfo = new ArticleInfo();
						articleInfo.Url = link.attr("href");
						articleInfo.Title = link.attr("title");
						articleInfo.Category = topic.Title;

						if (articleInfo.Url.startsWith("/") == false) {
							articleInfo.Url = "/" + articleInfo.Url;
						}

						if (articleInfo.Title != null && articleInfo.Title != "" && articleInfo.Url != null
								&& articleInfo.Url != "") {

							if (articleInfo.Url.startsWith("http://") == false) {
								articleInfo.Url = "http://guba.eastmoney.com" + articleInfo.Url;
							}

							articleInfoList.add(articleInfo);
						}

					} else {
						// 进入发帖作者信息提取规则
						UserInfo userInfo = new UserInfo();
						userInfo.Id = uid;
						userInfo.UserName = link.text();
						userInfo.Url = link.attr("href");

						if (userInfo.Id != null && !Objects.equals(userInfo.Id, "")) {
							userInfoList.add(userInfo);
						}
					}

				} catch (Exception e) {
					_logger.error(e);
				}
			}

			// 提取文章信息
			statusIndex.Article.TotalCount += articleInfoList.size();
			ExtractArticleInfo(articleInfoList);
			articleInfoList.clear();

			// 提取作者信息
			statusIndex.User.TotalCount += userInfoList.size();
			ExtractUserInfo(userInfoList);
			userInfoList.clear();

			statusIndex.Print(_logger);

		}

	}

	/**
	 * 获取股吧主贴文章内容
	 * 
	 * @param articleInfo
	 */
	private void ExtractArticleInfo(ArticleInfo articleInfo) {

		if (isExtractArticleInfo == false) {

			return;

		}

		String htmlContent = HttpClientManage.GetInstance().GetRequest(articleInfo.Url);

		if (htmlContent.equals("")) {

			statusIndex.Article.ErrorCount += 1;

			return;
		}

		Document document = Jsoup.parse(htmlContent);

		if (document == null) {

			statusIndex.Article.ErrorCount += 1;

			return;
		}

		// articleId
		try {

			Pattern articleIdCompile = Pattern.compile("topicid=\\\"[0-9]+\\\"");
			Matcher articleIdMatcher = articleIdCompile.matcher(htmlContent);
			if (articleIdMatcher.find()) {
				String articleIdScript = articleIdMatcher.group(0).replace("\"", "").replace("topicid=", "");
				articleInfo.Id = articleIdScript;
			}

		} catch (Exception e) {

			_logger.error(e);

		}

		// content and referrer
		try {

			Element contentElement = document.getElementsByClass("stockcodec").first();

			if (contentElement != null) {
				articleInfo.ContentHtml = contentElement.html();
				articleInfo.ContentText = contentElement.text().trim();
			}

		} catch (Exception e) {

			_logger.error(e);

		}

		// referrer
		try {

			Element referrerElement = document.getElementById("zw_header");

			if (referrerElement != null) {
				articleInfo.Referrer = referrerElement.text().replace("来源：", "");
			}

		} catch (Exception e) {

			_logger.error(e);

		}

		// 发布时间、在什么设备发布
		try {

			Element publishTimeElement = document.getElementsByClass("zwfbtime").first();

			String[] publishTimeArray = publishTimeElement.text().replace("发表于", "").trim().split(" ");

			articleInfo.PublishTime = publishTimeArray[0] + " " + publishTimeArray[1];

			articleInfo.PublishDevice = publishTimeArray[2];

		} catch (Exception e) {

			_logger.error(e);

		}

		// 转发
		try {
			String zhuanfaCount = document.getElementById("zwconbtnsi_zf").text();

			if (zhuanfaCount != null) {

				zhuanfaCount = zhuanfaCount.trim().replace("转发(", "").replace(")", "").replace("转发", "");

				if (StringUtil.isBlank(zhuanfaCount) == false) {
					articleInfo.ZhuanFaCount = Integer.parseInt(zhuanfaCount);
				}
			}

		} catch (Exception e) {

			_logger.error(e);

		}

		// 赞
		try {

			String praiseUrl = "http://iguba.eastmoney.com/interf/guba.aspx?action=getpraise&id=" + articleInfo.Id;
			String praiseJsonContent = HttpClientManage.GetInstance().GetRequest(praiseUrl);

			Pattern praiseCompile = Pattern.compile("[0-9]+");
			Matcher praiseMatcher = praiseCompile.matcher(praiseJsonContent);
			if (praiseMatcher.find()) {
				String praiseNumber = praiseMatcher.group(0);
				articleInfo.PraiseCount = Integer.parseInt(praiseNumber);
			}

		} catch (Exception e) {

			_logger.error(e);

		}

		// 阅读总数
		try {

			Pattern readCountCompile = Pattern.compile("num=[0-9]+");
			Matcher readCountMatcher = readCountCompile.matcher(htmlContent);
			if (readCountMatcher.find()) {
				String readCountScript = readCountMatcher.group(0).replace("num=", "");
				articleInfo.ReadCount = Integer.parseInt(readCountScript);
			}

		} catch (Exception e) {
			_logger.error(e);
		}

		// 评论总数
		try {

			Pattern commentCountCompile = Pattern.compile("pinglun_num=[0-9]+");
			Matcher commentCountMatcher = commentCountCompile.matcher(htmlContent);
			if (commentCountMatcher.find()) {
				String commentCountScript = commentCountMatcher.group(0).replace("pinglun_num=", "");
				articleInfo.CommentCount = Integer.parseInt(commentCountScript);
			}

		} catch (Exception e) {
			_logger.error(e);
		}

		// author
		try {
			Element authorElement = document.getElementById("zwconttphoto");

			if (authorElement != null) {

				authorElement = authorElement.getElementsByTag("a").first();

				if (authorElement != null) {
					articleInfo.UserInfoId = authorElement.attr("data-popper");
				}
			}

		} catch (Exception e) {

			_logger.error(e);

		}

		// 转换协议消息格式
		Message message = MessageConvertProtocol.ConvertArticleInfo(articleInfo);

		// 发送到kafka
		dataProvider.Send(message);

		statusIndex.Article.SuccessCount += 1;

		if (isExtractCommentInfo == true) {

			// 提取文章评论分页信息
			ExtractCommentPageInfo(articleInfo, document);

			statusIndex.Print(_logger);

		}

	}

	/**
	 * 获取股吧主贴文章内容
	 * 
	 * @param articleInfoList
	 */
	private void ExtractArticleInfo(List<ArticleInfo> articleInfoList) {

		for (ArticleInfo articleInfo : articleInfoList) {

			ExtractArticleInfo(articleInfo);

		}

	}

	/**
	 * 获取股吧用户详细信息
	 * 
	 * @param userinfo
	 */
	private void ExtractUserInfo(UserInfo userinfo) {

		if (isExtractUserInfo == false) {

			return;

		}

		String htmlContent = HttpClientManage.GetInstance().GetRequest(userinfo.Url);

		if (htmlContent.equals(htmlContent)) {

			statusIndex.User.ErrorCount += 1;

			return;

		}

		Document document = Jsoup.parse(htmlContent);

		if (document == null) {

			statusIndex.User.ErrorCount += 1;

			return;
		}

		// 用户昵称
		if (userinfo.equals("") == false) {
			try {

				Element usernameElement = document.getElementsByClass("taname").first();

				if (usernameElement != null) {

					userinfo.UserName = usernameElement.text().trim();

				}

			} catch (Exception e) {

				_logger.error(e);

			}
		}

		// 用户简介
		try {
			Element summaryElement = document.getElementsByClass("taintro").first();

			if (summaryElement != null) {

				userinfo.Summary = summaryElement.text().trim();

			}

		} catch (Exception e) {

			_logger.error(e);

		}

		// 注册时间
		try {

			Element regtimeElement = document.getElementById("influence");

			if (regtimeElement != null) {

				Pattern regtimeCompile = Pattern.compile("([0-9-]+)");

				Matcher regtimeMatcher = regtimeCompile.matcher(regtimeElement.text());

				if (regtimeMatcher.find()) {

					String regtimeText = regtimeMatcher.group(0);

					regtimeText = regtimeText.replace("(", "").replace(")", "");

					userinfo.RegTime = regtimeText;
				}
			}

		} catch (Exception e) {

			_logger.error(e);

		}

		// 影响力
		try {

			Element starsElement = document.getElementsByClass("stars").first();

			if (starsElement != null) {

				String dataInfluence = starsElement.attr("data-influence");

				userinfo.Force = Integer.parseInt(dataInfluence);
			}
		} catch (Exception e) {
			_logger.error(e);
		}

		// 总访问 and 今日访问
		try {

			Element totalAccessElement = document.getElementsByClass("sumfw").first();

			if (totalAccessElement != null) {

				Elements spanList = totalAccessElement.getElementsByTag("span");

				// 总访问
				if (spanList.size() >= 1) {

					Element totalAccess = spanList.get(0);

					if (totalAccess != null) {

						String totalText = totalAccess.text().replace("次", "");

						if (StringUtil.isBlank(totalText) == false) {

							userinfo.TotalAccess = Integer.parseInt(totalText);

						}
					}
				}

				// 今日访问
				if (spanList.size() >= 2) {

					Element todayAccess = spanList.get(1);

					if (todayAccess != null) {

						String todayText = todayAccess.text().replace("次", "");

						if (StringUtil.isBlank(todayText) == false) {

							userinfo.TodayAccess = Integer.parseInt(todayText);

						}
					}
				}
			}

		} catch (Exception e) {
			_logger.error(e);
		}

		try {

			Element bodyElement = document.getElementsByClass("photo").first();

			if (bodyElement != null) {

				Elements tdElement = bodyElement.getElementsByTag("td");

				// 自选股
				{
					if (tdElement.size() >= 1) {

						String stockCountText = tdElement.get(0).text().replace("自选股", "").trim();

						userinfo.StockCount = Integer.parseInt(stockCountText);
					}
				}

				// 关注总数
				{
					if (tdElement.size() >= 2) {

						String focusText = tdElement.get(1).text().replace("关注的人", "").trim();

						userinfo.Focus = Integer.parseInt(focusText);
					}
				}

				// 粉丝总数
				{
					if (tdElement.size() >= 3) {
						String fansText = tdElement.get(2).text().replace("粉丝", "").trim();

						userinfo.Fans = Integer.parseInt(fansText);
					}
				}

			}

		} catch (Exception e) {
			_logger.error(e);
		}

		Message message = MessageConvertProtocol.ConvertUserInfo(userinfo);

		dataProvider.Send(message);

		statusIndex.User.SuccessCount += 1;
	}

	/**
	 * 获取股吧用户详细信息
	 * 
	 * @param userInfoList
	 */
	private void ExtractUserInfo(List<UserInfo> userInfoList) {

		for (UserInfo userInfo : userInfoList) {

			ExtractUserInfo(userInfo);

		}

	}

	/**
	 * 提取贴吧基本信息
	 * 
	 * @param DataList
	 */
	private void ExtractTopicInfo(List<TopicPageInfo> DataList) {

		for (TopicPageInfo bbsTopic : DataList) {

			ExtractTopicInfo(bbsTopic);

		}

	}

	/**
	 * 
	 * 提取贴吧基本信息
	 * 
	 * @param
	 */
	private void ExtractTopicInfo(TopicPageInfo topicPageInfo) {

		if (isExtractTopicInfo == false) {
			return;
		}

		TopicInfo topicInfo = new TopicInfo();
		topicInfo.Id = topicPageInfo.Url;
		topicInfo.Title = topicPageInfo.Title;
		topicInfo.Category = topicPageInfo.Category;
		topicInfo.ArticleCount = topicPageInfo.SizeCount;
		topicInfo.ConcernCount = 0;

		// 提取关注人数
		try {

			// 提取股票代码
			String stockCode = "";

			Pattern stockCodeCompile = Pattern.compile("following\\\"\\:\\\"[0-9]{6}\\\"");

			Matcher stockCodeMatcher = stockCodeCompile.matcher(topicPageInfo.Url);

			if (stockCodeMatcher.find()) {

				stockCode = stockCodeMatcher.group(0).trim();
			}

			// 调用关注人数API接口
			if (stockCode.equals("") == false) {

				String url = "http://iguba.eastmoney.com/action.aspx?action=opopstock&code=" + stockCode;

				String jsonContent = HttpClientManage.GetInstance().GetRequest(url);

				Pattern concernCountCompile = Pattern.compile("following\\\"\\:\\\"[0-9]+\\\"");

				Matcher concernCountMatcher = concernCountCompile.matcher(jsonContent);

				if (concernCountMatcher.find()) {

					String concernCountText = concernCountMatcher.group(0);

					concernCountText = concernCountText.replace("following", "").replace(":", "").replace("\"", "")
							.trim();

					topicInfo.ConcernCount = Integer.parseInt(concernCountText);
				}
			}

			// 转换协议数据格式
			Message message = MessageConvertProtocol.ConvertTopicInfo(topicInfo);

			// 发送到kafka
			dataProvider.Send(message);

			statusIndex.Topic.SuccessCount += 1;

		} catch (Exception e) {

			_logger.error(e);

			statusIndex.Topic.ErrorCount += 1;
		}
	}

	/**
	 * 提取文章评论分页信息
	 * 
	 * @param articleInfo
	 * @param document
	 */
	private void ExtractCommentPageInfo(ArticleInfo articleInfo, Document document) {

		// 获取帖子评论信息
		if (articleInfo.CommentCount <= 0) {
			return;
		}

		// 初始化帖子评论分页信息
		CommentPageInfo commentPageInfo = new CommentPageInfo();
		commentPageInfo.Url = articleInfo.Url;
		commentPageInfo.InitPageInfo(document);

		statusIndex.Comment.TotalCount += commentPageInfo.SizeCount;

		String currentUrl = null;

		for (; commentPageInfo.Start <= commentPageInfo.End;) {

			// extract comment info
			if (currentUrl == null || currentUrl == "") {

				ExtractCommentInfoDocument(document, articleInfo);

			} else {

				Document commentDocument = HttpClientManage.GetInstance().GetDocument(currentUrl);

				ExtractCommentInfoDocument(commentDocument, articleInfo);
			}

			// next page
			currentUrl = commentPageInfo.NextUrl();

			if (currentUrl == null | currentUrl == "") {
				break;
			}
		}

	}

	/**
	 * 提取文章评论
	 * 
	 * @param document
	 * @param articleInfo
	 */
	private void ExtractCommentInfoDocument(Document document, ArticleInfo articleInfo) {

		try {

			Element commentBodyElement = document.getElementById("zwlist");

			Elements commentContentListElement = commentBodyElement.getElementsByClass("zwli");

			List<CommentInfo> CommentInfoList = new ArrayList<CommentInfo>();

			for (Element commentItem : commentContentListElement) {

				CommentInfo commentInfo = new CommentInfo();
				commentInfo.ArticleId = articleInfo.Id;
				commentInfo.ArticleUrl = articleInfo.Url;

				// 评论id
				{
					commentInfo.Id = commentItem.attr("data-huifuid");
				}

				// 评论用户
				{
					commentInfo.UserInfoId = commentItem.attr("data-huifuuid");
				}

				// 评论内容
				{
					commentInfo.Content = commentItem.getElementsByClass("stockcodec").first().html().trim();
				}

				// 评论时间
				{
					commentInfo.CommentTime = commentItem.getElementsByClass("zwlitime").first().text()
							.replace("发表于", "").trim();
				}

				// 未处理
				{
					commentInfo.ParentId = "";
					commentInfo.Zan = 0;
				}

				CommentInfoList.add(commentInfo);
			}

			statusIndex.Comment.TotalCount += CommentInfoList.size();
			
			for (CommentInfo commentInfo : CommentInfoList) {

				Message message = MessageConvertProtocol.ConvertCommentInfo(commentInfo);

				dataProvider.Send(message);

				statusIndex.Comment.SuccessCount += 1;
			}

		} catch (Exception e) {

			_logger.error(e);

			statusIndex.Comment.ErrorCount += 1;
		}
	}
}