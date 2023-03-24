package online.elves.service.strategy;

/**
 * 精灵命令详解
 */
public abstract class CommandAnalysis {
    
    /**
     * 检查触发关键字
     * @return
     */
    public abstract boolean check(String commonKey);
    
    /**
     * 处理对象
     * @param commandKey  命令关键字
     * @param commandDesc 命令内容
     * @param userName    用户名
     */
    public abstract void process(String commandKey, String commandDesc, String userName);
    
}
