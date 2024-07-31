package hyundai.softeer.orange.eventuser.dto;

import hyundai.softeer.orange.common.util.ConstantUtil;
import hyundai.softeer.orange.common.util.MessageUtil;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Getter
public class RequestUserDto {

    @NotNull(message = MessageUtil.BAD_INPUT)
    private String name;

    @NotNull(message = MessageUtil.BAD_INPUT)
    @Pattern(regexp = ConstantUtil.PHONE_NUMBER_REGEX, message = MessageUtil.INVALID_PHONE_NUMBER)
    private String phoneNumber;

    public RequestUserDto(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
