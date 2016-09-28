package com.smart.spider.data.meta;

/**
 * 标准消息
 *
 * @author smart
 */
public class Message {

    /**
     * 内部分类
     */
    public String InternalCategory;

    /**
     * 外部分类
     */
    public String ExternalCategory;

    /**
     * 采集时间
     */
    public long Timestamp;

    /**
     * 网站名称
     */
    public String SiteName;

    /**
     * 网页地址
     */
    public String Url;

    /**
     * 网址哈希
     */
    public String UrlHash;

    /**
     * 内容类型,默认网页
     */
    public ContentType contentType;

    /**
     * 消息内容
     */
    public BaseContent content;

    /**
     * 爬虫名称
     */
    public String SpiderName;

    /**
     * 字段版本
     */
    public String Version;

    public Message() {
        InternalCategory = "";
        ExternalCategory = "";
        Timestamp = 0;
        SiteName = "";
        Url = "";
        UrlHash = "";
        contentType = ContentType.WebPage;
        SpiderName = "com.mfniu.spider";
        Version = "0.1";
    }
}
