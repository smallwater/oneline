package com.smart.spider.bbs.guba;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 股吧论坛帖子信息
 * 
 * @author smart
 * 
 *         example:
 * 
 *         { "TopicId": "192631786", "UserInfoId": "6733094365158544", "Url":
 *         "http://guba.eastmoney.com/news,bk0600,192631786.html", "Title":
 *         "您好， 这是新项目，该公司已经与德国某汽车生产商签订了一笔2年2亿的订单，后续利", "ContentText":
 *         "您好， 这是新项目，该公司已经与德国某汽车生产商签订了一笔2年2亿的订单，后续利润相当可观。订单合同正在和企业沟通，可以给投资者提供。该企业也是济宁市市政府重点扶持企业，政府承诺，给企业在新三板挂牌时提供推荐函。"
 *         , "ContentHtml":
 *         "您好， 这是新项目，该公司已经与德国某汽车生产商签订了一笔2年2亿的订单，后续利润相当可观。订单合同正在和企业沟通，可以给投资者提供。该企业也是济宁市市政府重点扶持企业，政府承诺，给企业在新三板挂牌时提供推荐函。\n<br>"
 *         , "Referrer": "", "PublishTime": "2015-08-05 08:55:44",
 *         "PublishDevice": "东方财富网iPhone版", "ZhuanFaCount": 0, "PraiseCount": 0,
 *         "ReadCount": 873, "CommentCount": 3 }
 *
 */
public class ArticleInfo {

	/**
	 * 文章ID
	 */
	public String Id = "";

	/**
	 * 作者ID/楼主
	 */
	public String UserInfoId = "";

	/**
	 * Url
	 */
	public String Url = "";

	/**
	 * 文章标题
	 */
	public String Title = "";

	/**
	 * 文章正文文本
	 */
	public String ContentText = "";

	/**
	 * 文章正文HTML
	 */
	public String ContentHtml = "";

	/**
	 * 文章来源
	 */
	public String Referrer = "";

	/**
	 * 发布时间:2015-11-26 15:00:56
	 */
	public String PublishTime = "";

	/**
	 * 发布环境设备
	 */
	public String PublishDevice = "";

	/**
	 * 转发总数
	 */
	public int ZhuanFaCount = 0;

	/**
	 * 赞总数
	 */
	public int PraiseCount = 0;

	/**
	 * 阅读总数
	 */
	public int ReadCount = 0;

	/**
	 * 评论总数
	 */
	public int CommentCount = 0;
	
	/**
	 * 论坛主题分类
	 */
	public String Category = "";

}
