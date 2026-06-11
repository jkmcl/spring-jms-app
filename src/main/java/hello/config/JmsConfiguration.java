package hello.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;

import hello.messaging.CommandMessageProcessor;
import hello.messaging.InboundMessageProcessor;
import jakarta.annotation.PostConstruct;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageListener;

@Configuration
@EnableJms
@EnableConfigurationProperties(JmsProperties.class)
public class JmsConfiguration implements JmsListenerConfigurer {

	private final Logger logger = LoggerFactory.getLogger(JmsConfiguration.class);

	@Autowired
	private JmsProperties properties;

	@Autowired
	private InboundMessageProcessor inMsgProcessor;

	@Autowired
	private CommandMessageProcessor cmdMsgProcessor;

	@Autowired
	private Environment environment;

	@PostConstruct
	void init() {
		logger.atInfo().log("Property os.path: {}", environment.getProperty("os.path"));
	}

	private static void registerEndpoint(JmsListenerEndpointRegistrar registrar, String destination, MessageListener messageListener) {
		var inboundEndpoint = new SimpleJmsListenerEndpoint();
		inboundEndpoint.setId(destination);
		inboundEndpoint.setDestination(destination);
		inboundEndpoint.setMessageListener(messageListener);
		registrar.registerEndpoint(inboundEndpoint);
	}

	@Override
	public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
		registerEndpoint(registrar, properties.getQueue().getInbound(), message -> inMsgProcessor.onMessage(message));
		registerEndpoint(registrar, properties.getQueue().getCommand(), message -> cmdMsgProcessor.onMessage(message));
	}

	@Bean
	@ConditionalOnProperty(name = "activemq.enabled", havingValue = "false", matchIfMissing = false)
	ConnectionFactory connectionFactory() {
		// We can create our own connection factory here if necessary
		return null;
	}

}
