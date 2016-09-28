package com.smart.spider.bbs.guba;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.smart.spider.data.meta.BBSArticle;
import com.smart.spider.data.meta.BBSComment;
import com.smart.spider.data.meta.BBSTopic;
import com.smart.spider.data.meta.BBSUser;
import com.smart.spider.data.meta.ContentType;
import com.smart.spider.data.meta.Message;
import com.smart.spider.util.DateUtil;
import com.smart.spider.util.HelperUtil;

/**
 * 
 * 消息转换协议
 * 
 * @author smart
 *
 */
public class MessageConvertProtocol {

	private static final Logger _logger = LogManager.getLogger(MessageConvertProtocol.class);
	
	/**
	 * 转换贴吧栏目消息
	 * 
	 * @param topicInfo
	 * @return
	 */
	public static Message ConvertTopicInfo(TopicInfo topicInfo) {

		Message message = new Message();
		
		message.contentType = ContentType.BBSTopic;
		message.SiteName = "东方财富股吧";
		message.SpiderName = "com.smart.spider.guba";
		message.InternalCategory = "金融社区_股吧_东方财富股吧_栏目";
		message.ExternalCategory = topicInfo.Category;
		message.Timestamp = System.currentTimeMillis();
		message.Url = topicInfo.Id;
		message.UrlHash = HelperUtil.ToMd5(message.Url);

		BBSTopic bbstopicContent = new BBSTopic();
		bbstopicContent.Id = topicInfo.Id;
		bbstopicContent.Title = topicInfo.Title;
		bbstopicContent.ArticleCount = topicInfo.ArticleCount;
		bbstopicContent.ConcernCount = topicInfo.ConcernCount;

		message.content = bbstopicContent;
				
		_logger.info("bbstopic:" + message.Url);

		return message;
	}

	/**
	 * 转换用户信息
	 * 
	 * @param userInfo
	 * @return
	 */
	public static Message ConvertUserInfo(UserInfo userInfo) {

		Message message = new Message();

		message.contentType = ContentType.BBSUser;
		message.SiteName = "东方财富股吧";
		message.SpiderName = "com.smart.spider.guba";
		message.InternalCategory = "金融社区_股吧_东方财富股吧_博主";
		message.ExternalCategory = message.InternalCategory;
		message.Timestamp = System.currentTimeMillis();
		message.Url = userInfo.Url;
		message.UrlHash = HelperUtil.ToMd5(message.Url);

		BBSUser bbsUser = new BBSUser();
		bbsUser.Id = userInfo.Id;
		bbsUser.UserName = userInfo.UserName;
		bbsUser.Summary = userInfo.Summary;
		bbsUser.RegTime = userInfo.RegTime;
		bbsUser.Force = userInfo.Force;
		bbsUser.TotalAccess = userInfo.TotalAccess;
		bbsUser.Fans = userInfo.Fans;
		bbsUser.Focus = userInfo.Focus;
		bbsUser.StockCount = userInfo.StockCount;

		message.content = bbsUser;
		
		_logger.info("bbsuser:" + message.Url);

		return message;

	}

	/**
	 * 转换主贴/文章消息
	 * 
	 * @param articleInfo
	 * @return
	 */
	public static Message ConvertArticleInfo(ArticleInfo articleInfo) {

		Message message = new Message();

		message.contentType = ContentType.BBSArticle;
		message.SiteName = "东方财富股吧";
		message.SpiderName = "com.smart.spider.guba";
		message.InternalCategory = "金融社区_股吧_东方财富股吧_主贴";
		message.ExternalCategory = articleInfo.Category;
		message.Timestamp = System.currentTimeMillis();
		message.Url = articleInfo.Url;
		message.UrlHash = HelperUtil.ToMd5(message.Url);

		BBSArticle bbsArticle = new BBSArticle();
		bbsArticle.Id = articleInfo.Id;
		bbsArticle.UserInfoId = articleInfo.UserInfoId;
		bbsArticle.Title = articleInfo.Title;
		bbsArticle.PublishTime = DateUtil.toDateTime(articleInfo.PublishTime);
		bbsArticle.PublishTimestamp = DateUtil.toTimeStamp(bbsArticle.PublishTime);
		bbsArticle.Author = articleInfo.UserInfoId;
		bbsArticle.Referrer = articleInfo.Referrer;
		bbsArticle.ContentText = articleInfo.ContentText;
		bbsArticle.ContentHtml = articleInfo.ContentHtml;
		bbsArticle.CommentCount = articleInfo.CommentCount;
		bbsArticle.ZhuanFaCount = articleInfo.ZhuanFaCount;
		bbsArticle.PraiseCount = articleInfo.PraiseCount;
		bbsArticle.ReadCount = articleInfo.ReadCount;

		message.content = bbsArticle;
		
		_logger.info("bbsarticle:" + message.Url);

		return message;

	}

	/**
	 * 转换评论信息
	 * 
	 * @param commentInfo
	 * @return
	 */
	public static Message ConvertCommentInfo(CommentInfo commentInfo) {

		Message message = new Message();

		message.contentType = ContentType.BBSComment;
		message.SiteName = "东方财富股吧";
		message.SpiderName = "com.smart.spider.guba";
		message.InternalCategory = "金融社区_股吧_东方财富股吧_评论";
		message.ExternalCategory = message.InternalCategory;
		message.Timestamp = System.currentTimeMillis();
		
		message.Url = commentInfo.ArticleUrl;
		message.UrlHash = HelperUtil.ToMd5(message.Url);

		BBSComment bbsComment = new BBSComment();
		bbsComment.Id = commentInfo.Id;
		bbsComment.ParentId = commentInfo.ParentId;
		bbsComment.ArticleId = commentInfo.ArticleId;
		bbsComment.UserInfoId = commentInfo.UserInfoId;
		bbsComment.CommentTime = commentInfo.CommentTime;
		bbsComment.Content = commentInfo.Content;
		bbsComment.Zan = commentInfo.Zan;

		message.content = bbsComment;
		
		_logger.info("bbscomment:" + message.Url);

		return message;

	}

}
