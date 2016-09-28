package com.mfniu.spider.core;

import java.util.List;

import org.junit.Test;

import com.smart.spider.core.ComplexFieldExtractRule;
import com.smart.spider.core.Content;
import com.smart.spider.core.ExtractEngine;
import com.smart.spider.core.Field;
import com.smart.spider.core.FieldResult;
import com.smart.spider.core.SubStringExtractRule;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

public class ExtractEngineTest {

	@Test
	public void test() {

		ExtractEngine extractEngine = new ExtractEngine();

		{
			// 构造字段Title
			Field titleField = new Field();
			titleField.title = "标题";
			titleField.dataName = "Title";
			titleField.require = false;

			// 截取字符串规则
			SubStringExtractRule subStringExtractRule = new SubStringExtractRule();
			subStringExtractRule.begin = "A";
			subStringExtractRule.end = "C";
			titleField.extractRule.add(subStringExtractRule);

			extractEngine.fieldList.add(titleField);
		}

		{
			// 复合字段规则
			ComplexFieldExtractRule complexFieldExtractRule = new ComplexFieldExtractRule();

			// 构造字段Title
			Field subtitleField = new Field();
			subtitleField.title = "复合子字段1";

			// 截取字符串规则
			SubStringExtractRule sub2StringExtractRule = new SubStringExtractRule();
			sub2StringExtractRule.begin = "D";
			sub2StringExtractRule.end = "F";
			subtitleField.extractRule.add(sub2StringExtractRule);

			complexFieldExtractRule.fieldList.add(subtitleField);

			Field fuhe = new Field();
			fuhe.title = "复合字段";
			fuhe.extractRule.add(complexFieldExtractRule);

			extractEngine.fieldList.add(fuhe);
		}

		// 构造测试内容
		Content testContent = new Content();
		testContent.text = "ABCDEF";

		// 测试引擎执行结果
		extractEngine.Filter = "http://tool.oschina.net/codeformat/json";
		List<FieldResult> result = extractEngine.exec("http://tool.oschina.net/codeformat/json", testContent);

		PrintResult(result);

		// 序列化

		// json格式，正常返回
		JettisonMappedXmlDriver jsonConfig = new JettisonMappedXmlDriver();

		// json格式，部分回不来
		JsonHierarchicalStreamDriver json1 = new JsonHierarchicalStreamDriver();

		XStream xStream = new XStream(jsonConfig);
		String xml = xStream.toXML(extractEngine);

		System.out.println(xml);

		// 反序列化
		ExtractEngine newEngine = (ExtractEngine) xStream.fromXML(xml);

		result.clear();
		result = newEngine.exec(testContent);
		PrintResult(result);
	}

	public static void PrintResult(List<FieldResult> result) {

		for (FieldResult f : result) {

			System.out.println(f.toString());

		}

	}

}
