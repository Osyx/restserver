package dev.osyx.restserver.db;

import dev.osyx.restserver.objects.Product;
import org.springframework.data.repository.ListCrudRepository;

public interface ProductRepository extends ListCrudRepository<Product, Long> {

}
