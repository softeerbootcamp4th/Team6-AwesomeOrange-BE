package hyundai.softeer.orange.event.common.controller;

import hyundai.softeer.orange.core.auth.Auth;
import hyundai.softeer.orange.core.auth.AuthRole;
import hyundai.softeer.orange.event.common.service.EventService;
import hyundai.softeer.orange.event.dto.BriefEventDto;
import hyundai.softeer.orange.event.dto.EventDto;
import hyundai.softeer.orange.event.dto.EventFrameCreateRequest;
import hyundai.softeer.orange.event.dto.group.EventEditGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 이벤트 관련 CRUD를 다루는 API
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
@RestController
public class EventController {
    private final EventService eventService;
    /**
     *
     * @param search 검색어
     * @param sort 정렬 기준. (eventId|name|startTime|endTime|eventType)(:(asc|desc))? 패턴이 ,로 나뉘는 형태. ex) eventId,name:asc,startTime:desc
     * @param page 페이지 번호
     * @param size 한번에 검색하는 이벤트 개수
     * @return 요청한 이벤트 리스트
     */
    @Auth({AuthRole.admin})
    @GetMapping
    @Operation(summary = "이벤트 리스트 획득", description = "관리자가 이벤트 목록을 검색한다. 검색어, sort 기준 등을 정의할 수 있다.", responses = {
            @ApiResponse(responseCode = "200", description = "성공적으로 이벤트 목록을 반환한다"),
            @ApiResponse(responseCode = "5xx", description = "서버 내부적 에러"),
            @ApiResponse(responseCode = "4xx", description = "클라이언트 에러 (보통 page / size 값을 잘못 지정. 숫자가 아닌 경우 등) ")
    })
    public ResponseEntity<List<BriefEventDto>> getEvents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        List<BriefEventDto> events = eventService.searchEvents(search, sort, page, size);
        return ResponseEntity.ok(events);
    }

    @Auth({AuthRole.admin})
    @PostMapping
    @Operation(summary = "이벤트 생성", description = "관리자가 이벤트를 새롭게 등록한다", responses = {
            @ApiResponse(responseCode = "201", description = "이벤트 생성 성공"),
            @ApiResponse(responseCode = "4xx", description = "유저 측 실수로 이벤트 생성 실패")
    })
    public ResponseEntity<Void> createEvent(@Validated @RequestBody EventDto eventDto) {
        eventService.createEvent(eventDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     *
     * @param eventId 이벤트 ID. HD000000~로 시작하는 그것
     * @return 해당 이벤트에 대한 정보
     */
    @Auth({AuthRole.admin})
    @GetMapping("/edit")
    @Operation(summary = "이벤트 데이터 획득", description = "이벤트 초기 정보를 받는다", responses = {
            @ApiResponse(responseCode = "200", description = "이벤트 정보를 정상적으로 받음"),
            @ApiResponse(responseCode = "404", description = "대응되는 이벤트가 존재하지 않음")
    })
    public ResponseEntity<EventDto> getEventEditData(
            @RequestParam("eventId") String eventId
    ) {
        EventDto eventInfo = eventService.getEventInfo(eventId);
        return ResponseEntity.ok(eventInfo);
    }

    /**
     * @param eventDto 수정된 이벤트 정보
     */
    @Auth({AuthRole.admin})
    @PostMapping("/edit")
    @Operation(summary = "이벤트 수정", description = "관리자가 이벤트를 수정한다", responses = {
            @ApiResponse(responseCode = "200", description = "이벤트 생성 성공"),
            @ApiResponse(responseCode = "4xx", description = "유저 측 실수로 이벤트 생성 실패")
    })
    public ResponseEntity<Void> editEvent(
            @Validated({EventEditGroup.class}) @RequestBody EventDto eventDto) {
        eventService.editEvent(eventDto);
        return ResponseEntity.ok().build();
    }

    /**
     * @param req 이벤트 프레임 생성을 위한 json
     */
    @Auth({AuthRole.admin})
    @PostMapping("/frame")
    @Operation(summary = "이벤트 프레임 생성", description = "관리자가 이벤트 프레임을 새롭게 등록한다", responses = {
            @ApiResponse(responseCode = "201", description = "이벤트 프레임 생성 성공"),
            @ApiResponse(responseCode = "4xx", description = "이벤트 프레임 생성 실패")
    })
    public ResponseEntity<Void> createEventFrame(@Valid @RequestBody EventFrameCreateRequest req) {
        eventService.createEventFrame(req.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
