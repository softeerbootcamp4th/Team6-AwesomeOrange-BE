package hyundai.softeer.orange.event.common.controller;

import hyundai.softeer.orange.core.auth.Auth;
import hyundai.softeer.orange.core.auth.AuthRole;
import hyundai.softeer.orange.event.common.service.EventService;
import hyundai.softeer.orange.event.dto.EventDto;
import hyundai.softeer.orange.event.dto.EventFrameCreateRequest;
import hyundai.softeer.orange.event.dto.group.EventEditGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 이벤트 관련 CRUD를 다루는 API
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/event")
@RestController
public class EventController {
    private final EventService eventService;

    @Auth({AuthRole.admin})
    @PostMapping
    @Operation(summary = "이벤트 생성", description = "관리자가 이벤트를 새롭게 등록한다", responses = {
            @ApiResponse(responseCode = "201", description = "이벤트 생성 성공"),
            @ApiResponse(responseCode = "4xx", description = "유저 측 실수로 이벤트 생성 실패")
    })
    public ResponseEntity<?> createEvent(@Validated @RequestBody EventDto eventDto) {
        eventService.createEvent(eventDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Auth({AuthRole.admin})
    @GetMapping("/edit")
    @Operation(summary = "이벤트 수정 초기 데이터 획득", description = "이벤트 정보 수정을 위해 초기 정보를 받는다", responses = {
            @ApiResponse(responseCode = "200", description = "이벤트 정보를 정상적으로 받음"),
            @ApiResponse(responseCode = "404", description = "대응되는 이벤트가 존재하지 않음")
    })
    public ResponseEntity<EventDto> getEventEditData(
            @RequestParam("eventId") String eventId
    ) {
        EventDto eventInfo = eventService.getEventInfo(eventId);
        return ResponseEntity.ok(eventInfo);
    }

    @Auth({AuthRole.admin})
    @PostMapping("/edit")
    @Operation(summary = "이벤트 수정", description = "관리자가 이벤트를 수정한다", responses = {
            @ApiResponse(responseCode = "200", description = "이벤트 생성 성공"),
            @ApiResponse(responseCode = "4xx", description = "유저 측 실수로 이벤트 생성 실패")
    })
    public ResponseEntity<?> editEvent(
            @Validated({EventEditGroup.class}) @RequestBody EventDto eventDto
    ) {
        eventService.editEvent(eventDto);
        return ResponseEntity.ok().build();
    }



    @Auth({AuthRole.admin})
    @PostMapping("/frame")
    @Operation(summary = "이벤트 프레임 생성", description = "관리자가 이벤트 프레임을 새롭게 등록한다", responses = {
            @ApiResponse(responseCode = "201", description = "이벤트 프레임 생성 성공"),
            @ApiResponse(responseCode = "4xx", description = "이벤트 프레임 생성 실패")
    })
    public ResponseEntity<?> createEventFrame(@Valid @RequestBody EventFrameCreateRequest req) {
        eventService.createEventFrame(req.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
