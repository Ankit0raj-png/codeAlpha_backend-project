package com.restaurant.controller;
import com.restaurant.entity.Reservation;
import com.restaurant.service.ReservationService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService service;
    public ReservationController(ReservationService service){this.service=service;}
    @GetMapping public List<Reservation> all(){return service.all();}
    @PostMapping public Reservation add(@RequestBody Reservation x){return service.reserve(x);}
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id){service.cancel(id);}
}
