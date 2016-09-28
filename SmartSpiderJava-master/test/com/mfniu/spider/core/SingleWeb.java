package com.mfniu.spider.core;

/***
 * 简单的爬取单个网页内容，没有添加过滤及复杂工具
 */

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class SingleWeb {

	public static void main(String[] args) {

		// String charset = "utf-8"; //网页编码
		String charset = "gbk"; // 网页编码

		// String url_str =
		// "http://blog.sina.com.cn/s/blog_66dd17cd0102vzx6.html";
		// String url_str =
		// "http://finance.sina.com.cn/stock/jsy/20151104/060923670447.shtml";
		// String url_str =
		// "http://finance.sina.com.cn/stock/jsy/20151104/061023670459.shtml";
		// String url_str =
		// "http://finance.sina.com.cn/stock/jsy/20151104/061123670469.shtml";
		String url_str = "http://finance.sina.com.cn/stock/jsy/20151104/085223672211.shtml";

		// 文件名的获取
		String s = new String(url_str);

		String a[] = s.split("/");

		String filepath = "F:/xinlang/caijing/" + a[a.length - 1];

		URL url = null;

		try {
			url = new URL(url_str);

			int sec_cont = 1000;

			URLConnection url_con = url.openConnection();
			url_con.setDoOutput(true);
			url_con.setReadTimeout(10 * sec_cont);
			url_con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
			InputStream htm_in = url_con.getInputStream();

			String htm_str = InputStream2String(htm_in, charset);

			saveHtml(filepath, htm_str, charset);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method: saveHtml Description: save String to file
	 * 
	 * @param filepath
	 *            file path which need to be saved
	 * @param str
	 *            string saved
	 */
	public static void saveHtml(String filepath, String str, String charset) {

		try {
			/*
			 * @SuppressWarnings("resource") FileWriter fw = new
			 * FileWriter(filepath); fw.write(str); fw.flush();
			 */
			OutputStreamWriter outs = new OutputStreamWriter(new FileOutputStream(filepath, true), charset);
			outs.write(str);
			System.out.print(str);
			outs.close();
		} catch (IOException e) {
			System.out.println("Error at save html...");
			e.printStackTrace();
		}
	}

	/**
	 * Method: InputStream2String Description: make InputStream to String
	 * 
	 * @param in_st
	 *            inputstream which need to be converted
	 * @param charset
	 *            encoder of value
	 * @throws IOException
	 *             if an error occurred
	 */
	public static String InputStream2String(InputStream in_st, String charset) throws IOException {
		BufferedReader buff = new BufferedReader(new InputStreamReader(in_st, charset));
		StringBuffer res = new StringBuffer();
		String line = "";
		while ((line = buff.readLine()) != null) {
			res.append(line);
		}
		return res.toString();
	}

}