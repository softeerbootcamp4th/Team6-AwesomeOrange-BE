package hyundai.softeer.orange.event.common.entity;

import hyundai.softeer.orange.comment.entity.Comment;
import hyundai.softeer.orange.eventuser.entity.EventUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name="event_frame")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class EventFrame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy="eventFrame")
    private List<EventMetadata> eventMetadataList = new ArrayList<>();

    @OneToMany(mappedBy="eventFrame")
    private List<EventUser> eventUserList = new ArrayList<>();

    @OneToMany(mappedBy="eventFrame")
    private List<Comment> commentList = new ArrayList<>();

    public static EventFrame of(String name) {
        EventFrame eventFrame = new EventFrame();
        eventFrame.name = name;
        return eventFrame;
    }
}
