package hyundai.softeer.orange.event.controller;

import hyundai.softeer.orange.core.auth.Auth;
import hyundai.softeer.orange.core.auth.AuthRole;
import hyundai.softeer.orange.event.dto.EventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/event")
@RestController
public class EventController {
    @Auth({AuthRole.admin})
    @PostMapping
    public ResponseEntity<?> createEvent(@Validated @RequestBody EventDto eventDto) {

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
