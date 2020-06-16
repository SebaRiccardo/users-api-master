package unsl.config;

import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;



@Configuration
public class CacheConfig {

    public static final String user_CACHE = "users";
    //@Value("${cache.users.maximum.size:2}")
    //private int usersMaxSize;

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
       
        simpleCacheManager.setCaches(Arrays.asList(buildUsersCache()));
       
        return simpleCacheManager;
    }


    private GuavaCache buildUsersCache() {
        return new GuavaCache(user_CACHE, CacheBuilder
                .newBuilder()
                .expireAfterAccess(23,TimeUnit.HOURS)
                .expireAfterWrite(167, TimeUnit.HOURS)
                .build(),
                true);
    }

}