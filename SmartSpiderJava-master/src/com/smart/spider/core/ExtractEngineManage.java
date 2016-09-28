package com.smart.spider.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.smart.spider.http.HttpClientManage;
import com.thoughtworks.xstream.XStream;

/**
 * 内容提取引擎管理器
 * 
 * @author smart
 * 
 */
public final class ExtractEngineManage {

	/**
	 * 内容提取引擎列表
	 */
	private List<ExtractEngine> _extractEngineList = new ArrayList<ExtractEngine>();

	/**
	 * 
	 * 初始化内容提取引擎
	 * 
	 * @param xml
	 * @return
	 */
	public final boolean Init(String xml) {

		XStream xStream = new XStream();

		@SuppressWarnings("unchecked")
		List<ExtractEngine> fromXML = (List<ExtractEngine>) xStream.fromXML(xml);
		List<ExtractEngine> engineList = fromXML;

		if (engineList == null)
			return false;

		if (engineList.size() > 0) {
			_extractEngineList = engineList;
		}

		return true;
	}

	/**
	 * 
	 * 内容提取
	 * 
	 * @param url
	 * @param content
	 * @return
	 */
	public final List<FieldResult> exec(String url, Content content) {

		List<FieldResult> result = new ArrayList<FieldResult>();

		ExtractEngine extractEngine = getExtractEngine(url);

		if (extractEngine != null) {

			result = extractEngine.exec(content);

		}

		return result;

	}

	public final List<FieldResult> exec(String url) {

		Content content = new Content();
		
		content.text = HttpClientManage.GetInstance().GetRequest(url);
		
		return exec(url, content);
	}

	/**
	 * 根据规则获取内容提取引擎实例
	 * 
	 * @param url
	 * @return
	 */
	private ExtractEngine getExtractEngine(String url) {

		for (ExtractEngine engine : _extractEngineList) {

			Pattern compile = Pattern.compile(engine.Filter, Pattern.CASE_INSENSITIVE);

			Matcher matcher = compile.matcher(url);

			boolean matchResult = matcher.matches();

			if (matchResult) {
				return engine;
			}
		}

		return null;
	}

}
