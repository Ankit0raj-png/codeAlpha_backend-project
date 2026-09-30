package com.restaurant.service;
import com.restaurant.entity.*;
import com.restaurant.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class ReservationService {
    private final ReservationRepository reservations;
    private final RestaurantTableRepository tables;
    public ReservationService(ReservationRepository reservations, RestaurantTableRepository tables){
        this.reservations=reservations; this.tables=tables;
    }
    public List<Reservation> all(){return reservations.findAll();}
    public Reservation reserve(Reservation r){
        RestaurantTable table=tables.findById(r.getRestaurantTable().getId())
            .orElseThrow(()->new RuntimeException("Table not found"));
        if(r.getGuests()>table.getCapacity()) throw new RuntimeException("Table capacity is too small");
        if(reservations.existsByRestaurantTableIdAndReservationTime(table.getId(), r.getReservationTime()))
            throw new RuntimeException("Table is already reserved at this time");
        r.setRestaurantTable(table);
        return reservations.save(r);
    }
    public void cancel(Long id){reservations.deleteById(id);}
}
