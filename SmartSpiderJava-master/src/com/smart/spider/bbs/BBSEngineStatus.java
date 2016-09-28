package com.smart.spider.bbs;

import org.apache.log4j.Logger;

import com.smart.spider.util.StatusIndex;

/**
 * 
 * 论坛抓取引擎状态统计信息
 * 
 * @author smart
 *
 */
public class BBSEngineStatus {

	/**
	 * 论坛主题指标
	 */
	public StatusIndex Topic = null;

	/**
	 * 论坛用户指标
	 */
	public StatusIndex User = null;

	/**
	 * 论坛主贴指标
	 */
	public StatusIndex Article = null;

	/**
	 * 论坛评论指标
	 */
	public StatusIndex Comment = null;

	/**
	 * 构造函数
	 */
	public BBSEngineStatus() {

		Topic = new StatusIndex("论坛主题指标");
		User = new StatusIndex("论坛用户指标");
		Article = new StatusIndex("论坛主贴指标");
		Comment = new StatusIndex("论坛评论指标");

	}

	public void Print() {

		/**
		 * 指标名称,成功总数,失败总数,成功率
		 */

		System.out.println(Topic);
		
		System.out.println(Article);
		
		System.out.println(Comment);
		
		System.out.println(User);

	}
	
	public void Print(Logger logger){
		
		logger.info(Topic);
		
		logger.info(Article);
		
		logger.info(Comment);
		
		logger.info(User);
		
		this.Print();
		
	}

}
