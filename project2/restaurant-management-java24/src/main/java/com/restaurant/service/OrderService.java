package com.restaurant.service;

import com.restaurant.entity.*;
import com.restaurant.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    private final OrderRepository orders;
    private final RestaurantTableRepository tables;
    private final MenuItemRepository menu;
    private final InventoryRepository inventory;

    public OrderService(OrderRepository orders, RestaurantTableRepository tables, MenuItemRepository menu, InventoryRepository inventory){
        this.orders=orders; this.tables=tables; this.menu=menu; this.inventory=inventory;
    }

    @Transactional
    public Order place(Map<String,Object> request){
        Long tableId=((Number)request.get("tableId")).longValue();
        RestaurantTable table=tables.findById(tableId).orElseThrow(()->new RuntimeException("Table not found"));
        List<Map<String,Object>> items=(List<Map<String,Object>>)request.get("items");
        if(items==null || items.isEmpty()) throw new RuntimeException("Order must contain items");

        BigDecimal total=BigDecimal.ZERO;
        for(Map<String,Object> row:items){
            Long menuId=((Number)row.get("menuItemId")).longValue();
            int qty=((Number)row.get("quantity")).intValue();
            MenuItem item=menu.findById(menuId).orElseThrow(()->new RuntimeException("Menu item not found: "+menuId));
            if(!item.isAvailable()) throw new RuntimeException(item.getName()+" is unavailable");
            Inventory inv=inventory.findByMenuItemId(menuId).orElseThrow(()->new RuntimeException("Inventory missing for "+item.getName()));
            if(inv.getQuantity()<qty) throw new RuntimeException("Insufficient stock for "+item.getName());
            inv.setQuantity(inv.getQuantity()-qty);
            inventory.save(inv);
            total=total.add(item.getPrice().multiply(BigDecimal.valueOf(qty)));
        }
        Order order=new Order();
        order.setRestaurantTable(table);
        order.setStatus("PENDING");
        order.setTotalAmount(total);
        order.setCreatedAt(LocalDateTime.now());
        return orders.save(order);
    }

    public List<Order> all(){return orders.findAll();}
    public Order get(Long id){return orders.findById(id).orElseThrow(()->new RuntimeException("Order not found"));}
    public Order status(Long id,String status){Order o=get(id);o.setStatus(status);return orders.save(o);}
}
