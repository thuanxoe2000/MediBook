package com.example.MediBook.repository;

import com.example.MediBook.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot,Long> {

    List<TimeSlot> findByDateAndAvailableIsTrueAndDepartmentId(LocalDate date, Long departmentId);

    List<TimeSlot> findByAvailableIsTrueAndDepartmentId(Long departmentId);

    List<TimeSlot> findByDepartmentId(Long aLong);


    boolean existsByDateAndDepartmentId(LocalDate date, Long departmentId);

}
