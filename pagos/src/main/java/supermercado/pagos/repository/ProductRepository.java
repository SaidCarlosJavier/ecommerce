package supermercado.pagos.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import supermercado.pagos.model.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActivoTrue();
}
