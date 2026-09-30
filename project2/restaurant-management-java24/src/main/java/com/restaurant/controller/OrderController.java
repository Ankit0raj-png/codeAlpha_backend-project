package com.restaurant.controller;
import com.restaurant.entity.Order;
import com.restaurant.service.OrderService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController @RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service){this.service=service;}
    @GetMapping public List<Order> all(){return service.all();}
    @GetMapping("/{id}") public Order get(@PathVariable Long id){return service.get(id);}
    @PostMapping public Order place(@RequestBody Map<String,Object> request){return service.place(request);}
    @PutMapping("/{id}/status") public Order status(@PathVariable Long id,@RequestParam String value){return service.status(id,value);}
}
