package hyundai.softeer.orange.event.draw;

import hyundai.softeer.orange.event.common.EventMetadata;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Table(name="draw_event")
@Getter
@Entity
public class DrawEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_metadata_id")
    private EventMetadata eventMetadata;

    @OneToMany(mappedBy ="draw_event")
    private List<DrawEventScorePolicy> policyList = new ArrayList<>();
    @OneToMany(mappedBy ="draw_event")
    private List<DrawEventMetadata> metadataList = new ArrayList<>();
    @OneToMany(mappedBy ="draw_event")
    private List<DrawEventWinningInfo> winningInfoList = new ArrayList<>();
}
