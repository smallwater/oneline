package com.smart.spider.p5w;

import com.smart.spider.util.NewsSiteCommandService;

/**
 * 
 * 全景网新闻抓取
 * 
 * @author smart
 *
 */
public class FinanceP5wCommandService extends NewsSiteCommandService {

	private int maxPageCount = 100;

	public FinanceP5wCommandService() {

		this.CommandName = "com.smart.spider.p5w.finance";
		this.Description = "全景网_财经频道";
		this.Author = "smart";

	}

	@Override
	public void Init(String[] args) {
	}

	@Override
	public void Exec(String[] args) {

		if (args != null && args.length > 1) {

			maxPageCount = Integer.parseInt(args[1]);

		}

		try {

			AnalyseP5wFactory analyse = new AnalyseP5wFactory(this.dataProvider);

			StringBuffer url = new StringBuffer();

			url.append("news/xwpl,");// 财经-评论

			url.append("news/pgt");// 财经-曝光台

			analyse.exec(url.toString(), maxPageCount, Description, CommandName);

		} catch (Exception e) {
			logger.error(e);
		}
	}

}
