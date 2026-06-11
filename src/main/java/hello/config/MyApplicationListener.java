package hello.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.StringUtils;

/**
 * This class should be registered in META-INF/spring.factories
 */
public class MyApplicationListener implements ApplicationListener<ApplicationContextInitializedEvent> {

	private static final String ENV_NAME = "PATH";

	private static final String PROP_NAME = "os.path";

	private final Logger logger = LoggerFactory.getLogger(MyApplicationListener.class);

	@Override
	public void onApplicationEvent(ApplicationContextInitializedEvent event) {
		var value = System.getenv(ENV_NAME);
		if (StringUtils.hasText(value)) {
			System.setProperty(PROP_NAME, value);
			logger.info("Assigned value of environment variable {} to system property {}", ENV_NAME, PROP_NAME);
		}
	}

}
