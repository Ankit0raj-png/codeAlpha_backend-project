package com.example.urlshortener.repository;

import com.example.urlshortener.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * This is the layer that talks to the SQL database.
 *
 * By extending JpaRepository<UrlMapping, Long>, Spring Data JPA AUTOMATICALLY implements
 * common methods for us at runtime: save(), findById(), findAll(), deleteById(), etc.
 * We never write the SQL for these ourselves.
 *
 * We can also declare our own "query methods" just by following a naming convention -
 * Spring reads the method name and generates the correct SQL query behind the scenes.
 */
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    // Spring auto-generates: SELECT * FROM url_mapping WHERE short_code = ?
    Optional<UrlMapping> findByShortCode(String shortCode);

    // Spring auto-generates: SELECT COUNT(*) > 0 FROM url_mapping WHERE short_code = ?
    boolean existsByShortCode(String shortCode);
}
