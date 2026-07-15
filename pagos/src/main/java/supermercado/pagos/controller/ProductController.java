package supermercado.pagos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import supermercado.pagos.model.Product;
import supermercado.pagos.repository.ProductRepository;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productRepository.findByActivoTrue();
    }
}