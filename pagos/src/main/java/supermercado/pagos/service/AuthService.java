package supermercado.pagos.service;

import supermercado.pagos.model.User;
import supermercado.pagos.dto.LoginRequest;
import supermercado.pagos.dto.RegisterRequest;

public interface AuthService {
    User login(LoginRequest request);
    User register(RegisterRequest request);
}