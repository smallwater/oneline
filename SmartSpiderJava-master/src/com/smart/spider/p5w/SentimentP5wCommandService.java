package com.smart.spider.p5w;

import com.smart.spider.util.NewsSiteCommandService;

/**
 * 
 * 全景网新闻抓取
 * 
 * @author smart
 *
 */
public class SentimentP5wCommandService extends NewsSiteCommandService {

	private int maxPageCount = 100;

	public SentimentP5wCommandService() {

		this.CommandName = "com.smart.spider.p5w.sentiment";
		this.Description = "全景网_舆情频道";
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
				e.printStackTrace();
			}

		}

		try {

			AnalyseP5wFactory analyse = new AnalyseP5wFactory(this.dataProvider);

			StringBuffer url = new StringBuffer();

			url.append("yuqing/kuaixun,"); // 舆情频道_舆情快讯
			url.append("yuqing/guancha,"); // 舆情频道_舆情观察
			url.append("yuqing/ipo,"); // 舆情频道_IPO舆情
			url.append("yuqing/yubg,"); // 舆情频道_舆情报告
			url.append("yuqing/yqbg,"); // 舆情频道_舆情案例

			url.append("yuqing/phb/cjxw,"); // 舆情频道_舆情榜单_财经新闻
			url.append("yuqing/phb/yqyjd,"); // 舆情频道_舆情榜单_舆情预警度
			url.append("yuqing/phb/mtgzd,"); // 舆情频道_舆情榜单_媒体关注度

			url.append("yuqing/jjyq"); // 舆情频道_基金舆情

			analyse.exec(url.toString(), maxPageCount, Description, CommandName);

		} catch (Exception e) {
			logger.error(e);
		}
	}

}
