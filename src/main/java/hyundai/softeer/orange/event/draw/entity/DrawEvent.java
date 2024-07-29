package hyundai.softeer.orange.event.draw.entity;

import hyundai.softeer.orange.event.common.entity.EventMetadata;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_metadata_id")
    private EventMetadata eventMetadata;

    @OneToMany(mappedBy ="drawEvent")
    private List<DrawEventScorePolicy> policyList = new ArrayList<>();

    @OneToMany(mappedBy ="drawEvent")
    private List<DrawEventMetadata> metadataList = new ArrayList<>();

    @OneToMany(mappedBy ="drawEvent")
    private List<DrawEventWinningInfo> winningInfoList = new ArrayList<>();
}
