package com.smart.spider.jrj;

import com.smart.spider.util.NewsSiteCommandService;

/**
 * 
 * 全景网新闻抓取
 * 
 * @author smart
 *
 */
public class FinanceJrjCommandService extends NewsSiteCommandService {

	private int maxPageCount = 100;

	public FinanceJrjCommandService() {

		this.CommandName = "com.smart.spider.jrj.finance";
		this.Description = "金融界_财经频道_股票";
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

			AnalyseJrjFactory analyse = new AnalyseJrjFactory(this.dataProvider);

			analyse.exec(maxPageCount, Description, this.CommandName);

		} catch (Exception e) {
			logger.error(e);
		}
	}

}
