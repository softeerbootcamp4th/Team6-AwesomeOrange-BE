package hyundai.softeer.orange.event.draw.component.picker;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class AccSumBasedWinnerPickerTest {

    @DisplayName("정상적으로 N개의 서로 다른 아이템을 뽑는지 검사")
    @Test
    public void test_pickMany() {
        AccSumBasedWinnerPicker picker = new AccSumBasedWinnerPicker();
        List<PickTarget> pickTargets = new ArrayList<>();

        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            pickTargets.add(new PickTarget((long)i, random.nextInt(1, 200)));
        }

        int pickCount = 1000;
        long startTime = System.currentTimeMillis();
        // pickMany와 pickMany를 바꿔가면서 테스트 필요.
        var picked = picker.pickMany(pickTargets, pickCount);
        long endTime = System.currentTimeMillis();
        log.info("time lapse: {}", (endTime - startTime));

        var distinctPicked = picked.stream().map(PickTarget::key).collect(Collectors.toSet());
        assertThat(distinctPicked).hasSize(1000);
    }

    @DisplayName("정상적으로 N개의 서로 다른 아이템을 뽑는지 검사")
    @Test
    public void test_pickManyUsingSet() {
        AccSumBasedWinnerPicker picker = new AccSumBasedWinnerPicker();
        List<PickTarget> pickTargets = new ArrayList<>();

        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            pickTargets.add(new PickTarget((long)i, random.nextInt(1, 200)));
        }

        int pickCount = 1000;
        long startTime = System.currentTimeMillis();
        // pickMany와 pickMany를 바꿔가면서 테스트 필요.
        var picked = picker.pickManyUsingSet(pickTargets, pickCount);
        long endTime = System.currentTimeMillis();
        log.info("time lapse: {}", (endTime - startTime));

        var distinctPicked = picked.stream().map(PickTarget::key).collect(Collectors.toSet());
        assertThat(distinctPicked).hasSize(1000);
    }

    @DisplayName("binary search가 제대로 동작하는지 테스트")
    @Test
    public void test_binarySearch() {
        AccSumBasedWinnerPicker picker = new AccSumBasedWinnerPicker();
        AccSumBasedWinnerPicker.RandomItem[] items = new AccSumBasedWinnerPicker.RandomItem[5];

        var target = new PickTarget(0L, 0L);

        items[0] = new AccSumBasedWinnerPicker.RandomItem(target, 5);
        items[1] = new AccSumBasedWinnerPicker.RandomItem(target, 10);
        items[2] = new AccSumBasedWinnerPicker.RandomItem(target, 16);
        items[3] = new AccSumBasedWinnerPicker.RandomItem(target, 21);
        items[4] = new AccSumBasedWinnerPicker.RandomItem(target, 33);

        // 1 ~ 5는 item 0에, 6 ~ 10은 item 1에 존재
        int idx = picker.binarySearch(items, 1);
        assertThat(idx).isEqualTo(0);
        int idx2 = picker.binarySearch(items, 5);
        assertThat(idx2).isEqualTo(0);
        int idx3 = picker.binarySearch(items, 6);
        assertThat(idx3).isEqualTo(1);
        int idx4 = picker.binarySearch(items, 10);
        assertThat(idx4).isEqualTo(1);
        int idx5 = picker.binarySearch(items, 11);
        assertThat(idx5).isEqualTo(2);
        int idx6 = picker.binarySearch(items, 33);
        assertThat(idx6).isEqualTo(4);
    }

    @DisplayName("정상적으로 누적합을 만드는지 검사")
    @Test
    public void test_getAccumulatedItems() {

        List<PickTarget> pickTargets = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            pickTargets.add(new PickTarget((long)i, i));
        }
        AccSumBasedWinnerPicker picker = new AccSumBasedWinnerPicker();

        var randItems = picker.getAccumulatedItems(pickTargets);

        assertThat(randItems).hasSize(4);
        assertThat(randItems[0].score()).isEqualTo(1);
        assertThat(randItems[1].score()).isEqualTo(3);
        assertThat(randItems[2].score()).isEqualTo(6);
        assertThat(randItems[3].score()).isEqualTo(10);
    }
}