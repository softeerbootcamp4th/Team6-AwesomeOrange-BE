package hyundai.softeer.orange.event.draw;

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
    private Long rank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draw_event_id")
    private DrawEvent drawEvent;
}
