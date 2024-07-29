package hyundai.softeer.orange.event.draw.entity;

import hyundai.softeer.orange.event.draw.enums.DrawEventAction;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class DrawEventScorePolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private DrawEventAction action;


    @Column
    private Integer score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draw_event_id")
    private DrawEvent drawEvent;
}
