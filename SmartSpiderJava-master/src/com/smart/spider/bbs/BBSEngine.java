package com.smart.spider.bbs;

import com.smart.spider.boot.CommandService;
import com.smart.spider.data.DataProvider;

/**
 * 
 * 论坛抓取引擎
 * 
 * @author smart
 * 
 */
public abstract class BBSEngine extends CommandService {

	/**
	 * 数据发布组件
	 */
	public DataProvider dataProvider = null;

	/**
	 * 抓取引擎状态指标
	 */
	public BBSEngineStatus statusIndex = null;
	
	
	public BBSEngine() {
		
		statusIndex = new BBSEngineStatus();
		
	}
	
	

}
