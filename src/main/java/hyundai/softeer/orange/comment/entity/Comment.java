package hyundai.softeer.orange.comment.entity;

import hyundai.softeer.orange.event.common.entity.EventFrame;
import hyundai.softeer.orange.eventuser.entity.EventUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Table(name="comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String content;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_frame_id")
    private EventFrame eventFrame;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_user_id")
    private EventUser eventUser;

    // 긍정 기대평 여부
    private Boolean isPositive;

    public static Comment of(String content, EventFrame eventFrame, EventUser eventUser, Boolean isPositive) {
        Comment comment = new Comment();
        comment.content = content;
        comment.eventFrame = eventFrame;
        comment.eventUser = eventUser;
        comment.createdAt = LocalDateTime.now(); // FIXME: 현재 테스트 시 자동 생성되지 않고 있음
        comment.isPositive = isPositive;
        return comment;
    }
}
