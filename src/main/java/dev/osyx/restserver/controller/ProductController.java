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
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Optional;

@RestController
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
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

    @GetMapping("/products")
    public MappingJacksonValue products() {
        var products = repository.findAll();
        var filter = new SimpleFilterProvider()
                .addFilter(Product.PRODUCT_FILTER, SimpleBeanPropertyFilter.serializeAllExcept("description"));
        return getJacksonValue(products, filter);
    }

    @GetMapping("/products/{productid}")
    public MappingJacksonValue product(@PathVariable Long productid) {
        var product = repository.findById(productid)
                .or(() -> getAndSaveProduct(productid))
                .orElseThrow(() -> {
                    log.debug(String.format("Call to product endpoint with id '%d' ended in an exception.", productid));
                    throw new ProductNotFoundException(productid);
                });
        var filter = new SimpleFilterProvider()
                .addFilter(Product.PRODUCT_FILTER, SimpleBeanPropertyFilter.serializeAll());
        return getJacksonValue(product, filter);
    }

    private Optional<Product> getAndSaveProduct(Long productid) {
        Optional<Product> product = externalRepository.getProduct(productid);
        product.ifPresent(entity -> {
            var savedProduct = repository.save(entity);
            log.debug(String.format("Saving object: '%s'.", savedProduct));
        });
        return product;
    }

    private static <T> MappingJacksonValue getJacksonValue(T object, FilterProvider filter) {
        var mappingJacksonValue = new MappingJacksonValue(object);
        mappingJacksonValue.setFilters(filter);
        return mappingJacksonValue;
    }
}
