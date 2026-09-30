package com.restaurant.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory")
public class Inventory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name="menu_item_id", nullable=false, unique=true)
    private MenuItem menuItem;
    @Column(nullable=false) private Integer quantity;
    @Column(nullable=false) private Integer reorderLevel = 5;

    public Inventory() {}
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public MenuItem getMenuItem(){return menuItem;}
    public void setMenuItem(MenuItem menuItem){this.menuItem=menuItem;}
    public Integer getQuantity(){return quantity;}
    public void setQuantity(Integer quantity){this.quantity=quantity;}
    public Integer getReorderLevel(){return reorderLevel;}
    public void setReorderLevel(Integer reorderLevel){this.reorderLevel=reorderLevel;}
}
