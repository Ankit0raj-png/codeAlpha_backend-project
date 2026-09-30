package com.restaurant.service;
import com.restaurant.entity.MenuItem;
import com.restaurant.repository.MenuItemRepository;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class MenuService {
    private final MenuItemRepository repo;
    public MenuService(MenuItemRepository repo){this.repo=repo;}
    public List<MenuItem> all(){return repo.findAll();}
    public MenuItem get(Long id){return repo.findById(id).orElseThrow(()->new RuntimeException("Menu item not found"));}
    public MenuItem save(MenuItem item){return repo.save(item);}
    public MenuItem update(Long id, MenuItem x){
        MenuItem m=get(id); m.setName(x.getName()); m.setDescription(x.getDescription());
        m.setPrice(x.getPrice()); m.setAvailable(x.isAvailable()); return repo.save(m);
    }
    public void delete(Long id){repo.deleteById(id);}
}
