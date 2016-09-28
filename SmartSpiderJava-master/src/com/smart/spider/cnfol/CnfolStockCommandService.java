package com.smart.spider.cnfol;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.smart.spider.data.meta.Article;
import com.smart.spider.data.meta.ContentType;
import com.smart.spider.data.meta.Message;
import com.smart.spider.http.HttpClientManage;
import com.smart.spider.util.DateUtil;
import com.smart.spider.util.HelperUtil;
import com.smart.spider.util.NewsSiteCommandService;

public class CnfolStockCommandService extends NewsSiteCommandService {
	
	private List<String> urlList = new ArrayList<String>();
	private static int maxPageCount = 100;
	
	public CnfolStockCommandService(){
		CommandName = "com.smart.spider.cnfol.stock";

		Description = "中金在线";

		Author = "smart";
	}

	@Override
	public void Init(String[] args) {
		urlList.add("http://bank.cnfol.com/yinhangyeneidongtai/");
		urlList.add("http://bank.cnfol.com/pinglunfenxi/");
		urlList.add("http://bank.cnfol.com/yanghang/");
		urlList.add("http://bank.cnfol.com/yinjianhui/");
		urlList.add("http://bank.cnfol.com/yinhangrenwu/");
		urlList.add("http://bank.cnfol.com/yinhangkadongtai/");
		urlList.add("http://bank.cnfol.com/xindaizixun/");
		urlList.add("http://bank.cnfol.com/yinhanglicai/");
		urlList.add("http://bank.cnfol.com/jujiayinhang/");
		urlList.add("http://bank.cnfol.com/shangshiyinhangzixun/");
		
		urlList.add("http://news.cnfol.com/guoneicaijing/");
		urlList.add("http://news.cnfol.com/guojicaijing/");
		urlList.add("http://news.cnfol.com/diqucaijing/");
		urlList.add("http://news.cnfol.com/gangaotaicaijing/");
		urlList.add("http://news.cnfol.com/shangyeyaowen/");
		urlList.add("http://news.cnfol.com/jingyingguanli/");
		urlList.add("http://news.cnfol.com/chanyejingji/");
		urlList.add("http://news.cnfol.com/xiaofei/");
		urlList.add("http://news.cnfol.com/it/");
		urlList.add("http://news.cnfol.com/zhengquanyaowen/");
		urlList.add("http://review.cnfol.com/tangulunshi/");
		urlList.add("http://qs.stock.cnfol.com/");
		urlList.add("http://news.cnfol.com/touzineican/");
		
		urlList.add("http://sc.stock.cnfol.com/gushizhibo/");
		urlList.add("http://sc.stock.cnfol.com/gushijujiao/");
		urlList.add("http://sc.stock.cnfol.com/shichangceping/");
		urlList.add("http://sc.stock.cnfol.com/shichangjuejin/");
		urlList.add("http://sc.stock.cnfol.com/gppdgdzx/");
		urlList.add("http://sc.stock.cnfol.com/jingcaishuju/");
		
		urlList.add("http://gegu.stock.cnfol.com/gegutuijian/");
		urlList.add("http://gegu.stock.cnfol.com/geguzixun/");
		urlList.add("http://gegu.stock.cnfol.com/gegudianping/");
		urlList.add("http://gegu.stock.cnfol.com/geguyujing/");
		
		urlList.add("http://zldx2.stock.cnfol.com/zhulidongxiang/");
		urlList.add("http://zldx2.stock.cnfol.com/zhulilunshi/");
		urlList.add("http://data.cnfol.com/zhulishuju/");
		urlList.add("http://zldx2.stock.cnfol.com/dazongjiaoyi/");
		urlList.add("http://zldx2.stock.cnfol.com/zhulichicangchengben/");
		
		urlList.add("http://xg.stock.cnfol.com/xinguyaowen/");
		urlList.add("http://xg.stock.cnfol.com/xingupinglun/");
		urlList.add("http://xg.stock.cnfol.com/xingugonggao/");
		urlList.add("http://xg.stock.cnfol.com/fswgg/");
		
		urlList.add("http://sc.stock.cnfol.com/caijingtoutiao/");
		urlList.add("http://sc.stock.cnfol.com/jinritishi/");
		urlList.add("http://sc.stock.cnfol.com/yaowendianping/");
		
		urlList.add("http://hy.stock.cnfol.com/bankuaijujiao/");
		urlList.add("http://hy.stock.cnfol.com/hangyezonghe/");
		urlList.add("http://hy.stock.cnfol.com/hangyeshuju/");
		
		urlList.add("http://zxqyb.stock.cnfol.com/zxbzx/");
		urlList.add("http://zxqyb.stock.cnfol.com/zxbfenxi/");
		urlList.add("http://zxqyb.stock.cnfol.com/zxbsj/");
		
		urlList.add("http://cyb.stock.cnfol.com/chuangyebanzixun/");
		urlList.add("http://cyb.stock.cnfol.com/chuangyebanfenxi/");
		urlList.add("http://cyb.stock.cnfol.com/chuangyebangegu/");
		urlList.add("http://cyb.stock.cnfol.com/chuangyefengtou/");
		urlList.add("http://cyb.stock.cnfol.com/chuangyebanzhishi/");
		
		urlList.add("http://sc.stock.cnfol.com/wanjiansudi/");
		
		
	}

