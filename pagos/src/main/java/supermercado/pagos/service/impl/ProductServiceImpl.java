package supermercado.pagos.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supermercado.pagos.model.Product;
import supermercado.pagos.repository.ProductRepository;
import supermercado.pagos.service.ProductService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<Product> obtenerCatalogoActivo() {
        return productRepository.findByActivoTrue();
    }

    @Override
    public Product obtenerPorId(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }
}