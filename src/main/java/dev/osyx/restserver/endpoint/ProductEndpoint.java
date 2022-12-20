package dev.osyx.restserver.endpoint;

import dev.osyx.restserver.controller.Controller;
import dev.osyx.restserver.db.ProductRepository;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static dev.osyx.restserver.controller.Controller.getAllowAllFilter;
import static dev.osyx.restserver.controller.Controller.getJacksonValue;
import static dev.osyx.restserver.controller.Controller.getPagedProducts;

@RestController
public class ProductEndpoint {

    private static final String PRODUCTS_ENDPOINT = "/products";
    private final Controller controller;

    @Autowired
    public ProductEndpoint(ProductRepository repository) {
        this.controller = new Controller(Objects.requireNonNull(repository));
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
        return getPagedProducts(page, controller::getAllProducts);
    }

    @GetMapping(value = PRODUCTS_ENDPOINT, params = {Parameters.MIN_PRICE, Parameters.MAX_PRICE})
    public MappingJacksonValue products(@RequestParam(Parameters.MIN_PRICE) int minPrice,
                                        @RequestParam(Parameters.MAX_PRICE) int maxPrice) {
        return products(minPrice, maxPrice, 0);
    }

    @GetMapping(value = PRODUCTS_ENDPOINT, params = {Parameters.MIN_PRICE, Parameters.MAX_PRICE, Parameters.PAGE})
    public MappingJacksonValue products(@RequestParam(Parameters.MIN_PRICE) int minPrice,
                                        @RequestParam(Parameters.MAX_PRICE) int maxPrice,
                                        @RequestParam(Parameters.PAGE) int page) {
        return getPagedProducts(page, paging -> controller.getProductsByPriceBetween(minPrice, maxPrice, paging));
    }

    @GetMapping(value = PRODUCTS_ENDPOINT, params = Parameters.CATEGORY)
    public MappingJacksonValue products(@RequestParam(Parameters.CATEGORY) String category) {
        return products(category, 0);
    }

    @GetMapping(value = PRODUCTS_ENDPOINT, params = {Parameters.CATEGORY, Parameters.PAGE})
    public MappingJacksonValue products(@RequestParam(Parameters.CATEGORY) String category,
                                        @RequestParam(Parameters.PAGE) int page) {
        String sanitizedCategory = StringEscapeUtils.escapeJava(category);
        return getPagedProducts(page, paging -> controller.getAllProductsByCategory(sanitizedCategory, paging));
    }

    @GetMapping(PRODUCTS_ENDPOINT + "/{id}")
    public MappingJacksonValue products(@PathVariable Long id) {
        var product = controller.getProduct(id);
        return getJacksonValue(product, getAllowAllFilter());
    }
}
