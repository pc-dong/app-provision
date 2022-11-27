package cn.dpc.provision.domain.condition;

import cn.dpc.provision.domain.Customer;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.stream.IntStream;

/**
 * adCode 表示地理位置编号
 * 一共六位数字前两位代表省、中间两位代表市、后两位代表区
 */
@RequiredArgsConstructor
public class LocationConditionMatcher implements Matcher {
    private final LocationCondition locationCondition;
    private boolean match(String adCode) {
        return Optional.ofNullable(locationCondition).map(LocationCondition::getAdCodes)
                .map(list -> list.isEmpty() || Optional.ofNullable(adCode)
                        .map(code ->
                                list.stream().anyMatch(item -> match(item, code)))
                        .orElse(false))
                .orElse(true);
    }

    private boolean match(String configAdCode, String paramAdCode) {
        if (configAdCode.length() != 6 || paramAdCode.length() != 6) {
            return false;
        }

        return IntStream.range(0, 3)
                .allMatch(index -> {
                    int startInclude = index * 2;
                    int endExclude = startInclude + 2;
                    String configCode = configAdCode.substring(startInclude, endExclude);
                    return "00".equals(configCode) || configCode.equals(paramAdCode.substring(startInclude, endExclude));
                });
    }

    @Override
    public Mono<Boolean> match(Customer customer) {
        return Mono.just(this.match(customer.getAdCode()));
    }
}
