package online.elves.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 定时任务池.
 */
@Configuration
public class ScheduledTaskPool implements SchedulingConfigurer {
    
    @Bean
    public Executor taskExecutor() {
        //指定定时任务线程数量，可根据需求自行调节
        return Executors.newScheduledThreadPool(4);
    }
    
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(taskExecutor());
    }
    
}
