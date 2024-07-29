package hyundai.softeer.orange.event.common.entity;

import hyundai.softeer.orange.comment.entity.Comment;
import hyundai.softeer.orange.eventuser.entity.EventUser;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Table(name="event_frame")
@Entity
@Getter
public class EventFrame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @OneToMany(mappedBy="eventFrame")
    private List<EventMetadata> eventMetadataList = new ArrayList<>();

    @OneToMany(mappedBy="eventFrame")
    private List<EventUser> eventUserList = new ArrayList<>();

    @OneToMany(mappedBy="eventFrame")
    private List<Comment> commentList = new ArrayList<>();
}
