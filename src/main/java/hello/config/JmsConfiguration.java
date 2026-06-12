package hello.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;

import hello.messaging.CommandMessageProcessor;
import hello.messaging.InboundMessageProcessor;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageListener;

@Configuration
@EnableJms
@EnableConfigurationProperties(JmsProperties.class)
public class JmsConfiguration implements JmsListenerConfigurer {

	@Autowired
	private JmsProperties properties;

	@Autowired
	private InboundMessageProcessor inMsgProcessor;

	@Autowired
	private CommandMessageProcessor cmdMsgProcessor;

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
