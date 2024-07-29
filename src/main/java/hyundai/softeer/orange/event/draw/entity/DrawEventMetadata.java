package hyundai.softeer.orange.event.draw.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class DrawEventMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long grade;

    @Column
    private Long count;

    @Column
    private String prizeInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draw_event_id")
    private DrawEvent drawEvent;
}
