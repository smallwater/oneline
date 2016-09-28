package com.mfniu.spider.bbs.guba;

import org.junit.Test;

import com.smart.spider.bbs.guba.CategoryList;
import com.smart.spider.bbs.guba.TopicPageInfo;

public class GuBaTopicTest {

	@Test
	public void testInit() {

		CategoryList topic = new CategoryList();

		topic.Init();

		System.out.println(topic.DataList.size());
		
		for (TopicPageInfo m : topic.DataList) {
			System.out.println(m.Url + "," + m.Title + "," + m.Category);
		}

		System.out.println("-----");

	}

}
