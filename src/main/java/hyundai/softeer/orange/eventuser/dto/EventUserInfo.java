package hyundai.softeer.orange.eventuser.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventUserInfo implements Serializable {

    private String userId;
    private String role;
}
