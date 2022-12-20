package dev.osyx.restserver.objects;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFilter(Product.PRODUCT_FILTER)
@SuppressWarnings("unused")
public class Product {

    public static final String PRODUCT_FILTER = "productFilter";

    @Id
    @GeneratedValue
    private Long id;
    private double price;
    private String title;
    @Column(length = 1000)
    private String description;
    private String category;
    private String image;

    public Long getId() {
        return id;
    }

    public Product setId(Long id) {
        this.id = id;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Product setPrice(double price) {
        this.price = price;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Product setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Product setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public Product setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Product setImage(String image) {
        this.image = image;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof Product product
                && Objects.equals(title, product.title)
                && Objects.equals(description, product.description)
                && Objects.equals(category, product.category)
                && Objects.equals(image, product.image)
                && Objects.equals(price, product.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, price);
    }

    @Override
    public String toString() {
        return "Product {" +
                "id=" + id +
                ", price=" + price +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
