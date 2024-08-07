package hyundai.softeer.orange.event.fcfs.controller;

import hyundai.softeer.orange.common.ErrorResponse;
import hyundai.softeer.orange.core.auth.Auth;
import hyundai.softeer.orange.core.auth.AuthRole;
import hyundai.softeer.orange.event.fcfs.dto.ResponseFcfsResultDto;
import hyundai.softeer.orange.event.fcfs.service.FcfsAnswerService;
import hyundai.softeer.orange.event.fcfs.service.FcfsService;
import hyundai.softeer.orange.eventuser.component.EventUserAnnotation;
import hyundai.softeer.orange.eventuser.dto.EventUserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "fcfs", description = "선착순 이벤트 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1/event/fcfs")
@RestController
public class FcfsController {

    private final FcfsService fcfsService;
    private final FcfsAnswerService fcfsAnswerService;

    @Auth(AuthRole.event_user)
    @Tag(name = "fcfs")
    @PostMapping
    @Operation(summary = "선착순 이벤트 참여", description = "선착순 이벤트에 참여한 결과(boolean)를 반환한다.", responses = {
            @ApiResponse(responseCode = "200", description = "선착순 이벤트 당첨 성공 혹은 실패",
                    content = @Content(schema = @Schema(implementation = ResponseFcfsResultDto.class))),
            @ApiResponse(responseCode = "400", description = "선착순 이벤트 시간이 아니거나, 요청 형식이 잘못된 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ResponseFcfsResultDto> participate(@EventUserAnnotation EventUserInfo userInfo, @RequestParam Long eventSequence, @RequestParam String eventAnswer) {
        boolean answerResult = fcfsAnswerService.judgeAnswer(eventSequence, eventAnswer);
        boolean isWin = answerResult && fcfsService.participate(eventSequence, userInfo.getUserId());
        return ResponseEntity.ok(new ResponseFcfsResultDto(answerResult, isWin));
    }
}
