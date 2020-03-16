package com.koop.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koop.bo.PracticalAdvice;
import com.koop.exception.OrderNotFoundException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RequestMapping(path = "${api.name}/kafka")
@RestController()
@Api(description = "Kafka Controller for pub/sub - REST APIs for Kafka related operations")
public class KafkaController {

	private static final Logger logger = LoggerFactory.getLogger(KafkaController.class);

	private final KafkaTemplate<String, Object> template;
	private final String topicName;
	private final int messagesPerRequest;
	private CountDownLatch latch;

	public KafkaController(final KafkaTemplate<String, Object> template,
			@Value("${koop.topic-name}") final String topicName,
			@Value("${koop.messages-per-request}") final int messagesPerRequest) {
		this.template = template;
		this.topicName = topicName;
		this.messagesPerRequest = messagesPerRequest;
	}

	@ApiOperation(value = "Kafka Events", notes = "This is place holder for more description about end point call")

	@GetMapping(value = "/processOrder")
	public Map<String, String> hello() throws Exception {
		latch = new CountDownLatch(messagesPerRequest);
		IntStream.range(0, messagesPerRequest).forEach(
				i -> this.template.send(topicName, String.valueOf(i), new PracticalAdvice("A Practical Advice", i)));
		latch.await(60, TimeUnit.SECONDS);
		logger.info("All messages received");
		Map<String, String> map = new LinkedHashMap<>();
		map.put("response", "Msgs sent successfully");
		return map;
	}

	@GetMapping(value = "/test")
	public String test() {
		if (true)
		throw new OrderNotFoundException("this is test message");
		
		return "";
	}
	
	@KafkaListener(topics = "${koop.topic-name}", clientIdPrefix = "json", containerFactory = "kafkaListenerContainerFactory")
	public void listenAsObject(ConsumerRecord<String, PracticalAdvice> cr, @Payload PracticalAdvice payload) {
		logger.info("Logger 1 [JSON] received key {}: Type [{}] | Payload: {} | Record: {}", cr.key(),
				typeIdHeader(cr.headers()), payload, cr.toString());
		latch.countDown();
	}

	@KafkaListener(topics = "${koop.topic-name}", clientIdPrefix = "string", containerFactory = "kafkaListenerStringContainerFactory")
	public void listenasString(ConsumerRecord<String, String> cr, @Payload String payload) {
		logger.info("Logger 2 [String] received key {}: Type [{}] | Payload: {} | Record: {}", cr.key(),
				typeIdHeader(cr.headers()), payload, cr.toString());
		latch.countDown();
	}

	@KafkaListener(topics = "${koop.topic-name}", clientIdPrefix = "bytearray", containerFactory = "kafkaListenerByteArrayContainerFactory")
	public void listenAsByteArray(ConsumerRecord<String, byte[]> cr, @Payload byte[] payload) {
		logger.info("Logger 3 [ByteArray] received key {}: Type [{}] | Payload: {} | Record: {}", cr.key(),
				typeIdHeader(cr.headers()), payload, cr.toString());
		latch.countDown();
	}

	private static String typeIdHeader(Headers headers) {
		return StreamSupport.stream(headers.spliterator(), false).filter(header -> header.key().equals("__TypeId__"))
				.findFirst().map(header -> new String(header.value())).orElse("N/A");
	}
}