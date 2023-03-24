package online.elves.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * redis相关配置
 */
@Component
public class RedisConfigService implements ApplicationContextAware {
    
    private static ApplicationContext applicationContext;
    
    @Qualifier("redisTemplate")
    private static RedisTemplate redisTemplate;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (Objects.isNull(RedisConfigService.applicationContext)) {
            RedisConfigService.applicationContext = applicationContext;
        }
        redisTemplate = (RedisTemplate) applicationContext.getBean("redisTemplate");
    }
    
    public static RedisTemplate get() {
        return redisTemplate;
    }
    
}
