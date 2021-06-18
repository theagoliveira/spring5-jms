package guru.springframework.sfgjms.listener;

import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import guru.springframework.sfgjms.config.JmsConfig;
import guru.springframework.sfgjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class HelloListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.MY_QUEUE)
    public void listen(@Payload HelloWorldMessage helloWorldMessage,
                       @Headers MessageHeaders messageHeaders, Message message) {
        // log.info(helloWorldMessage.toString());
    }

    @JmsListener(destination = JmsConfig.MY_SEND_AND_RECEIVE_QUEUE)
    public void listenAndRespond(@Payload HelloWorldMessage helloWorldMessage,
                                 @Headers MessageHeaders messageHeaders,
                                 Message message) throws JMSException {
        log.info("Message received!");
        log.info(helloWorldMessage.toString());
        var response = HelloWorldMessage.builder()
                                        .id(UUID.randomUUID())
                                        .message("...World!")
                                        .build();
        log.info("Sending world...");
        jmsTemplate.convertAndSend(message.getJMSReplyTo(), response);
        log.info("Sent!");
    }

}
