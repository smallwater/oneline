package com.smart.spider.util;

import org.apache.log4j.Logger;

/**
 * 
 * 命令服务运行状态指标
 * 
 * @author smart
 *
 */
public class CommandServiceStatus {

	/**
	 * 运行状态指标
	 */
	public StatusIndex Index = null;

	public CommandServiceStatus() {

		Index = new StatusIndex("运行状态指标");

	}

	public void Print() {

		/**
		 * 指标名称,成功总数,失败总数,成功率
		 */

		System.out.println(Index);

	}

	public void Print(Logger logger) {

		logger.info(Index);

		this.Print();

	}

}
