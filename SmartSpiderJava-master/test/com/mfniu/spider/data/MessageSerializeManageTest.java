package com.mfniu.spider.data;

import org.junit.Test;

import com.smart.spider.data.MessageSerializeManage;
import com.smart.spider.data.meta.BBSComment;
import com.smart.spider.data.meta.Message;

public class MessageSerializeManageTest {

	@Test
	public void testSerialize() {

		MessageSerializeManage instance = MessageSerializeManage.getInstance();

		String msg = "{\"com.mfniu.spider.data.meta.Message\":{\"InternalCategory\":\"金融社区_股吧_东方财富股吧_评论\",\"ExternalCategory\":\"金融社区_股吧_东方财富股吧_评论\",\"Timestamp\":1447747556728,\"Url\":8083208338,\"UrlHash\":\"80cfb891e239b6c9c1a79bef9d6e5d41\",\"contentType\":\"BBSComment\",\"content\":{\"@class\":\"com.mfniu.spider.data.meta.BBSComment\",\"Id\":8083208338,\"ParentId\":\"\",\"ArticleId\":222167055,\"UserInfoId\":\"\",\"CommentTime\":\"2015-11-17 04:55:15\",\"Content\":\"应网上网下合一\",\"Zan\":0},\"Version\":0.1}}";

		/*
		 * Message messageModel = new Message(); messageModel.Url =
		 * "000000000000000000"; messageModel.content = new BBSUser(); String
		 * json = instance.Serialize(messageModel);
		 */
		Message message = instance.Deserialize(msg);

		switch (message.contentType) {

		case BBSComment:

			BBSComment userModel = (BBSComment) message.content;

			System.out.println(userModel.Content);

			break;

		}

		System.out.println(message.contentType);

	}

}
