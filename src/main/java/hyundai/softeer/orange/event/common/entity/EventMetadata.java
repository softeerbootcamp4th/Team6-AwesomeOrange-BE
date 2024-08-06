package hyundai.softeer.orange.event.common.entity;

import hyundai.softeer.orange.event.common.enums.EventStatus;
import hyundai.softeer.orange.event.common.enums.EventType;
import hyundai.softeer.orange.event.draw.entity.DrawEvent;
import hyundai.softeer.orange.event.fcfs.entity.FcfsEvent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "event_metadata")
@Entity
public class EventMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String eventId;

    @Column(length=40)
    private String name;

    @Column(length = 100)
    private String description;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column
    private EventType eventType;

    @Column
    private String url;

    @Column
    private EventStatus status;

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void updateEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void updateUrl(String url) {
        this.url = url;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_frame_id")
    private EventFrame eventFrame;

    @OneToMany(mappedBy = "eventMetadata", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private final List<DrawEvent> drawEventList = new ArrayList<>();

    public void addDrawEvent(DrawEvent drawEvent) {
        this.drawEventList.add(drawEvent);
    }

    @OneToMany(mappedBy = "eventMetaData", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private final List<FcfsEvent> fcfsEventList = new ArrayList<>();

    public void addFcfsEvents(List<FcfsEvent> fcfsEventList) {
        this.fcfsEventList.addAll(fcfsEventList);
    }

    public void addFcfsEvent(FcfsEvent fcfsEvent) {
        this.fcfsEventList.add(fcfsEvent);
    }
}
