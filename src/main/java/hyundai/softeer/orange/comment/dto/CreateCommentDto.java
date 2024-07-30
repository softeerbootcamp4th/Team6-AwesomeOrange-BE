package hyundai.softeer.orange.comment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCommentDto {

    @NotNull(message = "잘못된 값이 입력되었거나 값이 누락되었습니다.")
    private Long eventUserId;

    @NotNull(message = "잘못된 값이 입력되었거나 값이 누락되었습니다.")
    private Long eventFrameId;

    @Size(min = 1, max = 100, message = "정해진 크기를 벗어났습니다.")
    private String content;
}
