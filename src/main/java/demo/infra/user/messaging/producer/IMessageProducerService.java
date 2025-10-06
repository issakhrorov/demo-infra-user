package demo.infra.user.messaging.producer;

import demo.infra.user.model.User;

public interface IMessageProducerService {
  void sendNewUser(User user);
}
