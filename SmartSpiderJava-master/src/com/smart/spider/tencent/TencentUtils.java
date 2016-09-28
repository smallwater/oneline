package com.smart.spider.tencent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class TencentUtils {

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
			bufferedReader = new BufferedReader(new InputStreamReader(urlStream, "gbk"));
			String line = " ";
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
	public String rollconnect(String spliderUrl) {

		StringBuffer buffer = new StringBuffer(); // 用来拼接参数
		StringBuffer result = new StringBuffer(); // 用来接受返回值
		URL httpUrl = null; // HTTP URL类 用这个类来创建连接
		URLConnection connection = null; // 创建的http连接
		BufferedReader bufferedReader = null; // 接受连接受的参数
		String line = null;
		try {
			// 创建URL
			httpUrl = new URL(spliderUrl);
			// 建立连接
			connection = httpUrl.openConnection();
			connection.setRequestProperty("Accept", "*/*");
			connection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
			connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("Cookie",
					"qq_slist_autoplay=on; pgv_info=ssid=s3549798125; ts_last=finance.qq.com/; ts_refer=www.qq.com/; pgv_pvid=4703297557; ts_uid=5674419712; ptag=www_qq_com|/; roll_mod=1");
			connection.setRequestProperty("Host", "roll.finance.qq.com");
			connection.setRequestProperty("Referer", "http://roll.finance.qq.com/");
			// connection.setRequestProperty("Content-Type", "text/html;
			// charset=gbk");
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");

			connection.connect();

			// 接受连接返回参数
			bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "gbk"));

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

	public String rollGetRequest(String url) {

		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url);

		// 设置请求和传输超时时间
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();// 设置请求和传输超时时间
		httpGet.setConfig(requestConfig);

		CloseableHttpResponse response = null;

		try {

			httpGet.addHeader("Accept", "*/*");
			httpGet.addHeader("Accept-Encoding", "gzip,deflate,sdch");
			httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.addHeader("Connection", "keep-alive");
			httpGet.addHeader("Cookie",
					"qq_slist_autoplay=on; pgv_info=ssid=s3549798125; ts_last=finance.qq.com/; ts_refer=www.qq.com/; pgv_pvid=4703297557; ts_uid=5674419712; ptag=www_qq_com|/; roll_mod=1");
			httpGet.addHeader("Host", "roll.finance.qq.com");
			httpGet.addHeader("Referer", "http://roll.finance.qq.com/");
			httpGet.addHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");

			response = httpClient.execute(httpGet);

			HttpEntity entity = response.getEntity();

			if (entity != null) {

				return EntityUtils.toString(entity, "gbk");

			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			// close response
			try {
				if (response != null) {
					response.close();
					response = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			// close http
			try {
				if (httpClient != null) {
					httpClient.close();
					httpClient = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return "";
	}

	public String yanbaoGetRequest(String url) {

		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url);

		// 设置请求和传输超时时间
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();// 设置请求和传输超时时间
		httpGet.setConfig(requestConfig);

		CloseableHttpResponse response = null;
		BufferedReader bufferedReader = null; // 接受连接受的参数
		try {

			httpGet.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			httpGet.addHeader("Accept-Encoding", "gzip,deflate,sdch");
			httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.addHeader("Connection", "keep-alive");
			httpGet.addHeader("Cookie",
					"ts_refer=stock.qq.com/a/20151111/010004.htm; ts_uid=5674419712; pgv_info=ssid=s5737838372; pgv_pvid=4703297557");
			httpGet.addHeader("Host", "message.finance.qq.com");
			httpGet.addHeader("Referer", "http://stockhtm.finance.qq.com/report/others/result.html");
			httpGet.addHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");

			response = httpClient.execute(httpGet);

			HttpEntity entity = response.getEntity();

			if (entity != null) {

				return EntityUtils.toString(entity, "gbk");

			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			// close response
			try {
				if (response != null) {
					response.close();
					response = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			// close http
			try {
				if (httpClient != null) {
					httpClient.close();
					httpClient = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return "";
	}

	public static TencentUtils GetInstance() {

		return new TencentUtils();

	}

}
