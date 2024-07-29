package hyundai.softeer.orange.admin.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Table(name="admin_user")
@Getter
@Entity
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 어드민 유저가 로그인할 때 사용하는 아이디 정보
     */
    @Column
    private String username;

    /**
     * 어드민 유저의 이름 ex) ~ 님 안녕하세요!
     */
    @Column
    private String nickname;

    @Column
    private String password;
}
