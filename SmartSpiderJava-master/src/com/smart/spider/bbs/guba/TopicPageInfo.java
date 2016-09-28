package com.smart.spider.bbs.guba;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.smart.spider.bbs.PageInfo;
import com.smart.spider.http.HttpClientManage;

/**
 * 论坛主题
 * 
 * @author smart
 *
 */
public class TopicPageInfo extends PageInfo {

	/**
	 * 结束页码
	 */
	private int endPageIndex = 0;

	/**
	 * 论坛主题名称
	 */
	public String Title = "";

	/**
	 * 论坛主题分类
	 */
	public String Category = "";

	/**
	 * 初始化分页信息
	 */
	@Override
	public void InitPageInfo() {

		Document document = HttpClientManage.GetInstance().GetDocument(Url);

		Element pageElement = document.getElementsByClass("pagernums").first();

		if (pageElement != null) {
			// list,600503_|184596|80|1
			String dataPager = pageElement.attr("data-pager");

			String[] dataPagerArray = dataPager.split("\\|");

			SizeCount = Integer.parseInt(dataPagerArray[1]);
			PageSize = Integer.parseInt(dataPagerArray[2]);
			End = SizeCount % PageSize == 0 ? SizeCount / PageSize : SizeCount / PageSize + 1;
			Start = 1;
		}
	}

	/**
	 * 设置终止页码
	 * 
	 * @param end
	 */
	public void setEndPage(int end) {
		
		endPageIndex = end;
		
	}

	/**
	 * 
	 * 获取下一条url
	 * 
	 */
	@Override
	public String NextUrl() {

		// 抓取页数控制
		if (endPageIndex > 0) {

			if (Start > endPageIndex) {
				
				return "";
				
			}
		}

		return super.NextUrl();
	}
}
