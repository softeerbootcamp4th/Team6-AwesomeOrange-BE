package hyundai.softeer.orange.eventuser.repository;

import hyundai.softeer.orange.eventuser.dto.EventUserScoreDto;

import java.util.Collection;
import java.util.List;

public interface CustomEventUserRepository {
    void updateScoreMany(List<EventUserScoreDto> dto);
}
