package hyundai.softeer.orange.eventuser.dto;

import hyundai.softeer.orange.common.util.ConstantUtil;
import hyundai.softeer.orange.common.util.MessageUtil;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RequestAuthCodeDto {

    @NotNull(message = MessageUtil.BAD_INPUT)
    private String name;

    @NotNull(message = MessageUtil.BAD_INPUT)
    @Pattern(regexp = ConstantUtil.PHONE_NUMBER_REGEX, message = MessageUtil.INVALID_PHONE_NUMBER)
    private String phoneNumber;

    @NotNull
    @Pattern(regexp = ConstantUtil.AUTH_CODE_REGEX, message = MessageUtil.INVALID_AUTH_CODE)
    private String authCode;

    @NotNull(message = MessageUtil.BAD_INPUT)
    private Long eventFrameId;
}
