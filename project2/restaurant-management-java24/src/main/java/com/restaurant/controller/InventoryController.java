package com.restaurant.controller;
import com.restaurant.entity.Inventory;
import com.restaurant.service.InventoryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService service;
    public InventoryController(InventoryService service){this.service=service;}
    @GetMapping public List<Inventory> all(){return service.all();}
    @PostMapping("/{menuId}") public Inventory update(@PathVariable Long menuId,@RequestBody Inventory x){return service.save(menuId,x);}
}
