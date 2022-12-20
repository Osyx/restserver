package dev.osyx.restserver.controller;

import dev.osyx.restserver.db.ProductRepository;
import dev.osyx.restserver.objects.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class ProductController {

    private final ProductRepository repo;

    @Autowired
    public ProductController(ProductRepository repo) {
        this.repo = Objects.requireNonNull(repo);
    }

    @GetMapping("/products")
    public Iterable<Product> products() {
        return repo.findAll();
    }

    @GetMapping("/product")
    public Product product(@RequestParam(value = "productid", defaultValue = "-1") long id) {
        return repo.findById(id)
                .orElseThrow(() -> {
                    throw new IllegalArgumentException("Invalid id");
                });
    }
}