	@Override
	public void Exec(String[] args) {

		if (null != args && args.length >= 2) {

			try {
				maxPageCount = Integer.parseInt(args[1]);

			} catch (Exception e) {

				e.printStackTrace();

			}
		}

		dataProvider.Open();

		for (String url : urlList) {

			try {

				ExtractHtmlContent(url);

			} catch (Exception e) {

				logger.error(e);

			}

		}

		dataProvider.Close();

	}
	
	/**
	 * 获取文章的链接，标题，来源
	 * @param url
	 */
	private void ExtractHtmlContent(String url){
		for (int i = 1; i <= maxPageCount; i++) {
			String urlItem ="";
			if(i==1){
				urlItem = url+"index.shtml";
			}else{
				urlItem = url+"index_"+CnfolUtil.getpageNum(i)+".shtml";
			}
			if(CnfolUtil.getCnfolNextPage(urlItem)){
				String htmlContent = HttpClientManage.GetInstance().GetRequest(urlItem, CnfolUtil.connect(urlItem));

				Document document = Jsoup.parse(htmlContent);
				
				Elements ele_ul = document.select("div.Fl.W630>ul");
				String mixMes = document.title().replaceAll("[ ]", "").replaceAll("-", "_");
				for (int k = 0; k < ele_ul.size(); k++) {
					Elements ele_li = ele_ul.get(k).select("li");
					for (int j = 0; j < ele_li.size(); j++) {
						String urlPath = ele_li.get(j).select("a").attr("href");// 链接
						String urlname = ele_li.get(j).select("a").text();// 标题
						getURLWords(mixMes, urlPath, urlname);
					}
				}
			}
			
		}
		
		
	}
	

	/**
	 * 获取文章内容，拼接kafka信息流
	 * @param mixMes
	 * @param urlPath
	 * @param urlname
	 */
	private void getURLWords(String mixMes, String urlPath, String urlname){
		String htmlContent = HttpClientManage.GetInstance().GetRequest(urlPath, CnfolUtil.connect(urlPath));

		Document document = Jsoup.parse(htmlContent);
		
		Message message = new Message();
		message.SiteName = "中金在线";
		message.SpiderName = this.CommandName;
		message.InternalCategory = "网络媒体_综合门户_中金在线";
		message.Timestamp = System.currentTimeMillis();
		message.contentType = ContentType.Article;

		Article article = new Article();
		article.Title =urlname;
		message.ExternalCategory = mixMes;
		message.Url = urlPath;
		message.UrlHash = HelperUtil.ToMd5(message.Url);
		article.PublishTime = DateUtil.toDateTime(document.select("div.Subtitle>span#pubtime_baidu").text().replaceAll(" ", " "),"yyyy-MM-ddHH:mm");//发布时间 yyyy-mm-dd HH:mm:ss
		article.PublishTimestamp = DateUtil.toTimeStamp(article.PublishTime);
		
		article.ContentText = document.getElementById("Content").text().replace("　　", "");//文章内容
		article.ContentHtml = document.getElementById("Content").html();//带HTML格式的文章内容
		
		article.CommentCount = 0;
		article.ZhuanFaCount = 0;
		article.PraiseCount = 0;
		article.ReadCount = 0;
		article.Referrer=document.select("div.Subtitle>span#source_baidu").text().replace("来源：", "");//来源
		article.Author=document.select("div.Subtitle>span#author_baidu").text().replace("作者：", "");//作者
		
		

		message.content = article;
		
		if(StringUtils.isNotEmpty(article.ContentText)){
			dataProvider.Send(message);
		}
		
		

	}

}
