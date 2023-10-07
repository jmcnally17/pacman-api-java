package pacmanapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pacmanapi.model.User;

public interface UserRepository extends MongoRepository<User, String> {
  User findByUsername(String username);
}
