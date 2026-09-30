package com.restaurant.repository;
import com.restaurant.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByRestaurantTableIdAndReservationTime(Long tableId, LocalDateTime time);
}
