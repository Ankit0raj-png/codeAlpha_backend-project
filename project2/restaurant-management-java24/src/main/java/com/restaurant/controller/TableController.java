package com.restaurant.controller;
import com.restaurant.entity.RestaurantTable;
import com.restaurant.service.TableService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/tables")
public class TableController {
    private final TableService service;
    public TableController(TableService service){this.service=service;}
    @GetMapping public List<RestaurantTable> all(){return service.all();}
    @PostMapping public RestaurantTable add(@RequestBody RestaurantTable x){return service.save(x);}
}
