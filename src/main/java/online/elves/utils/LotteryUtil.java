package online.elves.utils;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Maps;

import java.util.*;

/**
 * 抽奖工具 - 离散随机算法
 */
public final class LotteryUtil {
    
    /**
     * 随机数生成对象
     */
    private final Random random;
    
    /**
     * 别名
     */
    private final Integer[] alias;
    
    /**
     * 概率
     */
    private final Double[] probability;
    
    /**
     * 初始化概率
     * @param probabilities
     */
    public LotteryUtil(List<Double> probabilities) {
        this(probabilities, new Random());
    }
    
    /**
     * 初始化概率
     * @param probabilities
     * @param random
     */
    public LotteryUtil(List<Double> probabilities, Random random) {
        // 校验参数
        if (CollUtil.isEmpty(probabilities) || Objects.isNull(random)) {
            throw new RuntimeException("初始化概率采集器失败");
        }
        // 概率池大小
        int size = probabilities.size();
        // 初始化概率数组
        probability = new Double[size];
        alias = new Integer[size];
        // 初始化随机对象
        this.random = random;
        // 平均数
        final double average = 1.0 / size;
        // 复制数组
        probabilities = new ArrayList<>(probabilities);
        // 初始化双端队列
        Deque<Integer> small = new ArrayDeque<>();
        Deque<Integer> large = new ArrayDeque<>();
        // 均分概率池 获取下标
        for (int i = 0; i < probabilities.size(); ++i) {
            if (probabilities.get(i) >= average) {
                large.add(i);
            } else {
                small.add(i);
            }
        }
        // 处理权重
        while (!small.isEmpty() && !large.isEmpty()) {
            // 获取队列下标
            int less = small.removeLast();
            int more = large.removeLast();
            // 放大权重前概率
            probability[less] = probabilities.get(less) * probabilities.size();
            alias[less] = more;
            // 缩放概率
            probabilities.set(more, (probabilities.get(more) + probabilities.get(less)) - average);
            // 概率换防
            if (probabilities.get(more) >= 1.0 / probabilities.size()) {
                large.add(more);
            } else {
                small.add(more);
            }
        }
        while (!small.isEmpty()) {
            probability[small.removeLast()] = 1.0;
        }
        while (!large.isEmpty()) {
            probability[large.removeLast()] = 1.0;
        }
    }
    
    public int next() {
        int column = random.nextInt(probability.length);
        boolean coinToss = random.nextDouble() < probability[column];
        return coinToss ? column : alias[column];
    }
    
    /**
     * 获取概率等级
     * @param map
     * @return
     */
    public static int getLv(TreeMap<Integer, Double> map) {
        // 获取概率与奖品等级分组
        List<Double> list = new ArrayList<>(map.values());
        List<Integer> level = new ArrayList<>(map.keySet());
        return level.get(new LotteryUtil(list).next());
    }
    
    
    /**
     * 命中判定, 动态概率 两层
     * @param hitCount
     * @return
     */
    public static boolean hitJudge(int hitCount) {
        // 必然无法命中
        if (hitCount == 0) {
            return false;
        }
        // 动态概率
        TreeMap<Integer, Double> map = Maps.newTreeMap();
        // 命中概率
        String sDouble = "0." + hitCount;
        if (hitCount < 10) {
            sDouble = "0.0" + hitCount;
        } else if (hitCount > 99) {
            sDouble = "0.99";
        }
        Double dHit = Double.valueOf(sDouble);
        map.put(0, Double.valueOf("1") - dHit);
        map.put(1, dHit);
        return LotteryUtil.getLv(map) > 0;
    }
    
    public static void main(String[] args) {
        for (int x = 0; x < 20; x++) {
            System.out.println(x * x * (x << 5 + 32));
        }
    }
    
}
