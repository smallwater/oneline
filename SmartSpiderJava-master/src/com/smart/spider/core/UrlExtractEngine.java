package com.smart.spider.core;

import com.smart.spider.boot.CommandService;

public class UrlExtractEngine extends CommandService {

	private ExtractEngineManage extractEngineManage = new ExtractEngineManage();

	public UrlExtractEngine() {

		this.CommandName = "com.smart.spider.url";

		this.Description = "网址内容提取规则引擎";
		
		Author = "smart";

	}

	@Override
	public void Init(String[] args) {

	}

	/**
	 * 
	 * .jar com.mfniu.spider.url xml url
	 * 
	 */
	@Override
	public void Exec(String[] args) {

		try {

			String filename = args[1];

			String url = args[2];

			String xmlFileContent = this.GetXmlFileContent(filename);

			// 初始化引擎
			extractEngineManage.Init(xmlFileContent);

			// 执行引擎
			extractEngineManage.exec(url);

			// 处理解析内容结果
			// 发送到kafka

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	private String GetXmlFileContent(String filename) {

		return "";

	}

}
