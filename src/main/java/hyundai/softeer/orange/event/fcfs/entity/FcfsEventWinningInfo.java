package hyundai.softeer.orange.event.fcfs.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Table(name="fcfs_event_winning_info")
@Getter
@Entity
public class FcfsEventWinningInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fcfs_event_id")
    private FcfsEvent fcfsEvent;
}
