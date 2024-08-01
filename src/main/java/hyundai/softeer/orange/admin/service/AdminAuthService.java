package hyundai.softeer.orange.admin.service;

import hyundai.softeer.orange.admin.entity.Admin;
import hyundai.softeer.orange.admin.exception.AdminException;
import hyundai.softeer.orange.admin.repository.AdminRepository;
import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.core.auth.AuthRole;
import hyundai.softeer.orange.core.jwt.JWTManager;
import hyundai.softeer.orange.core.security.PasswordManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminAuthService {
    private final AdminRepository adminRepository;
    private final PasswordManager pwManager;
    private final JWTManager jwtManager;

    /**
     * 관리자 유저가 로그인하는 메서드. 로그인 성공 시 JWT 토큰(문자열) 을 반환한다.
     * @param username 관리자 ID
     * @param password 관리자 비밀번호
     * @return 관리자 유저의 JWT 토큰
     */
    public String signIn(String username, String password) {
        Optional<Admin> adminOptional = adminRepository.findFirstByUserName(username);
        if(adminOptional.isEmpty()) throw new AdminException(ErrorCode.AUTHENTICATION_FAILED);

        Admin admin = adminOptional.get();
        String beforePassword = admin.getPassword();
        boolean loginSuccess = pwManager.verify(password, beforePassword);
        if(!loginSuccess) throw new AdminException(ErrorCode.AUTHENTICATION_FAILED);

        // dto를 넣도록 수정하기.
        return jwtManager.generateToken("admin", Map.of("admin", admin,"role", AuthRole.admin), 5);
    }

    /**
     * 관리자 유저를 생성하는 메서드.
     * @param userName 관리자 ID
     * @param password 관리자 비밀번호
     * @param nickname 관리자의 닉네임
     */
    public void signUp(String userName, String password, String nickname) {
        boolean exists = adminRepository.existsByUserName(userName);
        if(exists) throw new AdminException(ErrorCode.ADMIN_USER_ALREADY_EXISTS);

        String encryptedPassword = pwManager.encrypt(password);

        Admin admin = Admin.builder()
                .userName(userName)
                .password(encryptedPassword)
                .nickName(nickname)
                .build();

        adminRepository.save(admin);
    }
}
