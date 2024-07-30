package hyundai.softeer.orange.admin.controller;

import hyundai.softeer.orange.admin.dto.AdminSignInDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin/auth")
@RestController
public class AdminAuthController {
    @PostMapping
    public String signIn(@Valid @RequestBody AdminSignInDto dto) {
        return "success";
    }
}
