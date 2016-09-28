package com.smart.spider.data.meta;

import com.smart.spider.util.DateUtil;

/**
 * 内容抽象类定义
 * 
 * @author smart
 *
 */
public abstract class BaseContent {

	public abstract boolean Verify();

	public abstract void Trim();

	public long FormatToTimeStamp(long time) {

		return DateUtil.FormatToTimeStamp(time);

	}

	public String FormatToDateTime(long time) {

		return DateUtil.FormatToDateTime(time);

	}

	/**
	 * 
	 * 自动添加换行BR标签
	 * 
	 * @param context
	 * @return
	 */
	public String ClearNoise(String context) {

		if (null == context || context.isEmpty()) {

			return "";

		}

		context = context.replaceAll("\\s{1}\\s*", "<br/>");

		return context;
	}

}
