package hyundai.softeer.orange.event.controller;

import hyundai.softeer.orange.core.auth.Auth;
import hyundai.softeer.orange.core.auth.AuthRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/event")
@RestController
public class EventController {
    @Auth({AuthRole.event_user})
    @PostMapping
    public ResponseEntity<?> createEvent() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
