package com.smart.spider.data.meta;

/**
 * 
 * 论坛评论
 * 
 * @author smart
 *
 */
public class BBSComment extends BaseContent{
	
	/**
	 * 评论ID
	 */
	public String Id;
	
	/**
	 * 评论引用ID
	 */
	public String ParentId;
	
	/**
	 * 主贴ID
	 */
	public String ArticleId;
	
	/**
	 * 评论用户ID
	 */
	public String UserInfoId;
		
	/**
	 * 评论时间
	 */
	public String CommentTime;
	
	/**
	 * 评论内容
	 */
	public String Content;
	
	/**
	 * 赞总数
	 */
	public int Zan;

	public BBSComment() {
		Id = "";
		ParentId = "";
		ArticleId = "";
		UserInfoId = "";
		CommentTime = "";
		Content = "";
		Zan = 0;
	}

	@Override
	public boolean Verify() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void Trim() {
		// TODO Auto-generated method stub
		
	}
}
