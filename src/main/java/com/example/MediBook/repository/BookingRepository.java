package com.example.MediBook.repository;

import com.example.MediBook.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {

    List<Booking> findAllByUserId(Long userId);

    List<Booking> findAllByDepartmentId(Long id);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.status = 'CONFIRMED' " +
            "AND (b.timeSlot.date < :limitDate OR " +
            "(b.timeSlot.date = :limitDate AND b.timeSlot.time <= :limitTime))")
    List<Booking> findBookingsToFinish(@Param("limitDate") LocalDate limitDate,
                                       @Param("limitTime") LocalTime limitTime);

}
