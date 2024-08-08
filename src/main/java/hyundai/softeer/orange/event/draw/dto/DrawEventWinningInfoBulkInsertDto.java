package hyundai.softeer.orange.event.draw.dto;

import lombok.Getter;

@Getter
public class DrawEventWinningInfoBulkInsertDto {
    private long eventUserId;
    private long ranking;
    private long drawEventId;

    public static DrawEventWinningInfoBulkInsertDto of(long eventUserId, long ranking, long drawEventId) {
        var dto = new DrawEventWinningInfoBulkInsertDto();
        dto.eventUserId = eventUserId;
        dto.ranking = ranking;
        dto.drawEventId = drawEventId;
        return dto;
    }
}
