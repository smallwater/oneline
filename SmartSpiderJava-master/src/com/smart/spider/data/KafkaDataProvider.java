package com.smart.spider.data;

import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.smart.spider.data.meta.Message;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaDataProvider extends DataProvider {

	private final String topicname = "smart.spider.message";
	private final Logger _logger = LogManager.getLogger(KafkaDataProvider.class);
	private ProducerConfig config = null;
	private Producer<String, String> producer = null;
	private XStream xStream = null;

	/**
	 * 默认配置：host1:9092,host2:9092,host3:9092
	 */
	public String metadataBrokerList = "host1:9092,host2:9092,host3:9092";

	public KafkaDataProvider() {

		// init xstream
		this.xStream = new XStream(new JettisonMappedXmlDriver());

	}

	@Override
	public void Open() {

		try {

			// open kafka
			Properties props = new Properties();

			props.put("metadata.broker.list", metadataBrokerList);

			props.put("serializer.class", "kafka.serializer.StringEncoder");

			props.put("request.required.acks", "1");

			this.Close();

			config = new ProducerConfig(props);

			producer = new Producer<String, String>(config);

		} catch (Exception e) {

			_logger.error(e);

		}
	}

	@Override
	public void Close() {

		if (producer != null) {

			try {

				producer.close();

				producer = null;

			} catch (Exception e) {

				_logger.error(e);

			}
		}

		if (config != null) {
			config = null;
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
			} catch (Exception ex) {
				throw new Exception("message序列化json异常");
			}

			if (null == jsonMessage || "".equals(jsonMessage)) {
				throw new Exception("jsonMessage内容为空");
			}

			if (message.content.Verify() == false) {

				_logger.warn(jsonMessage);
				
				throw new Exception("message.content内容验证失败");
			}

			SendKafkaMessage(topicname, jsonMessage);

		} catch (Exception e) {

			_logger.error(e);

		}
	}

	private void SendKafkaMessage(String topic, String msg) {

		try {

			if (null == topic | topic.equals("") | null == msg | msg.equals("")) {
				return;
			}

			_logger.info(topic + ":" + msg);

			KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, msg);

			if (producer == null) {
				throw new Exception("producer未实例化");
			}

			producer.send(data);

		} catch (Exception e) {

			_logger.error(e);

		}
	}

}
