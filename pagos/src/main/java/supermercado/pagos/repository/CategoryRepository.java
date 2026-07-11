package supermercado.pagos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import supermercado.pagos.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
