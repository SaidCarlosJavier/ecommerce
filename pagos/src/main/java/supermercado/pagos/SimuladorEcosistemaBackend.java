package supermercado.pagos;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import supermercado.pagos.dto.LoginRequest;
import supermercado.pagos.dto.RegisterRequest;
import supermercado.pagos.dto.CartDto;
import supermercado.pagos.model.*;
import supermercado.pagos.repository.CategoryRepository;
import supermercado.pagos.repository.ProductRepository;
import supermercado.pagos.repository.TransactionRepository;
import supermercado.pagos.service.AuthService;
import supermercado.pagos.service.CartService;
import supermercado.pagos.service.CheckoutFacade;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SimuladorEcosistemaBackend implements CommandLineRunner {

    // Inyección de todas las dependencias del sistema
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;
    private final CartService cartService;
    private final CheckoutFacade checkoutFacade;
    private final TransactionRepository transactionRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n=== 🚀 INICIANDO SIMULACIÓN COMPLETA DEL BACKEND ===");

        // 1. Configuración del escenario (Catálogo de productos)
        Category categoriaAbarrotes = crearCategoriaSujeto();
        Product arroz = crearProductoSujeto("Arroz Integral", new BigDecimal("4.50"), 50, categoriaAbarrotes);
        Product leche = crearProductoSujeto("Leche Entera 1L", new BigDecimal("3.20"), 100, categoriaAbarrotes);

        // 2. Flujo de Autenticación (Fase 6 Frontend)
        User usuarioLogueado = simularRegistroYLogin();

        // 3. Flujo del Carrito de Compras (Fase 3 Frontend)
        simularGestionCarrito(usuarioLogueado.getId(), arroz.getId(), leche.getId());

        // 4. Flujo de Pago mediante la Fachada (Fase 7 Frontend)
        Transaction ticketGenerado = simularProcesoDePago(usuarioLogueado.getId());

        // 5. Flujo de Auditoría y Reportes (Fase 4 y 5 Frontend)
        simularReportesYHistorial(usuarioLogueado.getId());

        System.out.println("=== 🏁 SIMULACIÓN FINALIZADA CON ÉXITO SIN ERRORES ===\n");
    }

    /**
     * FUNCIÓN 1: Inicialización del Catálogo
     */
    private Category crearCategoriaSujeto() {
        Category cat = Category.builder().nombre("Abarrotes").descripcion("Productos básicos").build();
        return categoryRepository.save(cat);
    }

    private Product crearProductoSujeto(String nombre, BigDecimal precio, Integer stock, Category cat) {
        Product prod = Product.builder()
                .nombre(nombre)
                .precio(precio)
                .stock(stock)
                .activo(true)
                .category(cat)
                .build();
        return productRepository.save(prod);
    }

    /**
     * FUNCIÓN 2: Registro y Autenticación de un Cliente
     */
    private User simularRegistroYLogin() {
        System.out.println("\n[AUTH] Registrando nuevo cliente...");
        RegisterRequest reg = new RegisterRequest();
        reg.setNombre("Carlos Said");
        reg.setEmail("carlos@said.com");
        reg.setPass("SAIDcarlos12#");
        User usuarioRegistrado = authService.register(reg);
        System.out.println("[AUTH] Usuario creado con ID: " + usuarioRegistrado.getId());

        System.out.println("[AUTH] Intentando login con credenciales...");
        LoginRequest login = new LoginRequest();
        login.setEmail("carlos@said.com");
        login.setPass("SAIDcarlos12#");
        User usuarioAutenticado = authService.login(login);
        System.out.println("[AUTH] Token de sesión simulado para: " + usuarioAutenticado.getNombre());

        return usuarioAutenticado;
    }

    /**
     * FUNCIÓN 3: Interacción con el Carrito en Memoria
     */
    private void simularGestionCarrito(Long userId, Long idArroz, Long idLeche) {
        System.out.println("\n[CARRITO] Agregando 2 unidades de Arroz e ingresando 3 de Leche...");
        cartService.agregarProducto(userId, idArroz, 2);
        cartService.agregarProducto(userId, idLeche, 3);

        CartDto carritoActual = cartService.obtenerCarrito(userId);
        System.out.println("[CARRITO] Cantidad de ítems diferentes: " + carritoActual.getItems().size());
        System.out.println("[CARRITO] Monto Neto calculado en DTO: $" + carritoActual.getTotalAmount());
    }

    /**
     * FUNCIÓN 4: Ejecución del Patrón Facade para la Transacción
     */
    private Transaction simularProcesoDePago(Long userId) {
        System.out.println("\n[CHECKOUT] Invocando la Fachada de transacciones mediante tarjeta ('CARD')...");
        Transaction ticket = checkoutFacade.realizarPago(userId, "CARD");

        System.out.println("[CHECKOUT] ¡Venta procesada!");
        System.out.println("[CHECKOUT] Número de Ticket emitido: " + ticket.getNumeroTicket());
        System.out.println("[CHECKOUT] Subtotal: $" + ticket.getMontoSubtotal() + " | Total final (+Impuestos): $" + ticket.getMontoTotal());
        return ticket;
    }

    /**
     * FUNCIÓN 5: Extracción de Datos para Vistas Especializadas (User/Admin)
     */
    private void simularReportesYHistorial(Long userId) {
        System.out.println("\n[PERFIL] Extrayendo historial de compras para el cliente...");
        List<Transaction> comprasUsuario = transactionRepository.findByUserIdOrderByFechaDesc(userId);
        comprasUsuario.forEach(c -> System.out.println(" -> Compra detectada: " + c.getNumeroTicket() + " por un total de $" + c.getMontoTotal()));

        System.out.println("\n[ADMIN PANEL] Extrayendo el consolidado global de auditoría de ventas...");
        List<Transaction> todasLasVentas = transactionRepository.findAll();
        System.out.println("[ADMIN PANEL] Total de ventas registradas en el supermercado: " + todasLasVentas.size());
    }
}