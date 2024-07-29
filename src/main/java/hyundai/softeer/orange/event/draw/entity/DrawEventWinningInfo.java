package hyundai.softeer.orange.event.draw.entity;

import hyundai.softeer.orange.eventuser.entity.EventUser;
import jakarta.persistence.*;
import lombok.Getter;

@Table(name="draw_event_winning_info")
@Getter
@Entity
public class DrawEventWinningInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long ranking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draw_event_id")
    private DrawEvent drawEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_user_id")
    private EventUser eventUser;

}
