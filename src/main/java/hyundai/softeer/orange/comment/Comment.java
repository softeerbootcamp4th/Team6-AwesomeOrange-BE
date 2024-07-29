package hyundai.softeer.orange.comment;

import hyundai.softeer.orange.event.common.EventFrame;
import hyundai.softeer.orange.eventuser.entity.EventUser;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Table(name="comment")
@Getter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
}
