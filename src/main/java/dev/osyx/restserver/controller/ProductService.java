package dev.osyx.restserver.controller;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import dev.osyx.restserver.db.ExternalRepository;
import dev.osyx.restserver.db.ProductRepository;
import dev.osyx.restserver.error.ProductNotFoundException;
import dev.osyx.restserver.objects.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.json.MappingJacksonValue;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);
    private static final int PAGE_SIZE = 8;
    private final ProductRepository repository;
    private final ExternalRepository externalRepository;

    private boolean hasFetchedAllFromExternal = false;

    public ProductService(ProductRepository repository) {
        this.repository = Objects.requireNonNull(repository);
        this.externalRepository = new ExternalRepository();
    }

    public static MappingJacksonValue getPagedProducts(int page, Function<Pageable, Page<Product>> getProductPage) {
        if (page < 0) {
            throw new IllegalArgumentException("Page size cannot be less than zero.");
        }
        Pageable paging = PageRequest.of(page, PAGE_SIZE, Sort.by("externalId").ascending());
        Page<Product> productPage = getProductPage.apply(paging);
        if (page > productPage.getTotalPages()) {
            throw new ProductNotFoundException();
        }

        List<Product> products = productPage.getContent();
        return getJacksonValue(products, getDescriptionFilter());
    }

    public Product getProduct(Long id) {
        return repository.findByExternalId(id)
                .or(() -> getAndSaveProduct(id))
                .orElseThrow(() -> {
                    LOG.debug(String.format("Call to product endpoint with id '%d' ended in an exception.", id));
                    throw new ProductNotFoundException(id);
                });
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        if (hasFetchedAllFromExternal) {
            return repository.findAll(pageable);
        }

        var allProducts = externalRepository.getAllProducts();
        var productsNotInDb = allProducts.stream()
                .filter(product -> !repository.existsByExternalId(product.getId()))
                .toList();
        repository.saveAll(productsNotInDb);
        hasFetchedAllFromExternal = true;
        return getProductPageFromList(pageable, allProducts);
    }

    public Page<Product> getProductsByPriceBetween(int minPrice, int maxPrice, Pageable paging) {
        return repository.findAllByPriceBetween(minPrice, maxPrice, paging);
    }

    public Page<Product> getAllProductsByCategory(String sanitizedCategory, Pageable paging) {
        return repository.findAllByCategoryIgnoreCase(sanitizedCategory, paging);
    }

    private Optional<Product> getAndSaveProduct(Long id) {
        Optional<Product> product = externalRepository.getProduct(id);
        product.ifPresent(entity -> {
            var savedProduct = repository.save(entity);
            LOG.debug(String.format("Saving object: '%s'.", savedProduct));
        });
        return product;
    }

    public static SimpleFilterProvider getAllowAllFilter() {
        return new SimpleFilterProvider()
                .addFilter(Product.PRODUCT_FILTER, SimpleBeanPropertyFilter.serializeAll());
    }

    public static <T> MappingJacksonValue getJacksonValue(T object, FilterProvider filter) {
        var mappingJacksonValue = new MappingJacksonValue(object);
        mappingJacksonValue.setFilters(filter);
        return mappingJacksonValue;
    }

    private static SimpleFilterProvider getDescriptionFilter() {
        return new SimpleFilterProvider()
                .addFilter(Product.PRODUCT_FILTER, SimpleBeanPropertyFilter.serializeAllExcept("description"));
    }

    private static PageImpl<Product> getProductPageFromList(Pageable pageable, List<Product> allProducts) {
        var offset = Math.toIntExact(pageable.getOffset());
        var subProducts = allProducts.subList(offset, offset + pageable.getPageSize());
        return new PageImpl<>(subProducts, pageable, allProducts.size());
    }
}
