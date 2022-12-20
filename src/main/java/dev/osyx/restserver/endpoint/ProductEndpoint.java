package dev.osyx.restserver.endpoint;

import dev.osyx.restserver.controller.ProductService;
import dev.osyx.restserver.db.ProductRepository;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class ProductEndpoint {

    private static final String PRODUCTS_ENDPOINT = "/products";
    private final ProductService productService;

    @Autowired
    public ProductEndpoint(ProductRepository repository) {
        this.productService = new ProductService(Objects.requireNonNull(repository));
    }

    @GetMapping("/")
    public String root() {
        return "Welcome to the RestServer!";
    }

    @GetMapping(PRODUCTS_ENDPOINT)
    public MappingJacksonValue products() {
        return products(0);
    }

    @GetMapping(value = PRODUCTS_ENDPOINT, params = Parameters.PAGE)
    public MappingJacksonValue products(@RequestParam(Parameters.PAGE) int page) {
        return ProductService.getPagedProducts(page, productService::getAllProducts);
    }

    @GetMapping(value = PRODUCTS_ENDPOINT, params = {Parameters.MIN_PRICE, Parameters.MAX_PRICE})
    public MappingJacksonValue products(@RequestParam(Parameters.MIN_PRICE) double minPrice,
                                        @RequestParam(Parameters.MAX_PRICE) double maxPrice) {
        return products(minPrice, maxPrice, 0);
    }

    @GetMapping(value = PRODUCTS_ENDPOINT, params = {Parameters.MIN_PRICE, Parameters.MAX_PRICE, Parameters.PAGE})
    public MappingJacksonValue products(@RequestParam(Parameters.MIN_PRICE) double minPrice,
                                        @RequestParam(Parameters.MAX_PRICE) double maxPrice,
                                        @RequestParam(Parameters.PAGE) int page) {
        return ProductService.getPagedProducts(page, paging -> productService.getProductsByPriceBetween(minPrice, maxPrice, paging));
    }

    @GetMapping(value = PRODUCTS_ENDPOINT, params = Parameters.CATEGORY)
    public MappingJacksonValue products(@RequestParam(Parameters.CATEGORY) String category) {
        return products(category, 0);
    }

    @GetMapping(value = PRODUCTS_ENDPOINT, params = {Parameters.CATEGORY, Parameters.PAGE})
    public MappingJacksonValue products(@RequestParam(Parameters.CATEGORY) String category,
                                        @RequestParam(Parameters.PAGE) int page) {
        String sanitizedCategory = StringEscapeUtils.escapeJava(category);
        return ProductService.getPagedProducts(page, paging -> productService.getAllProductsByCategory(sanitizedCategory, paging));
    }

    @GetMapping(PRODUCTS_ENDPOINT + "/{id}")
    public MappingJacksonValue products(@PathVariable Long id) {
        var product = productService.getProduct(id);
        return ProductService.getJacksonValue(product, ProductService.getAllowAllFilter());
    }
}
