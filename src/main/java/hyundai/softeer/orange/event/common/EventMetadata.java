package hyundai.softeer.orange.event.common;

import hyundai.softeer.orange.event.draw.DrawEvent;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "event_metadata")
@Entity
public class EventMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String eventId;

    @Column(length=80)
    private String name;

    @Column(length = 200)
    private String description;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column
    private EventType eventType;

    @Column
    private EventStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_frame_id")
    private EventFrame eventFrame;

    @OneToMany(mappedBy = "eventMetadata")
    private List<DrawEvent> drawEventList = new ArrayList<>();
}
