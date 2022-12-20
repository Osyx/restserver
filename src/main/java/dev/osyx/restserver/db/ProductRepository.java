package dev.osyx.restserver.db;

import dev.osyx.restserver.objects.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface ProductRepository extends ListCrudRepository<Product, Long>, ListPagingAndSortingRepository<Product, Long> {

    Page<Product> findAllByCategoryIgnoreCase(String category, Pageable pageable);

    Page<Product> findAllByPriceBetween(double min, double max, Pageable pageable);
}
