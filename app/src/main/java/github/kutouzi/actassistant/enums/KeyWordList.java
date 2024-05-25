package github.kutouzi.actassistant.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeyWordList {
    public static final List<String> pingduoduoKeyWordList = Stream.of("领取今日奖励","专属现金红包","立即提现","领取今日现金","明日继续来领","打款金额")
            .collect(Collectors.toList());
    public static final List<String> meituanKeyWordList = Stream.of("现金秒到账", "開")
            .collect(Collectors.toList());
}
