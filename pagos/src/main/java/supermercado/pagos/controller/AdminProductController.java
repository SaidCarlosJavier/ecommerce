package supermercado.pagos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import supermercado.pagos.dto.ProductRequest;
import supermercado.pagos.model.Product;
import supermercado.pagos.repository.ProductRepository;

import java.util.List;
import java.util.Map;

// Protegido automáticamente por SecurityConfig: todo /api/admin/** requiere ROLE_ADMIN
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminProductController {

    private final ProductRepository productRepository;

    @GetMapping
    public List<Product> listarTodos() {
        return productRepository.findAll(); // incluye inactivos, a diferencia del catálogo público
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ProductRequest request) {
        try {
            Product producto = Product.builder()
                    .nombre(request.getNombre())
                    .descripcion(request.getDescripcion())
                    .precio(request.getPrecio())
                    .stock(request.getStock())
                    .imagenUrl(request.getImagenUrl())
                    .activo(request.getActivo() != null ? request.getActivo() : true)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(producto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ProductRequest request) {
        try {
            Product producto = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (request.getNombre() != null) producto.setNombre(request.getNombre());
            if (request.getDescripcion() != null) producto.setDescripcion(request.getDescripcion());
            if (request.getPrecio() != null) producto.setPrecio(request.getPrecio());
            if (request.getStock() != null) producto.setStock(request.getStock());
            if (request.getImagenUrl() != null) producto.setImagenUrl(request.getImagenUrl());
            if (request.getActivo() != null) producto.setActivo(request.getActivo());

            return ResponseEntity.ok(productRepository.save(producto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // En vez de borrar de verdad (rompería la FK con transaction_details si el
    // producto ya tiene ventas), simplemente lo activa/desactiva.
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleActivo(@PathVariable Long id) {
        try {
            Product producto = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            producto.setActivo(!Boolean.TRUE.equals(producto.getActivo()));
            return ResponseEntity.ok(productRepository.save(producto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
