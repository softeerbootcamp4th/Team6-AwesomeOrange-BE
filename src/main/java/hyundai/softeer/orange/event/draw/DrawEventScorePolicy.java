package hyundai.softeer.orange.event.draw;

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
