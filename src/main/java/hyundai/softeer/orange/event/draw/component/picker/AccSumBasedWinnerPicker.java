package hyundai.softeer.orange.event.draw.component.picker;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.*;

@Primary
@Component
public class AccSumBasedWinnerPicker implements WinnerPicker {

    @Override
    public List<PickTarget> pick(List<PickTarget> items, long count) {
        long maxPickCount = Math.min(items.size(), count);
        // TODO: 둘 중 하나를 선택하는 최적 조건 분석하기.
        if (count - maxPickCount <= 10 || // 두 값 차이가 작을 때
                ((double) count / maxPickCount) <= 1.1 ) {
            return pickMany(items, maxPickCount);
        } else {
            return pickManyUsingSet(items, maxPickCount);
        }
    }

    protected List<PickTarget> pickMany(List<PickTarget> targets, long count) {
        List<PickTarget> pickedTargets = new ArrayList<>();
        // 추첨에 참여하는 객체들이 존재하는 set
        Set<PickTarget> targetSet = new HashSet<>(targets);

        for(int i = 0; i < count; i++) {
            RandomItem[] items = getAccumulatedItems(targetSet);
            long bound = items[items.length - 1].score;
            long targetScore = new Random().nextLong(1, bound + 1);
            int pickedIdx = binarySearch(items, targetScore);
            PickTarget pickedTarget = items[pickedIdx].target;

            targetSet.remove(pickedTarget);
            pickedTargets.add(pickedTarget);
        }

        return pickedTargets;
    }

    /**
     * set 자료구조를 이용하여 가중합을 1번만 계산하는 타겟 선택 방식
     * @param targets 선택 대상이 되는 배열
     * @param count
     * @return
     */
    protected List<PickTarget> pickManyUsingSet(List<PickTarget> targets, long count) {
        // 가중합 배열
        RandomItem[] items = getAccumulatedItems(targets);
        List<PickTarget> pickedTargets = new ArrayList<>();
        long bound = items[items.length - 1].score;
        // 이미 선택된 대상이 존재하는 공간
        Set<Integer> pickedIdxSet = new HashSet<>();

        for(int i = 0; i < count; i++) {
            int pickedIdx;
            do {
                long targetScore = new Random().nextLong(1, bound + 1);
                pickedIdx = binarySearch(items, targetScore);
            }while(pickedIdxSet.contains(pickedIdx));
            // 방문했다고 마킹
            pickedIdxSet.add(pickedIdx);
            pickedTargets.add(targets.get(pickedIdx));
        }

        return pickedTargets;
    }



    /**
     * 누적합 배열을 반환한다
     * @param targets 추첨 대상들
     * @return 누적합 형태로 표현된 RandomItem 배열
     */
    protected RandomItem[] getAccumulatedItems(Collection<PickTarget> targets) {
        RandomItem[] items = new RandomItem[targets.size()];
        long score = 0;

        int idx = 0;
        for (PickTarget target : targets) {
            score += target.score();
            items[idx++] = new RandomItem(target, score);
        }

        return items;
    }

    /**
     * 이진 탐색을 이용하여 누적합 배열에서 대상 인덱스를 얻는다.
     * @param items 누적합 배열
     * @param target 대상 점수
     * @return 선택된 인덱스 값
     */
    protected int binarySearch(RandomItem[] items, long target) {
        int low = 0;
        int high = items.length - 1;

        while(low <= high) {
            // >>>은 비트열 오른쪽으로 이동, 빈 공간은 0으로 채움
            // 2로 나누는 로직을 >>>으로 처리
            int mid = (low + high) >>> 1;

            if(items[mid].score > target) {
                high = mid - 1;
            } else if(items[mid].score < target) {
                low = mid + 1;
            } else {
                break;
            }
        }

        return low;
    }

    /**
     * 가중합을 기록해두는 객체
     * @param target 추첨 대상
     * @param score 누적합 점수
     */
    protected record RandomItem(PickTarget target, long score) {}
}
