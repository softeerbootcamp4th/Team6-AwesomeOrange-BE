package hyundai.softeer.orange.event.controller;

import hyundai.softeer.orange.admin.component.AdminAnnotation;
import hyundai.softeer.orange.admin.entity.Admin;
import hyundai.softeer.orange.core.auth.Auth;
import hyundai.softeer.orange.core.auth.AuthRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/event")
@RestController
public class EventController {
    @Auth({AuthRole.admin})
    @PostMapping
    public ResponseEntity<?> createEvent(@AdminAnnotation Admin admin) {
        log.info("admin: {}", admin);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
