package hyundai.softeer.orange.admin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name="admin_user")
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 어드민 유저가 로그인할 때 사용하는 아이디 정보
     */
    @Column
    private String userName;

    /**
     * 어드민 유저의 이름 ex) ~ 님 안녕하세요!
     */
    @Column
    private String nickName;

    @Column
    private String password;
}
