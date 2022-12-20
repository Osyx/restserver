package dev.osyx.restserver.db;

import dev.osyx.restserver.objects.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ExternalRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalRepository.class);
    private static final String EXTERNAL_API_PRODUCTS = "https://fakestoreapi.com/products";
    private static final String FETCHED_FROM_API_LOG_STRING = "Fetched from API: {}";
    private static final Product[] EMPTY_PRODUCTS = {};

    private final RestTemplate restTemplate;

    public ExternalRepository() {
        restTemplate = new RestTemplate();
    }

    public List<Product> getAllProducts() {
        Product[] response = restTemplate.getForObject(EXTERNAL_API_PRODUCTS, Product[].class);
        var products = Arrays.asList(Objects.requireNonNullElse(response, EMPTY_PRODUCTS));
        LOG.debug(FETCHED_FROM_API_LOG_STRING, products);
        return products;
    }

    public List<Product> getSomeProducts() {
        Product[] response = restTemplate.getForObject(EXTERNAL_API_PRODUCTS + "?limit=10", Product[].class);
        var products = Arrays.asList(Objects.requireNonNullElse(response, EMPTY_PRODUCTS));
        LOG.debug(FETCHED_FROM_API_LOG_STRING, products);
        return products;
    }

    public Optional<Product> getProduct(long id) {
        if (id < 1) {
            throw new IllegalArgumentException("Product ID cannot be less than 1.");
        }
        var product = restTemplate.getForObject(EXTERNAL_API_PRODUCTS + "/" + id, Product.class);
        LOG.debug(FETCHED_FROM_API_LOG_STRING, product);
        return Optional.ofNullable(product);
    }
}
