package supermercado.pagos.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import supermercado.pagos.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
