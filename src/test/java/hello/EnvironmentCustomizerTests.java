package hello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;

class EnvironmentCustomizerTests {

	@Test
	void testOnApplicationEvent() {
		var eventMock = mock(ApplicationEnvironmentPreparedEvent.class);

		var a = new EnvironmentCustomizer();
		a.onApplicationEvent(eventMock);

		assertEquals(System.getenv("PATH"), System.getProperty("os.path"));
	}

}
