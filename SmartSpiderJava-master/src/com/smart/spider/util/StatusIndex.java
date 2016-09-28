package com.smart.spider.util;

import java.text.DecimalFormat;

/**
 * 
 * 引擎状态指标
 * 
 * @author smart
 *
 */
public class StatusIndex {

	/**
	 * 指标名称
	 */
	public String IndexName = "";

	/**
	 * 总数
	 */
	public long TotalCount = 0;

	/**
	 * 成功总数
	 */
	public long SuccessCount = 0;

	/**
	 * 失败总数
	 */
	public long ErrorCount = 0;

	public StatusIndex(String indexName) {

		this.IndexName = indexName;

	}

	/**
	 * 成功率
	 */
	public String GetSuccessRate() {

		long rate = 0;

		if (TotalCount != 0) {
			rate = SuccessCount / TotalCount;
		}

		DecimalFormat decimalFormat = new DecimalFormat("###.###%");

		return decimalFormat.format(rate);

	}

	@Override
	public String toString() {

		return IndexName + "," + SuccessCount + "/" + TotalCount + "," + ErrorCount + "," + GetSuccessRate();
	}

}
