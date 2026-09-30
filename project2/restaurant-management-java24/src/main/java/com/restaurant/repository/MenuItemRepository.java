package com.restaurant.repository;
import com.restaurant.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {}
