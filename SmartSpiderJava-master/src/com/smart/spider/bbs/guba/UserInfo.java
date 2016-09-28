package com.smart.spider.bbs.guba;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 东方财富股吧用户信息
 * 
 * @author smart
 * 
 *         examle:
 * 
 *         { "Id": "6817094369357878", "Url":
 *         "http://iguba.eastmoney.com/6817094369357878", "UserName": "财联社app",
 *         "Summary": "证券资讯服务供应商", "RegTime": "3", "Force": 5, "TotalAccess":
 *         447, "TodayAccess": 1, "Fans": 35, "Focus": 3, "StockCount": 3 }
 *
 */
public class UserInfo {

	/**
	 * 用户ID
	 */
	public String Id;

	/**
	 * Url
	 */
	public String Url;

	/**
	 * 用户昵称
	 */
	public String UserName;

	/**
	 * 用户简介
	 */
	public String Summary;

	/**
	 * 注册时间
	 */
	public String RegTime;

	/**
	 * 影响力
	 */
	public int Force;

	/**
	 * 总访问
	 */
	public int TotalAccess;

	/**
	 * 今日访问
	 */
	public int TodayAccess;

	/**
	 * 粉丝总数
	 */
	public int Fans;

	/**
	 * 关注总数
	 */
	public int Focus;

	/**
	 * 自选股
	 */
	public int StockCount;

	public UserInfo() {
		Id = "";
		Url = "";
		UserName = "";
		Summary = "";
		RegTime = "";
		Force = 0;
		TotalAccess = 0;
		TodayAccess = 0;
		Fans = 0;
		Focus = 0;
		StockCount = 0;
	}
}
