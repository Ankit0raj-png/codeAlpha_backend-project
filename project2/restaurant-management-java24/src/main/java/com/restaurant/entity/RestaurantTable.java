package com.restaurant.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurant_tables")
public class RestaurantTable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true) private Integer tableNumber;
    @Column(nullable=false) private Integer capacity;
    @Column(nullable=false) private boolean available = true;

    public RestaurantTable() {}
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public Integer getTableNumber(){return tableNumber;}
    public void setTableNumber(Integer tableNumber){this.tableNumber=tableNumber;}
    public Integer getCapacity(){return capacity;}
    public void setCapacity(Integer capacity){this.capacity=capacity;}
    public boolean isAvailable(){return available;}
    public void setAvailable(boolean available){this.available=available;}
}
