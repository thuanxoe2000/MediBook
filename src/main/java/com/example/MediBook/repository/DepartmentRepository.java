package com.example.MediBook.repository;

import com.example.MediBook.entity.Department;
import com.example.MediBook.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department,Long> {

    List<Department> findAllByHospitalId(Long id);

    @Query("SELECT d.hospital FROM Department d WHERE d.id = :id")
    Optional<Hospital> findHospitalByDepartmentId(@Param("id") Long id);


    Optional<Department> findByHeadId(Long id);
}
