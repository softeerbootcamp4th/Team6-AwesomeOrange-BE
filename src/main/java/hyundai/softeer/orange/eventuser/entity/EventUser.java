package hyundai.softeer.orange.eventuser.entity;

import hyundai.softeer.orange.comment.entity.Comment;
import hyundai.softeer.orange.event.common.entity.EventFrame;
import hyundai.softeer.orange.event.draw.entity.DrawEventWinningInfo;
import hyundai.softeer.orange.event.fcfs.entity.FcfsEventWinningInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name="event_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class EventUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column
    private String phoneNumber;

    @Column
    private Integer score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_frame_id")
    private EventFrame eventFrame;

    @OneToMany(mappedBy = "eventUser")
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "eventUser")
    private List<DrawEventWinningInfo> drawEventWinningInfoList = new ArrayList<>();

    @OneToMany(mappedBy = "eventUser")
    private List<FcfsEventWinningInfo> fcfsEventWinningInfoList = new ArrayList<>();

    public static EventUser of(String username, String phoneNumber, EventFrame eventFrame) {
        EventUser eventUser = new EventUser();
        eventUser.username = username;
        eventUser.phoneNumber = phoneNumber;
        eventUser.score = 0;
        eventUser.eventFrame = eventFrame;
        return eventUser;
    }
}
