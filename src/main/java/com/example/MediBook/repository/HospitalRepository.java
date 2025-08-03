package com.example.MediBook.repository;

import com.example.MediBook.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital,Long>{

    Optional<Hospital> findByDirectorId(Long userId);

    Optional<Hospital> findByDepartmentId(Long dpmId);

    @Query("SELECT DISTINCT h FROM Hospital h " +
            "LEFT JOIN h.department d " +
            "WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(h.address) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Hospital> searchHospitals(@Param("keyword") String keyword);
}
