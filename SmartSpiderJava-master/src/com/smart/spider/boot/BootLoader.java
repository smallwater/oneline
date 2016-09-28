package com.smart.spider.boot;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.smart.spider.bbs.guba.GuBaBBSEngine;
import com.smart.spider.cnfol.CnfolFinanceCommandService;
import com.smart.spider.cnfol.CnfolStockCommandService;
import com.smart.spider.eastmoney.eastmoneyCommandService;
import com.smart.spider.hexun.HeXunRollCommandService;
import com.smart.spider.hexun.HexunStockCommandService;
import com.smart.spider.hexun.HexunXinGuCommandService;
import com.smart.spider.hexun.HexunYanBaoCommandService;
import com.smart.spider.ifeng.IfengMacroscopicService;
import com.smart.spider.ifeng.IfengStockJshqService;
import com.smart.spider.ifeng.IfengStockQtscService;
import com.smart.spider.ifeng.IfengStockReportService;
import com.smart.spider.ifeng.IfengStockSsgsService;
import com.smart.spider.ifeng.IfengStockXgplService;
import com.smart.spider.ifeng.IfengStockXgywService;
import com.smart.spider.ifeng.IfengStockYbdwService;
import com.smart.spider.ifeng.IfengStockZqywService;
import com.smart.spider.jrj.BaoGaoJrjCommandService;
import com.smart.spider.jrj.FinanceJrjCommandService;
import com.smart.spider.jrj.YanBaoJrjCommandService;
import com.smart.spider.netease.NeteaseCommandService;
import com.smart.spider.p5w.FinanceP5wCommandService;
import com.smart.spider.p5w.SentimentP5wCommandService;
import com.smart.spider.p5w.StockP5wCommandService;
import com.smart.spider.sina.SinaFinanceCommandService;
import com.smart.spider.sina.SinaFinanceDaPanCommandService;
import com.smart.spider.sina.SinaFinanceGeGuCommandService;
import com.smart.spider.sohu.SohuStockCommandService;
import com.smart.spider.tencent.FinanceCommandService;
import com.smart.spider.tencent.FinanceGunDongCommandService;
import com.smart.spider.tencent.StockCommandService;
import com.smart.spider.tencent.StockXinGuCommandService;
import com.smart.spider.tencent.StockYanBaoCommandService;

/**
 * 
 * 入口引导程序
 * 
 */
public class BootLoader {

	private final Logger logger = LogManager.getLogger(BootLoader.class);

	/**
	 * 服务命令列表集合
	 */
	private List<CommandService> commandServiceList = new ArrayList<CommandService>();

	/**
	 * 入口函数
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		BootLoader bootLoader = new BootLoader();

		bootLoader.initCommand();

		if (null == args || args.length == 0) {

			bootLoader.printCommand();

			return;
		}

		bootLoader.ExecCommand(args);

		bootLoader.printCommand();

	}

	private void ExecCommand(String[] args) {

		String cmd = args[0];

		for (CommandService commandService : commandServiceList) {

			if (cmd.equalsIgnoreCase(commandService.CommandName)) {

				try {

					commandService.Init(args);

					commandService.Exec(args);

					logger.info("恭喜你:" + commandService.CommandName + "命令执行完毕");
					// System.gc();

					Runtime.getRuntime().gc();

					// 相同命令执行第一个
					break;

				} catch (Exception e) {

					logger.error(e);
					logger.error(commandService.CommandName + " 命令发生异常");

				}
			}
		}
	}

	private void initCommand() {

		try {

			// 东方财富网股吧
			commandServiceList.add(new GuBaBBSEngine());
			// 网址内容提取规则引擎
			// commandServiceList.add(new UrlExtractEngine());

			// 东方财富新闻 
			commandServiceList.add(new eastmoneyCommandService());
			// 新浪
			commandServiceList.add(new SinaFinanceCommandService());
			// 和讯 
			commandServiceList.add(new HexunStockCommandService());
			// 中金在线 
			commandServiceList.add(new CnfolFinanceCommandService());
			commandServiceList.add(new CnfolStockCommandService());
			// 搜狐 
			commandServiceList.add(new SohuStockCommandService());

			// 和讯
			commandServiceList.add(new HexunXinGuCommandService());
			commandServiceList.add(new HexunYanBaoCommandService());
			// 新浪
			commandServiceList.add(new SinaFinanceGeGuCommandService());
			commandServiceList.add(new SinaFinanceDaPanCommandService());
			// 腾讯新闻
			commandServiceList.add(new FinanceGunDongCommandService());
			commandServiceList.add(new StockCommandService());
			commandServiceList.add(new StockXinGuCommandService());
			commandServiceList.add(new StockYanBaoCommandService());
			commandServiceList.add(new FinanceCommandService());

			
			// 全景网 
			commandServiceList.add(new FinanceP5wCommandService());
			commandServiceList.add(new StockP5wCommandService());
			commandServiceList.add(new SentimentP5wCommandService());
			//和讯滚动  
			commandServiceList.add(new HeXunRollCommandService());
			//金融界
			commandServiceList.add(new FinanceJrjCommandService());
			commandServiceList.add(new YanBaoJrjCommandService());
			commandServiceList.add(new BaoGaoJrjCommandService());

			// 网易
			commandServiceList.add(new NeteaseCommandService());

			// 凤凰网
			commandServiceList.add(new IfengMacroscopicService());
			commandServiceList.add(new IfengStockJshqService());
			commandServiceList.add(new IfengStockQtscService());
			commandServiceList.add(new IfengStockReportService());
			commandServiceList.add(new IfengStockSsgsService());
			commandServiceList.add(new IfengStockXgplService());
			commandServiceList.add(new IfengStockXgywService());
			commandServiceList.add(new IfengStockYbdwService());
			commandServiceList.add(new IfengStockZqywService());
			
		} catch (Exception e) {

			logger.error(e);

		}

	}

	private void printCommand() {

		System.out.println();
		System.out.println("舆情爬虫引擎:");

		for (CommandService commandService : commandServiceList) {

			String cmd = "命令:" + commandService.CommandName + "\t描述:" + commandService.Description + "\t作者:"
					+ commandService.Author;

			System.out.println(cmd);
		}

		System.out.println();
		System.out.println();

	}

}
