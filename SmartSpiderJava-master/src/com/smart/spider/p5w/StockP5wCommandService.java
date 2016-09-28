package com.smart.spider.p5w;

import com.smart.spider.util.NewsSiteCommandService;

/**
 * 
 * 全景网新闻抓取
 * 
 * @author smart
 *
 */
public class StockP5wCommandService extends NewsSiteCommandService {

	private int maxPageCount = 100;

	public StockP5wCommandService() {

		this.CommandName = "com.smart.spider.p5w.stock";
		this.Description = "全景网_股票频道";
		this.Author = "田志朋";

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

				e.printStackTrace();

			}
		}

		try {

			AnalyseP5wFactory analyse = new AnalyseP5wFactory(this.dataProvider);

			StringBuffer url = new StringBuffer();

			url.append("stock/news/zqyw,"); // 股票频道_证券要闻

			url.append("stock/market/dpfx,");// 股票频道_市场_大盘分析
			url.append("stock/market/ggjj,");// 股票频道_市场_个股聚焦
			url.append("stock/market/gng,"); // 股票频道_市场_题材概念股

			url.append("stock/news/gsxw,"); // 股票频道_公司新闻
			url.append("stock/gpyb/hyfx,"); // 股票频道_研报_行业研报
			url.append("stock/gpyb/ggjj,"); // 股票频道_研报_行个股研究

			url.append("stock/gpyb/hgyj,"); // 股票频道_研报_策略研究
			url.append("stock/news/newstock,"); // 股票频道_新股新闻
			url.append("stock/xingu/dingjia"); // 股票频道_新股定价

			analyse.exec(url.toString(), maxPageCount, Description, CommandName);

		} catch (Exception e) {

			logger.error(e);

		}
	}

}
