package online.elves.service.strategy.commands;

import lombok.extern.slf4j.Slf4j;
import online.elves.service.strategy.CommandAnalysis;
import online.elves.third.apis.MoYuCalendar;
import online.elves.third.apis.Vocation;
import online.elves.third.fish.Fish;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 摸鱼日历.
 */
@Slf4j
@Component
public class MoyuCalendarAnalysis extends CommandAnalysis {
    /**
     * 关键字
     */
    private static final List<String> keys = Arrays.asList("摸鱼历", "摸鱼", "日历", "鱼历", "报时");

    @Override
    public boolean check(String commonKey) {
        return keys.contains(commonKey);
    }

    @Override
    public void process(String commandKey, String commandDesc, String userName) {
        if (commandKey.equals("报时")) {
            Fish.sendMsg(Vocation.getWord(Vocation.get()));
        } else {
            Fish.sendMsg(MoYuCalendar.getMyCal());
        }
    }
}
