package supermercado.pagos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotNull
    private String nombre;

    @Email
    @NotNull
    private String email;

    @NotNull
    private String pass; // Coincide con el JS
}