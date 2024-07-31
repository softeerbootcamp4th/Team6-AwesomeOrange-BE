package hyundai.softeer.orange.comment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ResponseCommentsDto {

    private List<ResponseCommentDto> comments;

    public ResponseCommentsDto(List<ResponseCommentDto> comments) {
        this.comments = comments;
    }
}
