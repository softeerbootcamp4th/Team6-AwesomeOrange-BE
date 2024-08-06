package hyundai.softeer.orange.eventuser.service;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.common.dto.TokenDto;
import hyundai.softeer.orange.common.util.ConstantUtil;
import hyundai.softeer.orange.core.jwt.JWTManager;
import hyundai.softeer.orange.event.common.entity.EventFrame;
import hyundai.softeer.orange.event.common.repository.EventFrameRepository;
import hyundai.softeer.orange.eventuser.dto.RequestAuthCodeDto;
import hyundai.softeer.orange.eventuser.dto.RequestUserDto;
import hyundai.softeer.orange.eventuser.entity.EventUser;
import hyundai.softeer.orange.eventuser.exception.EventUserException;
import hyundai.softeer.orange.eventuser.repository.EventUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EventUserService {

    private final EventUserRepository eventUserRepository;
    private final EventFrameRepository eventFrameRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final JWTManager jwtManager;

    /**
     * 1. dto 데이터로 DB 조회하여 이미 존재하면 로그인 처리
     * 2. 존재하지 않으면 404 예외
     * 3. 유저 uuid 기반 JWT 토큰 발급
     */
    @Transactional(readOnly = true)
    public TokenDto login(RequestUserDto dto) {
        EventUser eventUser = eventUserRepository.findByUserNameAndPhoneNumber(dto.getName(), dto.getPhoneNumber())
                .orElseThrow(() -> new EventUserException(ErrorCode.EVENT_USER_NOT_FOUND));

        Map<String, Object> claims = new HashMap<>(Map.of(ConstantUtil.CLAIMS_KEY, eventUser.getUserId()));
        String token = jwtManager.generateToken(eventUser.getUserName(), claims, 1);
        return new TokenDto(token);
    }

    /**
     * 1. 유저가 입력한 인증번호와 Redis에 저장된 인증번호 비교
     * 2. 일치하면 신규 유저 저장하고 JWT 토큰 발급
     * 3. 불일치하면 401 예외
     * 4. 전화번호로 발송된 인증번호가 존재하지 않는다면 400 예외
     */
    @Transactional
    public TokenDto checkAuthCode(RequestAuthCodeDto dto, Long eventFrameId) {
        // Redis에서 인증번호 조회
        String authCode = stringRedisTemplate.opsForValue().get(dto.getPhoneNumber());
        if(authCode == null) {
            throw new EventUserException(ErrorCode.BAD_REQUEST);
        }

        if (!authCode.equals(dto.getAuthCode())) {
            throw new EventUserException(ErrorCode.INVALID_AUTH_CODE);
        }

        // Redis에 저장된 인증번호 삭제
        stringRedisTemplate.delete(dto.getPhoneNumber());

        // DB에 유저 데이터 저장
        EventFrame eventFrame = eventFrameRepository.findById(eventFrameId)
                .orElseThrow(() -> new EventUserException(ErrorCode.EVENT_FRAME_NOT_FOUND));
        String userId = UUID.randomUUID().toString().substring(0, 8);
        EventUser eventUser = EventUser.of(dto.getName(), dto.getPhoneNumber(), eventFrame, userId);
        eventUserRepository.save(eventUser);
        return generateToken(eventUser);
    }

    // JWT 토큰 생성
    private TokenDto generateToken(EventUser eventUser) {
        Map<String, Object> claims = new HashMap<>(Map.of(ConstantUtil.CLAIMS_KEY, eventUser.getUserId()));
        String token = jwtManager.generateToken(eventUser.getUserName(), claims, 1);
        return new TokenDto(token);
    }
}
