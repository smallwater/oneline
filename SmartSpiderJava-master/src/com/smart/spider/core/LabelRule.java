package com.smart.spider.core;

public class LabelRule extends ExtractRule {

	@Override
	public Content exec(Content content) {

		
		content.text = content.text.replace("</p>", "").replace("<p>", "")
				.replace("</strong>", "").replace("</span>", "")
				.replace("<span>", "").replace("&gt", "")
				.replace("<strong>", "")
				.replace("<spanstyle=\"color:#231f1f;\">", "").replace(" ", "")
				.trim();
		
		return content;
	}

}
