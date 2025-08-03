package com.example.MediBook.controller;

import com.example.MediBook.dto.request.DepartmentRequest;
import com.example.MediBook.dto.request.DpmUpdateRequest;
import com.example.MediBook.dto.request.HospitalRequest;
import com.example.MediBook.dto.request.HospitalUpdateRequest;
import com.example.MediBook.dto.response.DepartmentResponse;
import com.example.MediBook.dto.response.DoctorResponse;
import com.example.MediBook.dto.response.HospitalResponse;
import com.example.MediBook.service.HospitalService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hospital")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class HospitalController {

    HospitalService hospitalService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createHospital(@RequestBody @Valid HospitalRequest request){
        hospitalService.createHospitalAndDirector(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<?> updateHospital(@RequestBody HospitalUpdateRequest request){
        hospitalService.updateHospital(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-hospital")
    public ResponseEntity<?> getHospital(){
        return ResponseEntity.ok(hospitalService.getHospital());
    }

    @GetMapping("/list")
    public ResponseEntity<List<HospitalResponse>> getList(){
        List<HospitalResponse> responses=hospitalService.getAllHospital();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/hospital-detail")
    public ResponseEntity<HospitalResponse> detail(@RequestParam Long id){
        HospitalResponse response=hospitalService.getDetail(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/createDpm")
    public ResponseEntity<?> createDpm(@RequestBody @Valid DepartmentRequest request){
        hospitalService.createDepartmentAndHead(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<HospitalResponse>> searchHospital(@RequestParam String keyword){
        List<HospitalResponse> result = hospitalService.search(keyword);
        if (result == null) return ResponseEntity.ok(Collections.emptyList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/department-detail")
    public ResponseEntity<DepartmentResponse> departmentDetail(@PathVariable Long id){
        return ResponseEntity.ok(hospitalService.getDepartmentDetail(id));
    }

    @PutMapping("/{id}/updateDpm")
    public ResponseEntity<?> updateDpm(@RequestBody DpmUpdateRequest request,@PathVariable Long id){
        hospitalService.updateDepartment(request,id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/doctors")
    public ResponseEntity<List<DoctorResponse>> getDoctors(@PathVariable Long id){
        return ResponseEntity.ok(hospitalService.getDoctors(id));
    }

    @GetMapping("/{id}/departments")
    public ResponseEntity<List<DepartmentResponse>> getDepartments(@PathVariable Long id){
        return ResponseEntity.ok(hospitalService.getDepartments(id));
    }

    @PutMapping("/{id}/change-department")
    public ResponseEntity<?> changeDpm(@PathVariable Long id, @RequestBody Map<String,Long> request){
        Long dpmId=request.get("departmentId");
        hospitalService.changeDpm(id,dpmId);
        return ResponseEntity.noContent().build();
    }


}
