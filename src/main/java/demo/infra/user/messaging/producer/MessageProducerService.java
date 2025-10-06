package demo.infra.user.messaging.producer;

import demo.infra.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MessageProducerService implements IMessageProducerService {
  private final KafkaTemplate<String, Object> kafkaTemplate;
  public MessageProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @Value("${app.kafka.topic.newUser}")
  private String newUserTopic;

  @Override
  public void sendNewUser(User user) {
    final var message = new demo.portfolio.infra.schemas.user.NewUserMessage(user.getId(), user.getLogin());
    kafkaTemplate.send(newUserTopic, UUID.randomUUID().toString(), message)
      .thenAccept(response -> {})
      .exceptionally(ex -> null);
  }
}
