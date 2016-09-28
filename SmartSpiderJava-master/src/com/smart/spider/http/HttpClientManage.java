package com.smart.spider.http;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public final class HttpClientManage {

	public String Encoding = "utf-8";
	public String UserAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.89 Safari/537.36";
	public String ContentType = "text/html;";
	public String Accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
	public String Referer = "http://guba.eastmoney.com/remenba.aspx";
	public String AcceptLanguage = "zh-CN,zh;q=0.8,en;q=0.6";
	public int SocketTimeOut = 5000;
	public int ConnectTimeOut = 10000;

	public String GetRequest(String url, String encoding) {

		Encoding = encoding;

		return this.GetRequest(url);
	}

	public String GetRequest(String url, int socketTimeout) {

		return GetRequest(url, socketTimeout, ConnectTimeOut);

	}

	public String GetRequest(String url, int socketTimeout, int connectionTimeout) {

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
				.setConnectTimeout(connectionTimeout).build();
		httpGet.setConfig(requestConfig);

		CloseableHttpResponse response = null;

		try {

			// init header
			httpGet.addHeader("User-Agent", UserAgent);
			httpGet.addHeader("Content-Type", ContentType);
			httpGet.addHeader("Accept", Accept);
			httpGet.addHeader("Referer", Referer);
			httpGet.addHeader("Accept-Language", AcceptLanguage);

			response = httpClient.execute(httpGet);

			HttpEntity entity = response.getEntity();

			if (entity != null) {

				return EntityUtils.toString(entity, Encoding);

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

	public String GetRequest(String url) {

		return GetRequest(url, SocketTimeOut, ConnectTimeOut);
	}

	public Document GetDocument(String url, String encoding) {

		Encoding = encoding;

		return this.GetDocument(url);

	}

	public Document GetDocument(String url) {

		try {

			String htmlContent = this.GetRequest(url);

			if (null == htmlContent | htmlContent.equals("")) {

				return null;

			}

			return Jsoup.parse(htmlContent);

		} catch (Exception e) {

			e.printStackTrace();
		}

		return null;

	}

	public Document GetDocument(String url, String encoding, int socketTimeout, int connectionTimeout) {

		try {
			Encoding = encoding;
			String htmlContent = this.GetRequest(url, socketTimeout, connectionTimeout);

			if (null == htmlContent | htmlContent.equals("")) {

				return null;

			}

			return Jsoup.parse(htmlContent);

		} catch (Exception e) {

			e.printStackTrace();
		}

		return null;

	}

	public static HttpClientManage GetInstance() {

		return new HttpClientManage();

	}

}
