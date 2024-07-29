package hyundai.softeer.orange.event.participationinfo;

import hyundai.softeer.orange.event.draw.DrawEvent;
import hyundai.softeer.orange.eventuser.entity.EventUser;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Table(name="event_partication_info")
@Getter
@Entity
public class EventParticipationInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_user_id")
    private EventUser eventUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="draw_event_id")
    private DrawEvent drawEvent;
}
