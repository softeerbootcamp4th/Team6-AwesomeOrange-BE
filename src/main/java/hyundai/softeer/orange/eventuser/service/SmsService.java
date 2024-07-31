package hyundai.softeer.orange.eventuser.service;

import hyundai.softeer.orange.eventuser.dto.RequestUserDto;

public interface SmsService {
    /**
     * 1. 6자리 난수 인증번호를 생성한 뒤, 전화번호로 인증번호 전송
     * 2. 생성된 인증번호는 <유저전화번호-인증번호> 형태로 Redis에 저장
     */
    void sendSms(RequestUserDto dto);
}
