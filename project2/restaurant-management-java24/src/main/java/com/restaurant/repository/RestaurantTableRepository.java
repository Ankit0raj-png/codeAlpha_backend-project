package com.restaurant.repository;
import com.restaurant.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {}
