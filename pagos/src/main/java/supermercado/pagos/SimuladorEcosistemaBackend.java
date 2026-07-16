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
import supermercado.pagos.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n===  INICIANDO SIMULACIÓN COMPLETA DEL BACKEND ===");

        // 1. Configuración del escenario (Catálogo de productos)
        Category categoriaAbarrotes = crearCategoriaSujeto();
        Product arroz = crearProductoSujeto("Arroz Integral", new BigDecimal("4.50"), 50, categoriaAbarrotes);
        Product leche = crearProductoSujeto("Leche Entera 1L", new BigDecimal("3.20"), 100, categoriaAbarrotes);
        Product azucar = crearProductoSujeto("Azúcar Rubia 1Kg", new BigDecimal("3.80"), 80, categoriaAbarrotes);
        Product sal = crearProductoSujeto("Sal de Mesa 1Kg", new BigDecimal("1.50"), 120, categoriaAbarrotes);
        Product fideos = crearProductoSujeto("Fideos Spaghetti 500g", new BigDecimal("2.20"), 90, categoriaAbarrotes);
        Product aceite = crearProductoSujeto("Aceite Vegetal 1L", new BigDecimal("7.50"), 60, categoriaAbarrotes);
        Product atun = crearProductoSujeto("Atún en Lata", new BigDecimal("4.00"), 70, categoriaAbarrotes);
        Product lentejas = crearProductoSujeto("Lentejas 500g", new BigDecimal("2.80"), 85, categoriaAbarrotes);
        Product frejoles = crearProductoSujeto("Frejoles Canarios 500g", new BigDecimal("3.50"), 75, categoriaAbarrotes);
        Product harina = crearProductoSujeto("Harina de Trigo 1Kg", new BigDecimal("3.00"), 65, categoriaAbarrotes);
        Product avena = crearProductoSujeto("Avena en Hojuelas 500g", new BigDecimal("2.60"), 50, categoriaAbarrotes);
        Product galletas = crearProductoSujeto("Galletas Soda", new BigDecimal("1.80"), 110, categoriaAbarrotes);

        Product yogurt = crearProductoSujeto("Yogurt Natural 1L", new BigDecimal("5.20"), 40, categoriaAbarrotes);
        Product queso = crearProductoSujeto("Queso Fresco 500g", new BigDecimal("8.50"), 35, categoriaAbarrotes);
        Product mantequilla = crearProductoSujeto("Mantequilla 200g", new BigDecimal("4.90"), 45, categoriaAbarrotes);
        Product jamon = crearProductoSujeto("Jamón de Pavo 250g", new BigDecimal("6.70"), 30, categoriaAbarrotes);
        Product pollo = crearProductoSujeto("Pollo Entero", new BigDecimal("12.00"), 25, categoriaAbarrotes);

        Product manzana = crearProductoSujeto("Manzana Roja 1Kg", new BigDecimal("4.00"), 100, categoriaAbarrotes);
        Product platano = crearProductoSujeto("Plátano 1Kg", new BigDecimal("3.20"), 90, categoriaAbarrotes);
        Product naranja = crearProductoSujeto("Naranja 1Kg", new BigDecimal("2.80"), 85, categoriaAbarrotes);
        Product papa = crearProductoSujeto("Papa Blanca 1Kg", new BigDecimal("2.50"), 120, categoriaAbarrotes);
        Product zanahoria = crearProductoSujeto("Zanahoria 1Kg", new BigDecimal("2.20"), 110, categoriaAbarrotes);

        Product tomate = crearProductoSujeto("Tomate 1Kg", new BigDecimal("3.00"), 95, categoriaAbarrotes);
        Product cebolla = crearProductoSujeto("Cebolla Roja 1Kg", new BigDecimal("2.70"), 100, categoriaAbarrotes);
        Product ajo = crearProductoSujeto("Ajo 250g", new BigDecimal("1.90"), 60, categoriaAbarrotes);
        Product limon = crearProductoSujeto("Limón 1Kg", new BigDecimal("3.50"), 80, categoriaAbarrotes);
        Product palta = crearProductoSujeto("Palta 1Kg", new BigDecimal("6.00"), 50, categoriaAbarrotes);

        Product agua = crearProductoSujeto("Agua Mineral 1.5L", new BigDecimal("2.00"), 150, categoriaAbarrotes);
        Product gaseosa = crearProductoSujeto("Gaseosa Cola 2L", new BigDecimal("5.50"), 130, categoriaAbarrotes);
        Product jugo = crearProductoSujeto("Jugo de Naranja 1L", new BigDecimal("4.20"), 70, categoriaAbarrotes);
        Product cafe = crearProductoSujeto("Café Instantáneo 200g", new BigDecimal("9.50"), 40, categoriaAbarrotes);
        Product te = crearProductoSujeto("Té en Bolsitas", new BigDecimal("3.30"), 60, categoriaAbarrotes);

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
     * FUNCIÓN 2b: Registro y Autenticación de un Administrador
     * (Solo para pruebas/seed local — nunca expongas esto como endpoint público)
     */
    private User simularRegistroYLoginAdmin() {
        System.out.println("\n[AUTH] Registrando nuevo administrador...");
        RegisterRequest reg = new RegisterRequest();
        reg.setNombre("Said Admin");
        reg.setEmail("admin@said.com");
        reg.setPass("SAIDcarlos12#");

        User usuarioRegistrado = authService.register(reg); // se crea como CLIENT por defecto
        System.out.println("[AUTH] Usuario creado con ID: " + usuarioRegistrado.getId());

        // Ascenso manual a ADMIN — el registro público NUNCA debe permitir esto directamente
        usuarioRegistrado.setRole(Role.ADMIN);
        usuarioRegistrado = userRepository.save(usuarioRegistrado);
        System.out.println("[AUTH] Usuario ascendido a rol: " + usuarioRegistrado.getRole());

        System.out.println("[AUTH] Intentando login con credenciales...");
        LoginRequest login = new LoginRequest();
        login.setEmail("admin@said.com");
        login.setPass("SAIDcarlos12#");

        User usuarioAutenticado = authService.login(login);
        System.out.println("[AUTH] Token de sesión simulado para: " + usuarioAutenticado.getNombre()
                + " (rol: " + usuarioAutenticado.getRole() + ")");

        return usuarioAutenticado;
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