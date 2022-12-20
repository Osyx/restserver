package dev.osyx.restserver.error;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(long productid) {
        super(String.format("Product with id '%s' not found!", productid));
    }

    public ProductNotFoundException() {
        super("Could not find any products with that request.");
    }
}
