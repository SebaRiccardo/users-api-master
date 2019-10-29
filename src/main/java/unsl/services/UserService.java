package unsl.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import unsl.entities.User;
import unsl.repository.UserRepository;
import unsl.config.CacheConfig;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }
    @Cacheable(CacheConfig.user_CACHE)
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
    @Cacheable(CacheConfig.user_CACHE)
    public User findByDni(Long dni) {
        return userRepository.findByDni(dni);
    }
    @CachePut(CacheConfig.user_CACHE)
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
