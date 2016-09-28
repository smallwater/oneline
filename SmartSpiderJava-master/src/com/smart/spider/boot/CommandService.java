package com.smart.spider.boot;

/**
 * 
 * 命令服务接口
 * 
 * @author smart
 *
 */
public abstract class CommandService {

	/**
	 * 命令名称
	 */
	public String CommandName = "";

	/**
	 * 引擎描述
	 */
	public String Description = "";

	/**
	 * 作者
	 */
	public String Author = "";

	/**
	 * 命令初始化
	 */
	public abstract void Init(String[] args);

	/**
	 * 命令执行入口
	 */
	public abstract void Exec(String[] args);
}
