package online.elves.enums;

import cn.hutool.core.collection.CollUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 所谓语料?
 */
@Getter
public enum Words {
    /**
     * 打劫
     */
    Robbery_0(0, " 抢到 300 积分.半路遇见阿达被他要走了...呜呜呜~~~", "r"),
    Robbery_1(1, " 手拿搬砖, 鼻孔朝天. 我要抢劫啦! 都出来受死...", "r"),
    Robbery_2(2, " 怎么肥事, 我司四有好少年, 要不我去天桥给你要点吧...ε=(´ο｀*)))唉", "r"),
    Robbery_3(3, " 我能抢到么? 我这身板? 啊? 你想过我的感受么? 我生气了, 哄不好的那种!!!", "r"),
    Robbery_4(4, " 本来想去的, 但是我转念一想.咱们要一心向善啊 ~~~ 所以我又回来了", "r"),
    Robbery_5(5, " 我还小, 不要着急...我打不过他们...", "r"),
    Robbery_6(6, " 这不好吧...~~我又不是小冰~~", "r"),
    Robbery_7(7, " emmm... 我帮你艾特一下阿达? ", "r"),
    Robbery_8(8, " 哎...真不让人省心...😌...救命呀! 这个人要打劫, 快来抓坏人啦...", "r"),
    Robbery_9(9, " 已举报, 300 积分到手...美滋滋~ 小冰, 咱们买糖去吧!", "r"),
    /**
     * 默认值
     */
    Default_0(0, " 你可以试着对我说 `凌 帮助`", "def"),
    Default_1(1, " emmm...让我想想", "def"),
    Default_2(2, " 听不懂诶! 😌", "def"),
    Default_3(3, " 啊? 刚在想小冰在干嘛! 你说什么? 麻烦再说一遍", "def"),
    Default_4(4, " 听不懂诶~ 听说来了个小智哦! 他是真的AI呀, 去问问他吧. (#^.^#)", "def"),
    ;

    /**
     * 序号
     */
    public int code;

    /**
     * 语料
     */
    public String words;

    /**
     * 类型
     */
    public String type;

    /**
     * 有参构造
     *
     * @param code
     * @param words
     * @param type
     */
    Words(int code, String words, String type) {
        this.code = code;
        this.words = words;
        this.type = type;
    }

    /**
     * 随机语料...
     *
     * @param type
     * @return
     */
    public static String random(String type) {
        // 返回的值
        String returnWord = " 你等我长大点再聊天吧~";
        // 如果没值
        if (StringUtils.isBlank(type)) {
            return " 啊? 你说什么?...我听不懂啊....我又不是小冰...";
        }
        // 语料集合
        List<Words> wordsList = Arrays.stream(Words.values()).filter(x -> x.getType().equals(type)).collect(Collectors.toList());
        // 没有语料定义
        if (CollUtil.isEmpty(wordsList)) {
            return returnWord;
        }
        // 随机数
        int nextInt = new SecureRandom().nextInt(wordsList.size());
        // 遍历
        for (Words word : wordsList) {
            if (word.code == nextInt) {
                returnWord = word.getWords();
            }
        }
        // 返回
        return returnWord;
    }
}
