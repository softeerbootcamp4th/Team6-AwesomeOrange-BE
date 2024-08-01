package hyundai.softeer.orange.event.common.controller;

import hyundai.softeer.orange.core.auth.Auth;
import hyundai.softeer.orange.core.auth.AuthRole;
import hyundai.softeer.orange.event.common.service.EventService;
import hyundai.softeer.orange.event.dto.EventDto;
import hyundai.softeer.orange.event.dto.EventFrameCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
