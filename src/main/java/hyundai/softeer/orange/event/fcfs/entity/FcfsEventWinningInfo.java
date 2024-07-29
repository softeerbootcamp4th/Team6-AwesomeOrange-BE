package hyundai.softeer.orange.event.fcfs.entity;

import hyundai.softeer.orange.eventuser.entity.EventUser;
import jakarta.persistence.*;
import lombok.Getter;

@Table(name="fcfs_event_winning_info")
@Getter
@Entity
public class FcfsEventWinningInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fcfs_event_id")
    private FcfsEvent fcfsEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="event_user_id")
    private EventUser eventUser;
}
