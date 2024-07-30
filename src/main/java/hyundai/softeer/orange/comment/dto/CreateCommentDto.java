package hyundai.softeer.orange.comment.dto;

import hyundai.softeer.orange.common.util.MessageUtil;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
public class CreateCommentDto {

    @NotNull(message = MessageUtil.BAD_INPUT)
    private Long eventUserId;

    @NotNull(message = MessageUtil.BAD_INPUT)
    private Long eventFrameId;

    @Size(min = 1, max = 100, message = MessageUtil.OUT_OF_SIZE)
    private String content;

    public CreateCommentDto(Long eventUserId, Long eventFrameId, String content) {
        this.eventUserId = eventUserId;
        this.eventFrameId = eventFrameId;
        this.content = content;
    }
}
