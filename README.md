A sample application demonstrating the following:

* Spring Boot and its JMS features (JmsTemplate, MessageListener, etc.)
* Queue names configurable in application.properties
* Character encoding used in BytesMessage conversion configurable in application.properties
* Business layer receives and sends Strings
* Messaging layer converts inbound TextMessage or BytesMessage to String automatically
* Messaging layer converts outbound String to TextMessage or BytesMessage based on application.properties
* Processing special inbound command messages in JSON format
