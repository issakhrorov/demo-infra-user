package demo.infra.user.repo;

import demo.infra.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends CrudRepository<User, Long> {
  User findByLogin(String login);
  Page<User> findAll(Pageable pageable);
}
