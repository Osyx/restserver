package dev.osyx.restserver.objects;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonIgnore
    private Long identity;
    @JsonProperty("id")
    private Long externalId;
    private double price;
    private String title;
    @Column(length = 1000)
    private String description;
    private String category;
    private String image;

    public Long getId() {
        return externalId;
    }

    public Product setId(Long id) {
        this.externalId = id;
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
                && Objects.equals(externalId, product.externalId)
                && Objects.equals(title, product.title)
                && Objects.equals(description, product.description)
                && Objects.equals(category, product.category)
                && Objects.equals(image, product.image)
                && Objects.equals(price, product.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identity, externalId, title, price);
    }

    @Override
    public String toString() {
        return "Product {" +
                "id=" + externalId +
                ", price=" + price +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
