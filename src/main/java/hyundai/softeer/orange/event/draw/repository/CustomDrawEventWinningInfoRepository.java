package hyundai.softeer.orange.event.draw.repository;

import hyundai.softeer.orange.event.draw.dto.DrawEventWinningInfoBulkInsertDto;

import java.util.List;

public interface CustomDrawEventWinningInfoRepository {
    void insertMany(List<DrawEventWinningInfoBulkInsertDto> targets);
}
