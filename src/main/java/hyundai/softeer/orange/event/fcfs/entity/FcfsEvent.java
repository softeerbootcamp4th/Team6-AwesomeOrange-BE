package hyundai.softeer.orange.event.fcfs.entity;

import hyundai.softeer.orange.event.common.entity.EventMetadata;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name="fcfs_event")
@Getter
@Entity
public class FcfsEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column
    private Long participantCount;

    @Column
    private String prizeInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_metadata_id")
    private EventMetadata eventMetaData;

    @OneToMany(mappedBy = "fcfsEvent")
    private final List<FcfsEventWinningInfo> infos = new ArrayList<>();
}
