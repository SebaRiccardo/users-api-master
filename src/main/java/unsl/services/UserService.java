package unsl.services;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unsl.entities.User;
import unsl.entities.User.Status;
import unsl.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User findByDni(Long dni) {
        return userRepository.findByDni(dni);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
     
    public User deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);;
        if (user ==  null){
            return null;
        }
        user.setStatus(User.Status.BAJA);
        return userRepository.save(user);
    }
}
