package com.smart.spider.bbs.guba;

/**
 * 股吧帖子评论信息
 * 
 * @author smart
 *
 */
public class CommentInfo {

	/**
	 * 评论ID
	 */
	public String Id = "";
	
	/**
	 * 评论引用ID
	 */
	public String ParentId = "";
	
	/**
	 * 主贴ID
	 */
	public String ArticleId = "";
	
	/**
	 * 主贴url
	 */
	public String ArticleUrl = "";
	
	/**
	 * 评论用户ID
	 */
	public String UserInfoId = "";
		
	/**
	 * 评论时间
	 */
	public String CommentTime = "";
	
	/**
	 * 评论内容
	 */
	public String Content = "";
	
	/**
	 * 赞总数
	 */
	public int Zan = 0;
	
}
