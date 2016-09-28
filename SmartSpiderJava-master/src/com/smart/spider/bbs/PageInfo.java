package com.smart.spider.bbs;

public class PageInfo {

	/**
	 * 网址模板
	 */
	public String Url = "";

	/**
	 * 开始页码
	 */
	public int Start = 1;

	/**
	 * 结束页码
	 */
	public int End = 100;

	/**
	 * 步增页码
	 */
	public int Step = 1;

	/**
	 * 每页条数
	 */
	public int PageSize = 80;

	/**
	 * 页码总数
	 */
	public int SizeCount = 0;

	/**
	 * 初始化分页信息
	 */
	public void InitPageInfo() {

	}

	/**
	 * 获取下一个Url
	 * 
	 * @return
	 */
	public String NextUrl() {

		if (Start > End) {

			Start = 1;

			return "";
		}

		String url = Url.replace(".html", "_" + Start + ".html");

		Start += Step;

		return url;
	}

	/**
	 * 回滚一个Url
	 * 
	 * @return
	 */
	public String RollbackUrl() {

		if (Start <= 0) {

			Start = 1;

			return "";
		}

		String url = Url.replace(".html", "_" + Start + ".html");

		Start -= Step;

		return url;

	}

}
