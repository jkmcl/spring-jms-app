package hello;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import hello.config.JmsProperties;
import hello.messaging.CommandMessageProcessor;
import hello.messaging.TextMessenger;
import hello.service.Command;
import hello.service.DummyService;

@SpringBootTest
class ApplicationTest {

	private final Logger log = LoggerFactory.getLogger(ApplicationTest.class);

	@Autowired
	private TextMessenger messenger;

	@Autowired
	private DummyService service;

	@Autowired
	private JmsProperties properties;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private CommandMessageProcessor cmdMsgProcessor;

	@Test
	void testTextMessenger() {
		var expected = "This message was sent via the messenger object";
		messenger.send(properties.getQueue().getOutbound(), expected);
		var actual = messenger.receive(properties.getQueue().getOutbound());
		assertEquals(expected, actual);
	}

	@Test
	void testSendMessage() {
		log.info("Sending 1 outbound message...");
		var expected = "This message was sent via the service object";
		service.sendSomething(expected);

		await().until(() -> {
			var str = (String) jmsTemplate.receiveAndConvert(properties.getQueue().getOutbound());
			return expected.equals(str);
		});
	}

	@Test
	void testInboundMessage() {
		log.info("Sending 1 inbound message...");
		var expected = "Hello world!";
		messenger.send(properties.getQueue().getInbound(), expected);

		await().until(() -> {
			var msg = service.getReceivedMessage();
			return expected.equals(msg);
		});
	}

	@Test
	void testMultipleInboundMessages() {
		log.info("Sending 8 inbound messages...");
		messenger.send(properties.getQueue().getInbound(), "msg1");
		messenger.send(properties.getQueue().getInbound(), "msg2");
		messenger.send(properties.getQueue().getInbound(), "msg3");
		messenger.send(properties.getQueue().getInbound(), "msg4");
		messenger.send(properties.getQueue().getInbound(), "msg5");
		messenger.send(properties.getQueue().getInbound(), "msg6");
		messenger.send(properties.getQueue().getInbound(), "msg7");
		messenger.send(properties.getQueue().getInbound(), "msg8");

		await().until(() -> service.getReceivedMessageCount() == 8);
	}

	@Test
	void testCommandMessage() throws Exception {
		log.info("Sending 1 command message...");

		Command command = new Command();
		command.setName("doWork");
		command.putParameter("param1", "value1");
		command.putParameter("param2", "value2");

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(command);
		log.info("JSON: {}", json);

		messenger.send(properties.getQueue().getCommand(), json);

		var commandRef = new Reference<Command>();
		await().until(() -> {
			var cmd = cmdMsgProcessor.getReceivedCommand();
			commandRef.set(cmd);
			return cmd != null;
		});

		var receivedCommand = commandRef.get();
		assertEquals(command.getName(), receivedCommand.getName());
		assertEquals(command.getParameter("param1"), receivedCommand.getParameter("param1"));
		assertEquals(command.getParameter("param2"), receivedCommand.getParameter("param2"));
	}

}
