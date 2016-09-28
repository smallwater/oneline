package com.smart.spider.soho;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.zip.GZIPInputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.smart.spider.http.HttpClientManage;

/**
 * 爬虫工具类
 * 
 * @author smart
 * 
 */
public class SohuUtils {

	/**
	 * 判断是否存在下一页
	 * 
	 * @param spliderUrl
	 * @return
	 */
	public boolean spliderUrlisExist(String spliderUrl) {
		try {
			Document doc = Jsoup.connect(spliderUrl).timeout(1000).get();
			if (doc.getElementsByClass("list_009").select("li").size() <= 0) {
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 模拟浏览器链接url获取doc
	 * 
	 * @param spliderUrl
	 * @return
	 */
	public Document connect(String spliderUrl) {
		Document doc = null;
		try {
			doc = Jsoup.connect(spliderUrl)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31")
					.timeout(3000).get();
		} catch (Exception e) {
		}
		return doc;
	}

	/**
	 * 模拟浏览器链接url获取doc
	 * 
	 * @param spliderUrl
	 * @return
	 */
	public String rollconnect(String spliderUrl) {

		StringBuffer buffer = new StringBuffer(); // 用来拼接参数
		StringBuffer result = new StringBuffer(); // 用来接受返回值
		URL httpUrl = null; // HTTP URL类 用这个类来创建连接
		URLConnection connection = null; // 创建的http连接
		BufferedReader bufferedReader = null; // 接受连接受的参数

		try {
			// 创建URL
			httpUrl = new URL(spliderUrl);
			// 建立连接
			connection = httpUrl.openConnection();
			connection.setRequestProperty("Accept", "*/*");
			connection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
			connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("Cookie", "roll_mod=1");
			connection.setRequestProperty("Host", "roll.finance.qq.com");
			connection.setRequestProperty("Referer", "http://roll.finance.qq.com/");
			connection.setRequestProperty("Content-Type", "text/html; charset=gbk");
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");

			connection.connect();
			// 接受连接返回参数
			bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "gbk"));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				result.append(line);
			}
			bufferedReader.close();
			return (result.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return (result.toString());
	}

	/**
	 * 模拟浏览器链接url获取doc
	 * 
	 * @param spliderUrl
	 * @return
	 */
	public String httpconnect(String spliderUrl) {

		StringBuffer buffer = new StringBuffer(); // 用来拼接参数
		StringBuffer result = new StringBuffer(); // 用来接受返回值
		URL httpUrl = null; // HTTP URL类 用这个类来创建连接
		URLConnection connection = null; // 创建的http连接
		BufferedReader bufferedReader = null; // 接受连接受的参数

		try {
			// 创建URL
			httpUrl = new URL(spliderUrl);
			// 建立连接
			connection = httpUrl.openConnection();
			connection.setRequestProperty("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			connection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
			connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("Cookie",
					"ts_refer=stock.qq.com/a/20151111/010004.htm; ts_uid=5674419712; pgv_info=ssid=s5737838372; pgv_pvid=4703297557");
			connection.setRequestProperty("Host", "message.finance.qq.com");
			connection.setRequestProperty("Referer", "http://stockhtm.finance.qq.com/report/others/result.html");
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");

			connection.connect();

			// 网页内容解压
			InputStream urlStream = new GZIPInputStream(connection.getInputStream());

			// 接受连接返回参数
			bufferedReader = new BufferedReader(new InputStreamReader(urlStream, "GBK"));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				result.append(line);
			}
			bufferedReader.close();
			return (result.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return (result.toString());
	}

	/**
	 * 获取和讯网每个子版块的页数
	 * 
	 * @param doc
	 * @return
	 */
	public int getPageNumsDoc(Document doc) {
		int endlength = doc.select("div.mainboxcontent").select("div").get(1).select("div.listdh").select("script")
				.html().indexOf(";");
		String a = doc.select("div.mainboxcontent").select("div").get(1).select("div.listdh").select("script").html();
		return Integer.parseInt(a.substring(16 + "hxPage.maxPage".length(), endlength));// 获取行数
	}

	public String getOtherDate(int i) {
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, i);// 把日期往后增加一天.整数往后推,负数往前移动
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date);
	}

	public String getYear() {
		return new SimpleDateFormat("yyyy").format(new Date());
	}

	// 搜狐要闻
	public int SohuLastPageNums(String url) {

		int page = 0;

		Document doc = HttpClientManage.GetInstance().GetDocument(url);

		Elements mixMes = doc.select("div.main.area").select("script");

		int maxPageIndex = mixMes.get(0).toString().indexOf("maxPage");

		return Integer
				.parseInt(mixMes.get(0).toString().substring(maxPageIndex + 9, maxPageIndex + 18).split(";")[0].trim());

	}

	public void WriteText(String strings) {
		File file = new File("D:\\temp\\sinaSplider" + File.separator + "SOHU.csv");
		FileWriter fw = null;
		BufferedWriter writer = null;
		try {
			fw = new FileWriter(file, true);
			writer = new BufferedWriter(fw);
			writer.write(strings);
			writer.newLine();
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static SohuUtils GetInstance() {

		return new SohuUtils();

	}
}
