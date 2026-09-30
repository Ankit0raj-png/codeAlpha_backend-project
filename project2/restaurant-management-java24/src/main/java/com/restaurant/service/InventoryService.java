package com.restaurant.service;
import com.restaurant.entity.*;
import com.restaurant.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class InventoryService {
    private final InventoryRepository repo;
    private final MenuItemRepository menuRepo;
    public InventoryService(InventoryRepository repo, MenuItemRepository menuRepo){this.repo=repo;this.menuRepo=menuRepo;}
    public List<Inventory> all(){return repo.findAll();}
    public Inventory save(Long menuId, Inventory input){
        MenuItem item=menuRepo.findById(menuId).orElseThrow(()->new RuntimeException("Menu item not found"));
        Inventory inv=repo.findByMenuItemId(menuId).orElse(new Inventory());
        inv.setMenuItem(item); inv.setQuantity(input.getQuantity()); inv.setReorderLevel(input.getReorderLevel());
        return repo.save(inv);
    }
}
