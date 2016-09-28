package com.smart.spider.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * properties工具类
 * 
 * @author smart
 *
 */
public class PropertiesUtil {

	/**
	 * 获得相应路径的Properties文件
	 * 
	 * @param path
	 * @return
	 */
	public static Properties getProperties(String path) {

		InputStream in = null;

		Properties prop = new Properties();

		try {

			in = PropertiesUtil.class.getResourceAsStream(path);

			if (in == null) {
				return prop;
			}

			prop.load(in);

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (in != null) {

					in.close();

					in = null;
				}

			} catch (IOException e) {

				e.printStackTrace();

			}
		}

		return prop;
	}

	/**
	 * 获得相应路径Properties文件中的内容
	 * 
	 * @param key
	 * @param path
	 * @return
	 */
	public static String getValue(String key, String path) {

		return getProperties(path).getProperty(key);

	}
}
