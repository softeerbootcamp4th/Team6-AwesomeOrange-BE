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

    public void updateGrade(Long grade) {
        this.grade = grade;
    }

    public void updateCount(Long count) {
        this.count = count;
    }

    public void updatePrizeInfo(String prizeInfo) {
        this.prizeInfo = prizeInfo;
    }

    public static DrawEventMetadata of(Long grade, Long count, String prizeInfo, DrawEvent drawEvent) {
        DrawEventMetadata metadata = new DrawEventMetadata();
        metadata.grade = grade;
        metadata.count = count;
        metadata.prizeInfo = prizeInfo;
        metadata.drawEvent = drawEvent;
        return metadata;
    }
}
