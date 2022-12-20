package dev.osyx.restserver.db;

import dev.osyx.restserver.objects.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {

}
