package com.smart.spider.data.meta;

/**
 * 
 * 论坛主题
 * 
 * @author smart
 *
 */
public class BBSTopic extends BaseContent{

	/**
	 * 股吧ID
	 */
	public String Id;

	/**
	 * 股吧名称
	 */
	public String Title;

	/**
	 * 帖子总数
	 */
	public int ArticleCount;

	/**
	 * 关注人数
	 */
	public int ConcernCount;

	public BBSTopic() {
		Id = "";
		Title = "";
		ArticleCount = 0;
		ConcernCount = 0;
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
