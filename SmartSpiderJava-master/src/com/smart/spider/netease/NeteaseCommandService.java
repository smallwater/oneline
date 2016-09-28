package com.smart.spider.netease;

import com.smart.spider.util.NewsSiteCommandService;

/**
 * 
 * 网易抓取
 * 
 * @author smart
 *
 */
public class NeteaseCommandService extends NewsSiteCommandService {

	private int maxPageCount = 100;

	public NeteaseCommandService() {

		this.CommandName = "com.smart.spider.netease.news";
		this.Description = "网易-股票,新股,财经";
		this.Author = "smart";

	}

	@Override
	public void Init(String[] args) {
	}

	@Override
	public void Exec(String[] args) {

		if (args != null && args.length > 1) {

			try {
				maxPageCount = Integer.parseInt(args[1]);
			} catch (Exception e) {
				logger.error(e);
			}

		}

		try {

			AnalyseNeteaseFactory analyse = new AnalyseNeteaseFactory(this.dataProvider);

			StringBuffer url = new StringBuffer();

			url.append("00251LR5/cpznList,");// 网易-股票-大盘
			url.append("00251LJV/hyyj,");// 网易-股票-行业
			url.append("00251LR5/gptj,");// 网易-股票-个股

			url.append("00251LR5/scyp,");// 网易-股票-机构策略
			url.append("00251LJV/hyyj,");// 网易-股票-行业研究
			url.append("00251LR5/gsdy,");// 网易-股票-公司研报

			url.append("00252032/xingugongsi,");// 网易-新股-最新资讯

			url.append("00252G50/macro,");// 网易-宏观
			url.append("002534M5/review");// 网易-评论

			analyse.getHtmlText(url.toString(), maxPageCount, Description, this.CommandName);

		} catch (Exception e) {
			logger.error(e);
		}
	}
}
