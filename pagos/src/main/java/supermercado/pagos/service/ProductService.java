package supermercado.pagos.service;

import supermercado.pagos.model.Product;
import java.util.List;

public interface ProductService {
    List<Product> obtenerCatalogoActivo();
    Product obtenerPorId(Long id);
}