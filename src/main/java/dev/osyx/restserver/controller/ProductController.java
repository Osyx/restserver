package dev.osyx.restserver.controller;

import dev.osyx.restserver.db.ExternalRepository;
import dev.osyx.restserver.db.ProductRepository;
import dev.osyx.restserver.error.ProductNotFoundException;
import dev.osyx.restserver.objects.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
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
    public Collection<Product> products() {
        return repository.findAll();
    }

    @GetMapping("/products/{productid}")
    public Product product(@PathVariable Long productid) {
        return repository.findById(productid)
                .or(() -> getAndSaveProduct(productid))
                .orElseThrow(() -> {
                    log.debug(String.format("Call to product endpoint with id '%d' ended in an exception.", productid));
                    throw new ProductNotFoundException(productid);
                });
    }

    private Optional<Product> getAndSaveProduct(Long productid) {
        Optional<Product> product = externalRepository.getProduct(productid);
        product.ifPresent(entity -> {
            var savedProduct = repository.save(entity);
            log.debug(String.format("Saving object: '%s'.", savedProduct));
        });
        return product;
    }
}
