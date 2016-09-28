package com.mfniu.spider.core;

/*
以下是一个Java爬虫程序，它能从指定主页开始，按照指定的深度抓取该站点域名下的网页并维护简单索引。
参数：private static int webDepth = 2;//爬虫深度。主页的深度为1，设置深度后超过该深度的网页不会抓取。
     private int intThreadNum = 10;//线程数。开启的线程数。

抓取时也会在程序源文件目录下生成一个report.txt文件记录爬虫的运行情况，并在抓取结束后生成一个fileindex.txt
文件维护网页文件索引。

本程序用到了多线程(静态变量和同步)，泛型，文件操作，URL类和连接，Hashtable类关联数组，正则表达式及其相关类。
运行时需使用命令行参数，第一个参数应使用http://开头的有效URL字符串作为爬虫的主页，第二个参数（可选）
应输入可转换为int型的字符串（用Integer.parseInt(String s)静态方法可以转换的字符串，如3）作为爬虫深度，
如果没有，则默认深度为2。

本程序的不足之处是：只考虑了href= href=' href="后加绝对url的这三种情况(由于url地址在网页源文件中情况比
较复杂，有时处理也会出现错误)，还有相对url和window.open('的情况没有考虑。异常处理程序也只是简单处理。

*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetWeb {
	private int webDepth = 2;// 爬虫深度
	private int intThreadNum = 1;// 线程数
	private String strHomePage = "";// 主页地址
	private String myDomain;// 域名
	private String fPath = "E:/Crawler/liudu/";// 储存网页文件的目录名
	private ArrayList<String> arrUrls = new ArrayList<String>();// 存储未处理URL
	private ArrayList<String> arrUrl = new ArrayList<String>();// 存储所有URL供建立索引
	private Hashtable<String, Integer> allUrls = new Hashtable<String, Integer>();// 存储所有URL的网页号
	private Hashtable<String, Integer> deepUrls = new Hashtable<String, Integer>();// 存储所有URL深度
	private int intWebIndex = 0;// 网页对应文件下标，从0开始
	private String charset = "utf-8";
	private String report = "";
	private long startTime;
	private int webSuccessed = 0;
	private int webFailed = 0;

	public GetWeb(String s) {
		this.strHomePage = s;
	}

	public GetWeb(String s, int i) {
		this.strHomePage = s;
		this.webDepth = i;
	}

	public synchronized void addWebSuccessed() {
		webSuccessed++;
	}

	public synchronized void addWebFailed() {
		webFailed++;
	}

	/***
	 * 
	 * <p>
	 * Title: addReport
	 * </p>
	 * <p>
	 * Description: 网页爬虫日志记录
	 * </p>
	 * 
	 * @param s
	 */
	public synchronized void addReport(String s) {
		try {
			report += s;
			PrintWriter pwReport = new PrintWriter(new FileOutputStream("E:/Crawler/report.txt"));
			pwReport.println(report);
			pwReport.close();
		} catch (Exception e) {
			System.out.println("生成报告文件失败!");
		}
	}

	public synchronized String getAUrl() {
		String tmpAUrl = arrUrls.get(0);
		arrUrls.remove(0);
		return tmpAUrl;
	}

	public synchronized String getUrl() {
		String tmpUrl = arrUrl.get(0);
		arrUrl.remove(0);
		return tmpUrl;
	}

	public synchronized Integer getIntWebIndex() {
		intWebIndex++;
		return intWebIndex;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * if (args.length == 0 || args[0].equals("")) { System.out.println(
		 * "No input!"); System.exit(1); } else if (args.length == 1) { GetWeb
		 * gw = new GetWeb(args[0]); gw.getWebByHomePage(); } else { GetWeb gw =
		 * new GetWeb(args[0], Integer.parseInt(args[1]));
		 * gw.getWebByHomePage(); } Scanner cin=new Scanner(System.in); String
		 * url = cin.next(); int webDepth = cin.nextInt();
		 */

		// GetWeb gw = new GetWeb("http://finance.sina.com.cn", 2);
		GetWeb gw = new GetWeb("http://roll.finance.sina.com.cn/finance/zq1/gsjsy/index.shtml", 2);
		gw.getWebByHomePage();
	}

	/***
	 * 
	 * <p>
	 * Title: getWebByHomePage
	 * </p>
	 * <p>
	 * Description: 爬虫的入口
	 * </p>
	 */
	public void getWebByHomePage() {
		startTime = System.currentTimeMillis();
		this.myDomain = getDomain();
		if (myDomain == null) {
			System.out.println("Wrong input!");
			// System.exit(1);
			return;
		}
		System.out.println("Homepage = " + strHomePage);
		addReport("Homepage = " + strHomePage + "!\n");
		System.out.println("Domain = " + myDomain);
		addReport("Domain = " + myDomain + "!\n");
		arrUrls.add(strHomePage);
		arrUrl.add(strHomePage);
		allUrls.put(strHomePage, 0);
		deepUrls.put(strHomePage, 1);
		File fDir = new File(fPath);
		if (!fDir.exists()) {
			fDir.mkdir();
		}
		System.out.println("Start!");
		this.addReport("Start!\n");
		String tmp = getAUrl();
		this.getWebByUrl(tmp, charset, allUrls.get(tmp) + "");
		int i = 0;
		for (i = 0; i < intThreadNum; i++) {
			new Thread(new Processer(this)).start();
		}
		while (true) {
			if (arrUrls.isEmpty() && Thread.activeCount() == 1) {
				long finishTime = System.currentTimeMillis();
				long costTime = finishTime - startTime;
				System.out.println("\n\n\n\n\nFinished!");
				addReport("\n\n\n\n\nFinished!\n");
				System.out.println("Start time = " + startTime + "   " + "Finish time = " + finishTime + "   "
						+ "Cost time = " + costTime + "ms");
				addReport("Start time = " + startTime + "   " + "Finish time = " + finishTime + "   " + "Cost time = "
						+ costTime + "ms" + "\n");
				System.out.println("Total url number = " + (webSuccessed + webFailed) + "   Successed: " + webSuccessed
						+ "   Failed: " + webFailed);
				addReport("Total url number = " + (webSuccessed + webFailed) + "   Successed: " + webSuccessed
						+ "   Failed: " + webFailed + "\n");

				String strIndex = "";
				String tmpUrl = "";
				while (!arrUrl.isEmpty()) {
					tmpUrl = getUrl();
					strIndex += "Web depth:" + deepUrls.get(tmpUrl) + "   Filepath: " + fPath + "/web"
							+ allUrls.get(tmpUrl) + ".htm" + "   url:" + tmpUrl + "\n\n";
				}
				System.out.println(strIndex);
				try {
					PrintWriter pwIndex = new PrintWriter(new FileOutputStream("E:/Crawler/fileindex.txt"));
					pwIndex.println(strIndex);
					pwIndex.close();
				} catch (Exception e) {
					System.out.println("生成索引文件失败!");
				}
				break;
			}
		}
	}

	/***
	 * 
	 * <p>
	 * Title: getWebByUrl
	 * </p>
	 * <p>
	 * Description: 请求网页且将网页写入到本地
	 * </p>
	 * 
	 * @param strUrl
	 * @param charset
	 * @param fileIndex
	 */
	public void getWebByUrl(String strUrl, String charset, String fileIndex) {
		try {
			// if(charset==null||"".equals(charset))charset="utf-8";
			System.out.println("Getting web by url: " + strUrl);
			addReport("Getting web by url: " + strUrl + "\n");
			URL url = new URL(strUrl);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			InputStream is = null;
			is = url.openStream();

			String filePath = fPath + "/web" + fileIndex + ".htm";
			PrintWriter pw = null;
			FileOutputStream fos = new FileOutputStream(filePath);
			OutputStreamWriter writer = new OutputStreamWriter(fos);
			pw = new PrintWriter(writer);
			BufferedReader bReader = new BufferedReader(new InputStreamReader(is));
			StringBuffer sb = new StringBuffer();
			String rLine = null;
			String tmp_rLine = null;
			while ((rLine = bReader.readLine()) != null) {
				tmp_rLine = rLine;
				int str_len = tmp_rLine.length();
				if (str_len > 0) {
					sb.append("\n" + tmp_rLine);
					pw.println(tmp_rLine);
					pw.flush();
					if (deepUrls.get(strUrl) < webDepth)
						// 调用getUrlByString方法，爬取网站
						getUrlByString(tmp_rLine, strUrl);
				}
				tmp_rLine = null;
			}
			is.close();
			pw.close();
			System.out.println("Get web successfully! " + strUrl);
			addReport("Get web successfully! " + strUrl + "\n");
			addWebSuccessed();
		} catch (Exception e) {
			System.out.println("Get web failed!       " + strUrl);
			addReport("Get web failed!       " + strUrl + "\n");
			addWebFailed();
		}
	}

	/***
	 * 
	 * <p>
	 * Title: getUrlByString
	 * </p>
	 * <p>
	 * Description: 搜寻在strUrl的inputArgs一列数据包含的链接
	 * </p>
	 * 
	 * @param inputArgs
	 * @param strUrl
	 */
	public void getUrlByString(String inputArgs, String strUrl) {
		String tmpStr = inputArgs;
		String regUrl = "(?<=(href=)[\"]?[\']?)[http://][^\\s\"\'\\?]*(" + myDomain + ")[^\\s\"\'>]*";
		Pattern p = Pattern.compile(regUrl, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(tmpStr);
		boolean blnp = m.find();
		// int i = 0;
		while (blnp == true) {
			if (!allUrls.containsKey(m.group(0))) {
				System.out.println("Find a new url,depth:" + (deepUrls.get(strUrl) + 1) + " " + m.group(0));
				addReport("Find a new url,depth:" + (deepUrls.get(strUrl) + 1) + " " + m.group(0) + "\n");
				arrUrls.add(m.group(0));
				arrUrl.add(m.group(0));
				allUrls.put(m.group(0), getIntWebIndex());
				deepUrls.put(m.group(0), (deepUrls.get(strUrl) + 1));
			}
			tmpStr = tmpStr.substring(m.end(), tmpStr.length());
			m = p.matcher(tmpStr);
			blnp = m.find();
		}
	}

	/***
	 * 
	 * <p>
	 * Title: getDomain
	 * </p>
	 * <p>
	 * Description: 验证domain是否正确，放回true或者flase
	 * </p>
	 * 
	 * @return
	 */
	public String getDomain() {
		// String reg =
		// "(?<=http\\://[a-zA-Z0-9]{0,100}[.]{0,1})[^.\\s]*?\\.(com|cn|net|org|biz|info|cc|tv)";
		String reg = "(?<=http\\://[a-zA-Z0-9]{0,100}[.]{0,1})[^.\\s]*?\\.(com|cn|net|org|biz|info|cc|tv|shtml)";
		Pattern p = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(strHomePage);
		boolean blnp = m.find();
		if (blnp == true) {
			return m.group(0);
		}
		return null;
	}

	/***
	 * 
	 * <p>
	 * Description:开启多个线程
	 * </p>
	 * 
	 * @author 余辉
	 * @date 2015年11月3日下午2:15:34
	 * @version 1.0
	 */
	class Processer implements Runnable {
		GetWeb gw;

		public Processer(GetWeb g) {
			this.gw = g;
		}

		public void run() {
			// Thread.sleep(5000);
			while (!arrUrls.isEmpty()) {
				String tmp = getAUrl();
				getWebByUrl(tmp, charset, allUrls.get(tmp) + "");
			}
		}
	}
}
