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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class XLFWeb {

	private static String homeurl = "http://roll.finance.sina.com.cn/finance/zq1/gsjsy/";// 爬取的url
	private static int page = 1;// 爬取页数

	public static void main(String[] args) {

		try {

			String spliderUrl = "";

			for (int i = 1; i <= page; i++) {
				spliderUrl = homeurl + "index_" + i + ".shtml";
				Document doc = Jsoup.connect(spliderUrl).get();
				Elements el = doc.getElementsByClass("list_009");
				Elements les = el.select("li");
				for (int j = 0; j < 1; j++) {
				}
			}

		} catch (Exception e) {

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