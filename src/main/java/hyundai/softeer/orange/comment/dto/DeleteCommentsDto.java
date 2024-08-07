package hyundai.softeer.orange.comment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class DeleteCommentsDto {
    @NotNull
    private List<Long> commentIds;

    public DeleteCommentsDto(List<Long> commentIds) {
        this.commentIds = commentIds;
    }
}
