package com.smart.spider.bbs.guba;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.smart.spider.bbs.PageInfo;
import com.smart.spider.http.HttpClientManage;

public class CommentPageInfo extends PageInfo {

	@Override
	public void InitPageInfo() {

		String htmlContent = HttpClientManage.GetInstance().GetRequest(Url);

		InitPageInfo(htmlContent);

	}

	public void InitPageInfo(String htmlContent) {

		Document document = Jsoup.parse(htmlContent);

		InitPageInfo(document);
	}

	public void InitPageInfo(Document document) {

		Element pageElement = document.getElementById("newspage");

		if (pageElement != null) {

			// news,cjpl,211885571_|424|30|1
			String dataPager = pageElement.attr("data-page");

			if (StringUtil.isBlank(dataPager)) {
				return;
			}

			String[] dataPagerArray = dataPager.split("\\|");

			if (dataPagerArray.length < 2) {
				return;
			}

			SizeCount = Integer.parseInt(dataPagerArray[1]);
			PageSize = Integer.parseInt(dataPagerArray[2]);
			End = SizeCount % PageSize == 0 ? SizeCount / PageSize : SizeCount / PageSize + 1;
			Start = 1;
		}
	}

}
