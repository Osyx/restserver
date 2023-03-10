package dev.osyx.restserver;

import dev.osyx.restserver.db.ExternalRepository;
import dev.osyx.restserver.db.ProductRepository;
import dev.osyx.restserver.objects.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
class LoadDatabase {

    private static final Logger LOG = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            LOG.debug("Starting...");
            List<Product> products = new ExternalRepository().getSomeProducts();
            repository.saveAll(products)
                    .forEach(savedObject -> LOG.debug("Saved: {}", savedObject));
            LOG.debug("Done preloading!");
        };
    }
}
