package com.restaurant.controller;
import com.restaurant.entity.MenuItem;
import com.restaurant.service.MenuService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/menu")
public class MenuController {
    private final MenuService service;
    public MenuController(MenuService service){this.service=service;}
    @GetMapping public List<MenuItem> all(){return service.all();}
    @GetMapping("/{id}") public MenuItem get(@PathVariable Long id){return service.get(id);}
    @PostMapping public MenuItem add(@RequestBody MenuItem x){return service.save(x);}
    @PutMapping("/{id}") public MenuItem update(@PathVariable Long id,@RequestBody MenuItem x){return service.update(id,x);}
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id){service.delete(id);}
}
