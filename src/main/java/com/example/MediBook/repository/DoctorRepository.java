package com.example.MediBook.repository;

import com.example.MediBook.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor,Long> {

    List<Doctor> findAllByHospitalId(Long id);
}
