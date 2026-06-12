package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.StringUtils;

/**
 * Demonstrates how to set a system property to the value of an environment
 * variable.
 */
public class EnvironmentCustomizer implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

	private static final String ENV_NAME = "PATH";

	private static final String PROP_NAME = "os.path";

	private final Logger logger = LoggerFactory.getLogger(EnvironmentCustomizer.class);

	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
		var value = System.getenv(ENV_NAME);
		if (StringUtils.hasText(value)) {
			System.setProperty(PROP_NAME, value);
			logger.info("Assigned value of environment variable {} to system property {}", ENV_NAME, PROP_NAME);
		}
	}

}
