package hyundai.softeer.orange.event.draw.entity;

import hyundai.softeer.orange.event.common.entity.EventMetadata;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Table(name="draw_event")
@Getter
@Setter
@Entity
public class DrawEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_metadata_id")
    private EventMetadata eventMetadata;

    @OneToMany(mappedBy ="drawEvent",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.EAGER) // 추첨 이벤트는 항상 policy와 함께 사용되므로 EAGER로 설정
    private List<DrawEventScorePolicy> policyList = new ArrayList<>();

    @OneToMany(mappedBy ="drawEvent",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.EAGER) // 추첨 이벤트는 항상 metadata와 함께 사용되므로 EAGER로 설정
    private List<DrawEventMetadata> metadataList = new ArrayList<>();

    @OneToMany(mappedBy ="drawEvent")
    private List<DrawEventWinningInfo> winningInfoList = new ArrayList<>();
}
