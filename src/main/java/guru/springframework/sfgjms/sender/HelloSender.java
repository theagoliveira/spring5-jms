package guru.springframework.sfgjms.sender;

import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import guru.springframework.sfgjms.config.JmsConfig;
import guru.springframework.sfgjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class HelloSender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 2000)
    public void sendMessage() {
        var message = HelloWorldMessage.builder()
                                       .id(UUID.randomUUID())
                                       .message("Hello, World!")
                                       .build();
        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message);
    }

    @Scheduled(fixedRate = 2000)
    public void sendAndReceiveMessage() throws JMSException {
        var message = HelloWorldMessage.builder().id(UUID.randomUUID()).message("Hello...").build();

        log.info("Sending hello...");
        var receivedMessage = jmsTemplate.sendAndReceive(
            JmsConfig.MY_SEND_AND_RECEIVE_QUEUE, new MessageCreator() {

                @Override
                public Message createMessage(Session session) throws JMSException {
                    try {
                        Message helloMessage = session.createTextMessage(
                            objectMapper.writeValueAsString(message)
                        );
                        helloMessage.setStringProperty(
                            "_type", "guru.springframework.sfgjms.model.HelloWorldMessage"
                        );

                        log.info("Sent!");
                        return helloMessage;
                    } catch (JsonProcessingException e) {
                        throw new JMSException("boom!");
                    }
                }

            }
        );

        log.info("Response received!");
        log.info(receivedMessage.getBody(String.class));
    }

}
