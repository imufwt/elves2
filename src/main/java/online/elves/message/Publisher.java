package online.elves.message;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 事件发送.
 */
@Component
public class Publisher {
    
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    
    /**
     * 发送事件
     *
     * @param event 事件对象
     */
    public void send(ApplicationEvent event) {
        if (event != null) {
            applicationEventPublisher.publishEvent(event);
        }
    }
}
