package com.mfniu.spider.bbs.guba;

import org.junit.Test;

import com.smart.spider.bbs.BBSEngine;
import com.smart.spider.bbs.guba.GuBaBBSEngine;

public class GuBaBBSEngineTest {

	@Test
	public void testInitCategory() {
		BBSEngine engine = new GuBaBBSEngine();

		engine.Init(null);

		engine.Exec(null);

		System.out.println("----");
	}

}
