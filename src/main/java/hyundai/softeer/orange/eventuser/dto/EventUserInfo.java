package hyundai.softeer.orange.eventuser.dto;

import hyundai.softeer.orange.core.auth.AuthRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventUserInfo implements Serializable {

    private String userId;
    private AuthRole role;
}
