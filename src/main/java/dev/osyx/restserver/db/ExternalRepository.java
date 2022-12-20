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

    private static final Logger log = LoggerFactory.getLogger(ExternalRepository.class);
    private static final String EXTERNAL_API_PRODUCTS = "https://fakestoreapi.com/products";
    private static final Product[] EMPTY_PRODUCTS = {};

    private final RestTemplate restTemplate;

    public ExternalRepository() {
        restTemplate = new RestTemplate();
    }

    public List<Product> getSomeProducts() {
        Product[] response = restTemplate.getForObject(EXTERNAL_API_PRODUCTS + "?limit=10", Product[].class);
        List<Product> products = Arrays.asList(Objects.requireNonNullElse(response, EMPTY_PRODUCTS));
        log.debug("Fetched products from API: {}", products);
        return products;
    }

    public Optional<Product> getProduct(long id) {
        Product product = restTemplate.getForObject(EXTERNAL_API_PRODUCTS + "/" + id, Product.class);
        log.debug("Fetched product from API: {}", product);
        return Optional.ofNullable(product);
    }
}
