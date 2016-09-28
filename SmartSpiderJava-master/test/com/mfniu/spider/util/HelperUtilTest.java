package com.mfniu.spider.util;

import org.junit.Test;

import com.smart.spider.http.HttpClientManage;
import com.smart.spider.util.DateUtil;
import com.smart.spider.util.FileUtil;
import com.smart.spider.util.HelperUtil;

public class HelperUtilTest {

	@Test
	public void testToMd5() {

		// 37daa4ddd322e8d06e17f31cc147e460
		// 37daa4ddd322e8d06e17f31cc147e460

		String md5 = HelperUtil.ToMd5("87927395");

		System.out.println(md5);

	}

	@Test
	public void testTimeStamp() {

		System.out.println(DateUtil.GetDateTime());

		// 1447741196513
		// 1447741196.513 后三位是毫秒
		System.out.println(System.currentTimeMillis() / 1000);

	}

	@Test
	public void testEncoding() {

		/*
		 * SortedMap<String, Charset> map = Charset.availableCharsets();
		 * Set<String> set = map.keySet(); Iterator<String> ite =
		 * set.iterator(); while (ite.hasNext()) { String key = ite.next();
		 * Charset value = map.get(key); System.out.println(key); GB2312 GBK }
		 */

		String content = HttpClientManage.GetInstance().GetRequest("http://yanbao.stock.hexun.com/dzqt655570.shtml",
				"GBK");

		FileUtil.AppendText("0.txt", content, "GBK");

		System.out.println(content);

	}

}
