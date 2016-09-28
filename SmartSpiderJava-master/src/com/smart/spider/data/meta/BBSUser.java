package com.smart.spider.data.meta;

/**
 * 论坛用户
 *
 * @author smart
 */
public class BBSUser extends BaseContent {

    /**
     * 用户ID
     */
    public String Id;

    /**
     * 用户昵称
     */
    public String UserName;

    /**
     * 用户简介
     */
    public String Summary;

    /**
     * 注册时间
     */
    public String RegTime;

    /**
     * 影响力
     */
    public int Force;

    /**
     * 总访问次数
     */
    public int TotalAccess;

    /**
     * 粉丝总数
     */
    public int Fans;

    /**
     * 关注总数
     */
    public int Focus;

    /**
     * 自选股
     */
    public int StockCount;

    public BBSUser() {
        Id = "";
        UserName = "";
        Summary = "";
        RegTime = "";
        Force = 0;
        TotalAccess = 0;
        Fans = 0;
        Focus = 0;
        StockCount = 0;
    }

	@Override
	public boolean Verify() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void Trim() {
		// TODO Auto-generated method stub
		
	}
}
