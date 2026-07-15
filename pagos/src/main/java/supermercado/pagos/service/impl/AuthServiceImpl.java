package supermercado.pagos.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supermercado.pagos.model.Role;
import supermercado.pagos.model.User;
import supermercado.pagos.dto.LoginRequest;
import supermercado.pagos.dto.RegisterRequest;
import supermercado.pagos.repository.UserRepository;
import supermercado.pagos.service.AuthService;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPass(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        if (Boolean.FALSE.equals(user.getActivo())) {
            throw new RuntimeException("El usuario está inactivo");
        }

        return user;
    }

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está registrado");
        }

        User nuevoUsuario = User.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPass()))
                .role(Role.CLIENT) // Rol por defecto para todo registro público
                .activo(true)
                .build();

        return userRepository.save(nuevoUsuario);
    }
}
