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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private static final int PAGE_SIZE = 8;
    private static final String PRODUCTS_ENDPOINT = "/products";
    private final ProductRepository repository;
    private final ExternalRepository externalRepository;

    @Autowired
    public ProductController(ProductRepository repository) {
        this.repository = Objects.requireNonNull(repository);
        this.externalRepository = new ExternalRepository();
    }

    @GetMapping("/")
    public String root() {
        return "Welcome to the RestServer!";
    }

    @GetMapping(PRODUCTS_ENDPOINT)
    public MappingJacksonValue products() {
        return products(0);
    }

    @GetMapping(value = PRODUCTS_ENDPOINT, params = "page")
    public MappingJacksonValue products(@RequestParam("page") int page) {
        var products = getPagedProducts(page);
        return getJacksonValue(products, getDescriptionFilter());
    }

    @GetMapping(PRODUCTS_ENDPOINT + "/{id}")
    public MappingJacksonValue products(@PathVariable Long id) {
        var product = repository.findById(id)
                .or(() -> getAndSaveProduct(id))
                .orElseThrow(() -> {
                    log.debug(String.format("Call to product endpoint with id '%d' ended in an exception.", id));
                    throw new ProductNotFoundException(id);
                });
        return getJacksonValue(product, getAllowAllFilter());
    }

    private List<Product> getPagedProducts(int page) {
        if (page < 0) {
            throw new IllegalArgumentException("Page size cannot be less than zero.");
        }
        Pageable paging = PageRequest.of(page, PAGE_SIZE, Sort.by("id").ascending());
        Page<Product> productPage = repository.findAll(paging);
        if (page > productPage.getTotalPages()) {
            throw new ProductNotFoundException();
        }
        return productPage.getContent();
    }

    private Optional<Product> getAndSaveProduct(Long id) {
        Optional<Product> product = externalRepository.getProduct(id);
        product.ifPresent(entity -> {
            var savedProduct = repository.save(entity);
            log.debug(String.format("Saving object: '%s'.", savedProduct));
        });
        return product;
    }

    private static SimpleFilterProvider getDescriptionFilter() {
        return new SimpleFilterProvider()
                .addFilter(Product.PRODUCT_FILTER, SimpleBeanPropertyFilter.serializeAllExcept("description"));
    }

    private static SimpleFilterProvider getAllowAllFilter() {
        return new SimpleFilterProvider()
                .addFilter(Product.PRODUCT_FILTER, SimpleBeanPropertyFilter.serializeAll());
    }

    private static <T> MappingJacksonValue getJacksonValue(T object, FilterProvider filter) {
        var mappingJacksonValue = new MappingJacksonValue(object);
        mappingJacksonValue.setFilters(filter);
        return mappingJacksonValue;
    }
}
