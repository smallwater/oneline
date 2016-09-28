package com.smart.spider.data.meta;

/**
 * 普通文章
 *
 * @author smart
 */
public class Article extends BaseContent {

	/**
	 * 文章标题
	 */
	public String Title;

	/**
	 * 发布时间:2015-11-26 14:57:59
	 */
	public String PublishTime;

	/**
	 * 发布时间戳
	 */
	public long PublishTimestamp;

	/**
	 * 文章作者
	 */
	public String Author;

	/**
	 * 文章来源
	 */
	public String Referrer;

	/**
	 * 文章正文文本
	 */
	public String ContentText;

	/**
	 * 文章正文HTML
	 */
	public String ContentHtml;

	/**
	 * 评论总数
	 */
	public int CommentCount;

	/**
	 * 转发总数
	 */
	public int ZhuanFaCount;

	/**
	 * 赞总数
	 */
	public int PraiseCount;

	/**
	 * 阅读总数
	 */
	public int ReadCount;

	public Article() {
		Title = "";
		PublishTime = "";
		PublishTimestamp = 0;
		Author = "";
		Referrer = "";
		ContentText = "";
		ContentHtml = "";
		CommentCount = 0;
		ZhuanFaCount = 0;
		PraiseCount = 0;
		ReadCount = 0;
	}

	@Override
	public boolean Verify() {

		if (null == Title || "".equals(Title) || Title.isEmpty()) {
			return false;
		}
		if (null == PublishTime || PublishTime.isEmpty()) {
			return false;
		}
		if (null == ContentText || ContentText.isEmpty()) {
			return false;
		}
		if (null == ContentHtml || ContentHtml.isEmpty()) {
			return false;
		}

		return true;
	}

	@Override
	public void Trim() {

		if (this.Verify()) {

			ContentText = ContentText.trim();
			ContentHtml = ContentHtml.trim();
			Title = Title.trim();
			PublishTime = PublishTime.trim();
			ContentText = ClearNoise(ContentText);
			PublishTimestamp = super.FormatToTimeStamp(PublishTimestamp);
			PublishTime = super.FormatToDateTime(PublishTimestamp);

		}

	}
}
