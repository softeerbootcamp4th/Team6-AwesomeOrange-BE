package hyundai.softeer.orange.eventuser.service;

import hyundai.softeer.orange.common.util.ConstantUtil;
import hyundai.softeer.orange.eventuser.config.CoolSmsApiConfig;
import hyundai.softeer.orange.eventuser.dto.RequestUserDto;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class CoolSmsService implements SmsService {

    private final DefaultMessageService defaultMessageService;
    private final CoolSmsApiConfig coolSmsApiConfig;
    private final StringRedisTemplate stringRedisTemplate;

    public CoolSmsService(CoolSmsApiConfig coolSmsApiConfig, StringRedisTemplate stringRedisTemplate) {
        this.defaultMessageService = NurigoApp.INSTANCE.initialize(coolSmsApiConfig.getApiKey(), coolSmsApiConfig.getApiSecret(), coolSmsApiConfig.getUrl());
        this.coolSmsApiConfig = coolSmsApiConfig;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void sendSms(RequestUserDto dto) {
        String authCode = generateAuthCode();
        Message message = new Message();
        message.setFrom(coolSmsApiConfig.getFrom());
        message.setTo(dto.getPhoneNumber());
        message.setText("[소프티어 오렌지] 인증번호는 (" + authCode + ")입니다.");

        SingleMessageSentResponse response = defaultMessageService.sendOne(new SingleMessageSendingRequest(message));
        log.info("{}에게 SMS 전송 완료: {}", dto.getPhoneNumber(), response);
        stringRedisTemplate.opsForValue().set(dto.getPhoneNumber(), authCode);
    }

    // 6자리 난수 인증번호 생성
    String generateAuthCode() {
        return String.format(ConstantUtil.AUTH_CODE_CREATE_REGEX, new Random().nextInt(1000000));
    }
}
