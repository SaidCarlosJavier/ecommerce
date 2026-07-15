package supermercado.pagos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nombre;

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    @JsonIgnore // FIX: nunca se debe enviar el hash de la contraseña al frontend
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean activo;

    @JsonIgnore // FIX: evita el ciclo infinito User -> transactions -> user -> transactions...
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Transaction> transactions;
}
