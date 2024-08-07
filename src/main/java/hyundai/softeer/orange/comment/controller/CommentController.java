package hyundai.softeer.orange.comment.controller;

import hyundai.softeer.orange.comment.dto.CreateCommentDto;
import hyundai.softeer.orange.comment.dto.ResponseCommentsDto;
import hyundai.softeer.orange.comment.service.ApiService;
import hyundai.softeer.orange.comment.service.CommentService;
import hyundai.softeer.orange.common.ErrorResponse;
import hyundai.softeer.orange.core.auth.Auth;
import hyundai.softeer.orange.core.auth.AuthRole;
import hyundai.softeer.orange.eventuser.component.EventUserAnnotation;
import hyundai.softeer.orange.eventuser.dto.EventUserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment", description = "기대평 관련 API")
@ConfigurationPropertiesScan
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
@RestController
public class CommentController {

    private final CommentService commentService;
    private final ApiService apiService;

    @Tag(name = "Comment")
    @GetMapping
    @Operation(summary = "기대평 조회", description = "주기적으로 추출되는 긍정 기대평 목록을 조회한다.", responses = {
            @ApiResponse(responseCode = "200", description = "기대평 조회 성공",
                    content = @Content(schema = @Schema(implementation = ResponseCommentsDto.class)))
    })
    public ResponseEntity<ResponseCommentsDto> getComments() {
        return ResponseEntity.ok(commentService.getComments());
    }

    @Auth(AuthRole.event_user)
    @Tag(name = "Comment")
    @PostMapping
    @Operation(summary = "기대평 등록", description = "유저가 신규 기대평을 등록한다.", responses = {
            @ApiResponse(responseCode = "200", description = "기대평 등록 성공",
                    content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "기대평 등록 실패, 지나치게 부정적인 표현으로 간주될 때",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 정보를 갖는 유저나 이벤트가 존재하지 않을 때",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "하루에 여러 번의 기대평을 작성하려 할 때",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Boolean> createComment(@EventUserAnnotation EventUserInfo userInfo, @RequestBody @Valid CreateCommentDto dto) {
        boolean isPositive = apiService.analyzeComment(dto.getContent());
        return ResponseEntity.ok(commentService.createComment(userInfo.getUserId(), dto, isPositive));
    }

    @Auth(AuthRole.event_user)
    @Tag(name = "Comment")
    @GetMapping("/info")
    @Operation(summary = "기대평 등록 가능 여부 조회", description = "오늘 기대평 등록 가능 여부를 조회한다.", responses = {
            @ApiResponse(responseCode = "200", description = "기대평 작성 가능 여부를 true/false로 반환한다.",
                    content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "404", description = "해당 정보를 갖는 유저가 존재하지 않을 때",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Boolean> isCommentable(@EventUserAnnotation EventUserInfo userInfo) {
        return ResponseEntity.ok(commentService.isCommentable(userInfo.getUserId()));
    }
}
