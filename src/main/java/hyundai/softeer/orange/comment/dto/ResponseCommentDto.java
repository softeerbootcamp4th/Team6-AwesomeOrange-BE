package hyundai.softeer.orange.comment.dto;

import hyundai.softeer.orange.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ResponseCommentDto {

    private Long id;
    private String content;
    private String userName;
    private String createdAt;

    public static ResponseCommentDto from(Comment comment) {
        return ResponseCommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userName(comment.getEventUser().getUserName())
                .createdAt(comment.getCreatedAt().toString())
                .build();
    }
}
