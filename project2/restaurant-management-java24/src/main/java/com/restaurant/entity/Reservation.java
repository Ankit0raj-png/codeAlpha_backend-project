package com.restaurant.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="reservations")
public class Reservation {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false) private String customerName;
    @Column(nullable=false) private Integer guests;
    @Column(nullable=false) private LocalDateTime reservationTime;
    @ManyToOne @JoinColumn(name="table_id", nullable=false)
    private RestaurantTable restaurantTable;

    public Reservation() {}
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getCustomerName(){return customerName;}
    public void setCustomerName(String customerName){this.customerName=customerName;}
    public Integer getGuests(){return guests;}
    public void setGuests(Integer guests){this.guests=guests;}
    public LocalDateTime getReservationTime(){return reservationTime;}
    public void setReservationTime(LocalDateTime reservationTime){this.reservationTime=reservationTime;}
    public RestaurantTable getRestaurantTable(){return restaurantTable;}
    public void setRestaurantTable(RestaurantTable restaurantTable){this.restaurantTable=restaurantTable;}
}
