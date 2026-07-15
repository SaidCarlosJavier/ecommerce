package supermercado.pagos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import supermercado.pagos.model.User;
import supermercado.pagos.dto.LoginRequest;
import supermercado.pagos.dto.RegisterRequest;
import supermercado.pagos.security.JwtUtil;
import supermercado.pagos.service.AuthService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = authService.login(request);
            String token = jwtUtil.generateToken(user);
            user.setPassword(null);

            // El frontend debe guardar "token" y usarlo como
            // header: Authorization: Bearer <token> en cada petición protegida.
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("token", token);
            body.put("user", user);

            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request);
            user.setPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
