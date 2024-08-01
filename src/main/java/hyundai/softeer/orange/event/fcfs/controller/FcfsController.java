package hyundai.softeer.orange.event.fcfs.controller;

import hyundai.softeer.orange.event.fcfs.service.FcfsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "fcfs", description = "선착순 이벤트 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1/event/fcfs")
@RestController
public class FcfsController {

    private final FcfsService fcfsService;

    @Tag(name = "fcfs")
    @PostMapping
    @Operation(summary = "선착순 이벤트 참여", description = "선착순 이벤트에 참여한 결과(boolean)를 반환한다.", responses = {
            @ApiResponse(responseCode = "200", description = "선착순 이벤트 당첨 성공 혹은 실패"),
            @ApiResponse(responseCode = "400", description = "선착순 이벤트 시간이 아니거나, 요청 형식이 잘못된 경우"),
    })
    public ResponseEntity<Boolean> participate(@RequestParam Long eventSequence, @RequestParam String userId) {
        return ResponseEntity.ok(fcfsService.participate(eventSequence, userId));
    }
}
