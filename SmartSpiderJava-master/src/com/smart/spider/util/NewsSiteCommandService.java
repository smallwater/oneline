package com.smart.spider.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.smart.spider.boot.CommandService;
import com.smart.spider.data.ActiveMqDataProvider;
import com.smart.spider.data.DataProvider;

public abstract class NewsSiteCommandService extends CommandService {

	protected DataProvider dataProvider = null;

	protected Logger logger = null;

	protected CommandServiceStatus Status = null;

	public NewsSiteCommandService() {

		dataProvider = new ActiveMqDataProvider();

		logger = LogManager.getLogger(NewsSiteCommandService.class);

		Status = new CommandServiceStatus();
	}

}
