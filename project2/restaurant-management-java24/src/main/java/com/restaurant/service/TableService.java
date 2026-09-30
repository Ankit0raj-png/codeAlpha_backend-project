package com.restaurant.service;
import com.restaurant.entity.RestaurantTable;
import com.restaurant.repository.RestaurantTableRepository;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class TableService {
    private final RestaurantTableRepository repo;
    public TableService(RestaurantTableRepository repo){this.repo=repo;}
    public List<RestaurantTable> all(){return repo.findAll();}
    public RestaurantTable save(RestaurantTable t){return repo.save(t);}
}
