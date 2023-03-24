package online.elves.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池
 */
@Slf4j
@Configuration
public class ThreadPool {
    
    @Bean(name = "taskPool")
    public ThreadPoolTaskExecutor getTaskPool(){
        // 创建线程池
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置属性
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setKeepAliveSeconds(300);
        executor.setQueueCapacity(64);
        // 线程前缀
        executor.setThreadNamePrefix("elves-task-");
        // 拒绝策略  不启用线程, 在调用者所在线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        executor.initialize();
        return executor;
    }
    
    @Bean
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SpringAsyncExceptionHandler();
    }
    
    class SpringAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
            log.error("Exception occurs in async method {}", throwable.getMessage());
        }
    }
}
