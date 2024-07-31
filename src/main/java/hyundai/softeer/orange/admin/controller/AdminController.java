package hyundai.softeer.orange.admin.controller;

 import hyundai.softeer.orange.admin.dto.AdminSignInRequest;
 import hyundai.softeer.orange.admin.dto.AdminSignupRequest;
 import hyundai.softeer.orange.admin.service.AdminAuthService;
 import hyundai.softeer.orange.common.dto.TokenDto;
 import jakarta.validation.Valid;
 import lombok.RequiredArgsConstructor;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin")
@RequiredArgsConstructor
@RestController
public class AdminController {
    private final AdminAuthService adminAuthService;

    @PostMapping("/auth/signin")
    public TokenDto signIn(@Valid @RequestBody AdminSignInRequest dto) {
        String userToken = adminAuthService.signIn(dto.getUsername(), dto.getPassword());
        return new TokenDto(userToken);
    }


    @PostMapping("/auth/signup")
    public ResponseEntity<Object> signUp(@Valid @RequestBody AdminSignupRequest dto) {
        try {
            adminAuthService.signUp(dto.getUsername(), dto.getPassword(), dto.getNickname());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            // user already exist
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

    }
}
