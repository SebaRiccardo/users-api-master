package unsl.repository;

import unsl.entities.User;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByDni(@Param("dni") Long dni);

	
}
