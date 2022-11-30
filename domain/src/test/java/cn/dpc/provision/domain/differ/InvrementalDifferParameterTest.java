package cn.dpc.provision.domain.differ;

import cn.dpc.provision.domain.*;
import cn.dpc.provision.domain.Customer.CustomerId;
import cn.dpc.provision.domain.condition.CustomerCriteriaCondition;
import cn.dpc.provision.domain.differ.DataVersion.TimeVersionObject;
import cn.dpc.provision.domain.differ.DifferResult.DifferContent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static cn.dpc.provision.domain.differ.DifferResult.HAS_NO_UPDATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class InvrementalDifferParameterTest extends ConfigurationTestBase {

    public static final List<StaticStatus> ORIGINAL_STATUS = List.of(StaticStatus.values());

    public static record DifferParamTestData(List<Configuration> configurations,
                                             String type,
                                             String version,
                                             Customer customer,
                                             DifferResult differResult) {
    }

    private static CustomerConfigurations customerConfigurations;
    private static IncrementalDiffer differ;

    @BeforeAll
    public static void setUp() {
        customerConfigurations = Mockito.mock(CustomerConfigurations.class);
        differ = new IncrementalDiffer(customerConfigurations);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void should_differ_result_is_expected(DifferParamTestData data) {
        when(customerConfigurations.flux(any(), any())).thenReturn(Flux.fromIterable(data.configurations));
        StepVerifier.create(differ.diff(data.type, data.version, data.customer))
                .expectNextMatches(result -> {
                    System.out.println(data);
                    System.out.println(result);

                    assertEquals(result.isHasUpdate(), data.differResult.isHasUpdate());
                    assertEquals(result.isUseDefault(), data.differResult.isUseDefault());
                    assertEquals(result.getVersion(), data.differResult.getVersion());
                    assertTrue(result.getContent() == null && data.differResult.getContent() == null ||
                            Objects.equals((result.getContent()).size(), data.differResult.getContent().size())
                                    && result.getContent().stream()
                                    .allMatch(resultContent ->
                                            !CollectionUtils.isEmpty(data.differResult.getContent())
                                                    && data.differResult.getContent().stream()
                                                    .anyMatch(expectContent -> Objects.equals(expectContent.key(), resultContent.key())
                                                            && Objects.equals(expectContent.items().size(), resultContent.items().size())
                                                            && expectContent.items().stream()
                                                            .allMatch(expectItem -> resultContent.items().stream()
                                                                    .anyMatch(resultItem -> Objects.equals(resultItem.id(), expectItem.id()))))));
                    return true;
                })
                .verifyComplete();
    }

    public static Stream<DifferParamTestData> data() {
        List<DifferParamTestData> result = new ArrayList<>();
        result.addAll(emptyDatas());
        result.addAll(multiData(1));
        result.addAll(multiData(2));
//        result.addAll(multiData(3));
        return result.stream();
    }

    private static Collection<DifferParamTestData> multiData(int cnt) {

        List<DifferParamTestData> result = new ArrayList<>();
        List<List<LocalDateTime>> startTimes = getStartTimes(cnt);

        List<List<LocalDateTime>> updateTimes = getUpdateTimes(cnt);

        List<List<StaticStatus>> statuses = getStatuses(cnt);

        statuses.forEach(status -> {
            startTimes.forEach(startTime -> {
                updateTimes.forEach(updateTime -> {
                    List<LocalDateTime> startTimeList = new ArrayList<>();
                    List<LocalDateTime> updateTimeList = new ArrayList<>();
                    List<Configuration> data = IntStream.range(0, cnt)
                            .mapToObj(index -> {
                                if (null != startTime.get(index)) {
                                    startTimeList.add(startTime.get(index));
                                }

                                updateTimeList.add(updateTime.get(index));

                                CustomerCriteriaCondition condition = CustomerCriteriaCondition.builder().build();
                                return generateConfiguration("id" + index, "key1", new HashMap<>(), status.get(index), new TimeRange(startTime.get(index), null), condition, "type", updateTime.get(index));
                            }).collect(Collectors.toList());

                    data.addAll(IntStream.range(0, cnt)
                            .mapToObj(index -> {

                                if (null != startTime.get(index)) {
                                    startTimeList.add(startTime.get(index));
                                }

                                updateTimeList.add(updateTime.get(index));

                                CustomerCriteriaCondition condition = CustomerCriteriaCondition.builder().build();
                                return generateConfiguration("idd" + index, "key2", new HashMap<>(), status.get(index), new TimeRange(startTime.get(index), null), condition, "type", updateTime.get(index));
                            }).collect(Collectors.toList()));


                    LocalDateTime lastStartTime = startTimeList.stream().max(Comparator.comparing(Function.identity())).orElse(null);
                    LocalDateTime lastUpdateTime = updateTimeList.stream().max(Comparator.comparing(Function.identity())).orElse(null);
                    TimeVersionObject timeVersion = TimeVersionObject.builder().lastUpdateTime(lastUpdateTime).lastStartTime(lastStartTime).build();

                    result.addAll(genSingleRecordSData(data, timeVersion));
                });
            });
        });

        return result;
    }

    private static List<List<StaticStatus>> getStatuses(int cnt) {
        List<StaticStatus> list = new ArrayList<>();
        List<List<StaticStatus>> result = new ArrayList<>();
        for (int i = 0; i < Math.pow(4, cnt); i++) {
            ArrayList<StaticStatus> init = new ArrayList<>();
            for (int j = 0; j < cnt; j++) {
                init.add(null);
            }

            result.add(init);
        }

        setDataOfLayer(cnt, cnt, result, ORIGINAL_STATUS);

        System.out.println("STATUSES: " + result);
        return result;
    }

    private static List<List<LocalDateTime>> getStartTimes(int cnt) {
        List<LocalDateTime> originalTimes = new ArrayList<>();
        originalTimes.add(null);
        IntStream.range(0, cnt).forEach((index -> originalTimes.add(LocalDateTime.parse("2022-07-28T12:00:00").plusHours(index))));
        int originalTimesSize = originalTimes.size();

        List<List<LocalDateTime>> result = new ArrayList<>();
        for (int i = 0; i < Math.pow(originalTimesSize, cnt); i++) {
            ArrayList<LocalDateTime> init = new ArrayList<>();
            for (int j = 0; j < cnt; j++) {
                init.add(null);
            }

            result.add(init);
        }

        setDataOfLayer(cnt, cnt, result, originalTimes);

        System.out.println("startTimes: " + result);
        return result;
    }

    private static List<List<LocalDateTime>> getUpdateTimes(int cnt) {
        List<LocalDateTime> originalTimes = new ArrayList<>();
        IntStream.range(0, cnt).forEach((index -> originalTimes.add(LocalDateTime.parse("2022-07-28T12:00:00").plusHours(index))));
        int originalTimesSize = originalTimes.size();

        List<List<LocalDateTime>> result = new ArrayList<>();
        for (int i = 0; i < Math.pow(originalTimesSize, cnt); i++) {
            ArrayList<LocalDateTime> init = new ArrayList<>();
            for (int j = 0; j < cnt; j++) {
                init.add(null);
            }

            result.add(init);
        }

        setDataOfLayer(cnt, cnt, result, originalTimes);

        System.out.println("updateTimes: " + result);
        return result;
    }

    private static <T> void setDataOfLayer(int curLayer, int totalLayer, List<List<T>> statuses, List<T> originalTimes) {
        int totalSize = statuses.size();
        if (curLayer <= 0) {
            return;
        }

        for (int i = 0; i < totalSize; i++) {
            List<T> configStatuses = statuses.get(i);
            configStatuses.set(curLayer - 1, originalTimes.get(i / (int) Math.pow(originalTimes.size(), totalLayer - curLayer) % originalTimes.size()));
        }

        setDataOfLayer(curLayer - 1, totalLayer, statuses, originalTimes);
    }

    static Collection<DifferParamTestData> emptyDatas() {
        return Arrays.asList(
                new DifferParamTestData(Collections.emptyList(),
                        "type", "",
                        Customer.builder().build(),
                        HAS_NO_UPDATE),

                new DifferParamTestData(Collections.emptyList(),
                        "type", "",
                        Customer.builder().customerId(new CustomerId("111")).build(),
                        HAS_NO_UPDATE),

                new DifferParamTestData(Collections.emptyList(),
                        "type", "112323213-12123232_0",
                        Customer.builder().build(),
                        DifferResult.USE_DEFAULT),

                new DifferParamTestData(Collections.emptyList(),
                        "type", "112323213-12123232_0",
                        Customer.builder().customerId(new CustomerId("111")).build(),
                        DifferResult.USE_DEFAULT),

                new DifferParamTestData(Collections.emptyList(),
                        "type", "112323213-12123232_1",
                        Customer.builder().build(),
                        DifferResult.USE_DEFAULT),

                new DifferParamTestData(Collections.emptyList(),
                        "type", "112323213-12123232_1",
                        Customer.builder().customerId(new CustomerId("111")).build(),
                        DifferResult.USE_DEFAULT)
        );
    }

    private static List<DifferParamTestData> genSingleRecordSData(List<Configuration> data, TimeVersionObject timeVersion) {
        List<DifferContent> contents = data.stream()
                .collect(Collectors.groupingBy(configuration -> configuration.getDescription().getKey()))
                .entrySet().stream().map(entry -> new DifferContent(entry.getKey(), 0, null,
                        entry.getValue().stream()
                                .filter(configuration -> configuration.getDescription().getStatus() == DynamicStatus.IN_PROGRESS)
                                .map(DifferResult.DifferItem::from)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());

        List<DifferContent> filterByTimeContents = data.stream()
                .collect(Collectors.groupingBy(configuration -> configuration.getDescription().getKey()))
                .entrySet().stream().map(entry -> new DifferContent(entry.getKey(), 0, null,
                        entry.getValue().stream()
                                .filter(configuration -> configuration.getDescription().getStatus() == DynamicStatus.IN_PROGRESS)
                                .filter(configuration -> TimeVersionObject.create(timeVersion.getLastUpdateTime().minusHours(1), timeVersion.getLastStartTime())
                                        .hasUpdated(configuration.getDescription().getUpdatedAt(), Optional.ofNullable(configuration.getDescription().getTimeRange()).map(TimeRange::getStartDate).orElse(null))
                                )
                                .map(DifferResult.DifferItem::from)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());

        return Arrays.asList(
                new DifferParamTestData(data,
                        "type", "",
                        Customer.builder().build(),
                        new DifferResult(true, false, contents, timeVersion + "_0")),
                new DifferParamTestData(data,
                        "type", "",
                        Customer.builder().customerId(new CustomerId("111")).build(),
                        new DifferResult(true, false, contents, timeVersion + "_1")),

                new DifferParamTestData(data,
                        "type", timeVersion + "_1",
                        Customer.builder().build(),
                        new DifferResult(true, false, contents, timeVersion + "_0")),

                new DifferParamTestData(data,
                        "type", timeVersion + "_0",
                        Customer.builder().build(),
                        HAS_NO_UPDATE),

                new DifferParamTestData(data,
                        "type", TimeVersionObject
                        .create(timeVersion.getLastUpdateTime().minusHours(1),
                                timeVersion.getLastStartTime()) + "_0",
                        Customer.builder().build(),
                        new DifferResult(true, false, filterByTimeContents, timeVersion + "_0")),

                new DifferParamTestData(data,
                        "type", TimeVersionObject
                        .create(timeVersion.getLastUpdateTime().minusHours(1),
                                timeVersion.getLastStartTime()) + "_1",
                        Customer.builder().customerId(new CustomerId("111")).build(),
                        new DifferResult(true, false, filterByTimeContents, timeVersion + "_1")),

                new DifferParamTestData(data,
                        "type", timeVersion + "_1",
                        Customer.builder().customerId(new CustomerId("111")).build(),
                        HAS_NO_UPDATE),

                new DifferParamTestData(data,
                        "type", timeVersion + "_0",
                        Customer.builder().customerId(new CustomerId("111")).build(),
                        new DifferResult(true, false, contents, timeVersion + "_1")),

                new DifferParamTestData(data,
                        "type", "notExistVersion" + "_0",
                        Customer.builder().build(),
                        new DifferResult(true, false, contents, timeVersion + "_0")),

                new DifferParamTestData(data,
                        "type", "notExistVersion" + "_0",
                        Customer.builder().build(),
                        new DifferResult(true, false, contents, timeVersion + "_0")),

                new DifferParamTestData(data,
                        "type", "notExistVersion" + "_1",
                        Customer.builder().customerId(new CustomerId("111")).build(),
                        new DifferResult(true, false, contents, timeVersion + "_1"))
        );
    }
}
