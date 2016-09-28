package com.smart.spider.data;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.smart.spider.data.meta.Message;
import com.smart.spider.util.PropertiesUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

public class ActiveMqDataProvider extends DataProvider {

	private String Server = "";
	private String Topic = "";

	private final Logger logger = LogManager.getLogger(ActiveMqDataProvider.class);
	private XStream xStream = null;

	private Connection connection = null;
	private Session session = null;
	private Destination destination = null;
	private javax.jms.MessageProducer producer = null;

	public ActiveMqDataProvider() {

		// init xstream
		this.xStream = new XStream(new JettisonMappedXmlDriver());

	}

	@Override
	public void Open() {

		this.init();

	}

	@Override
	public void Close() {

		try {

			if (null != producer) {
				producer.close();
				producer = null;
			}
			if (null != destination) {
				destination = null;
			}

			if (null != session) {
				session.close();
				session = null;
			}

			if (null != connection) {
				connection.stop();
				connection.close();
				connection = null;
			}

		} catch (Exception e) {

			logger.error(e);

		}

	}

	@Override
	public void Send(Message message) {

		try {

			if (null == message) {
				throw new Exception("message对象未实例化");
			}

			message.content.Trim();

			if (xStream == null) {
				throw new Exception("xStream对象未实例化");
			}

			String jsonMessage = "";

			try {
				jsonMessage = xStream.toXML(message);
			} catch (Exception e) {
				throw new Exception("message序列化json异常");
			}

			if (null == jsonMessage || "".equals(jsonMessage)) {
				throw new Exception("jsonMessage内容为空");
			}

			if (message.content.Verify() == false) {

				logger.warn(jsonMessage);

				throw new Exception("message.content内容验证失败");
			}

			TextMessage textMessage = session.createTextMessage(jsonMessage);

			SendTextMessage(textMessage);

		} catch (Exception e) {

			logger.error(e);

		}

	}

	private void init() {

		try {

			Server = PropertiesUtil.getValue("server", "/activemq.properties");

			Topic = PropertiesUtil.getValue("topic", "/activemq.properties");

			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					ActiveMQConnection.DEFAULT_USER,
					ActiveMQConnection.DEFAULT_PASSWORD, 
					Server);

			connection = connectionFactory.createConnection();

			connection.start();

			session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);

			destination = session.createQueue(Topic);

			producer = session.createProducer(destination);

			producer.setDeliveryMode(DeliveryMode.PERSISTENT);

		} catch (Exception e) {

			logger.error(e);
		}
	}

	private void SendTextMessage(javax.jms.TextMessage textMessage) {

		try {

			producer.send(textMessage);

			session.commit();

		} catch (Exception e) {

			logger.error(e);

		}

	}
}
