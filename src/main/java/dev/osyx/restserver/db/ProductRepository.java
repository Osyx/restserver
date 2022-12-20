package dev.osyx.restserver.db;

import dev.osyx.restserver.objects.Product;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface ProductRepository extends ListCrudRepository<Product, Long>, ListPagingAndSortingRepository<Product, Long> {

}
