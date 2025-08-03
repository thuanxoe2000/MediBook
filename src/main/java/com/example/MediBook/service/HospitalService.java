package com.example.MediBook.service;

import com.example.MediBook.dto.request.*;
import com.example.MediBook.dto.response.DepartmentResponse;
import com.example.MediBook.dto.response.DoctorResponse;
import com.example.MediBook.dto.response.HospitalResponse;
import com.example.MediBook.entity.*;
import com.example.MediBook.mapper.Mapper;
import com.example.MediBook.repository.*;
import com.example.MediBook.util.UserUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HospitalService {
    HospitalRepository hospitalRepository;
    DepartmentRepository departmentRepository;
    UserRepository userRepository;
    TimeSlotRepository timeSlotRepository;
    Mapper mapper;
    UserUtil userUtil;
    TimeSlotService timeSlotService;
    PasswordEncoder passwordEncoder;
    DoctorRepository doctorRepository;
    RoleRepository roleRepository;

    public Doctor createDoctor(CreateDoctorRequest request){
        var role=roleRepository.findById("DOCTOR").orElseThrow();
        User user= User.builder()
                .email(request.getEmail())
                .createdAt(LocalDateTime.now())
                .phoneNumber(request.getPhoneNumber())
                .enabled(true)
                .verified(true)
                .address(request.getAddress())
                .gender(request.getGender())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(role))
                .build();
        userRepository.save(user);
        Doctor doctor=Doctor.builder()
                .user(user)
                .build();
        doctorRepository.save(doctor);
        return doctor;
    }

    public void createHospitalAndDirector(HospitalRequest request) {
        var director=roleRepository.findById("DIRECTOR").orElseThrow();
        Hospital hospital = Hospital.builder()
                .name(request.getName())
                .address(request.getAddress())
                .cost(request.getCost())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();
        hospitalRepository.save(hospital);

        Doctor doctor=createDoctor(request.getDirector());
        Set<Role> roles=new HashSet<>(doctor.getUser().getRoles());
        roles.add(director);
        doctor.setHospital(hospital);
        doctor.getUser().setRoles(roles);
        doctorRepository.save(doctor);

        hospital.setDirector(doctor);
        hospitalRepository.save(hospital);
    }

    public void updateHospital(HospitalUpdateRequest request) {
        User user = userUtil.getCurrentUser();
        Hospital hospital = hospitalRepository.findByDirectorId(user.getId())
                .orElseThrow(() -> new RuntimeException("病院が見つかりません"));
        mapper.update(hospital, request);
        hospitalRepository.save(hospital);
    }

    public void createDepartmentAndHead(DepartmentRequest request) {
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new RuntimeException("病院が見つかりません"));

        var head=roleRepository.findById("DEPARTMENT_HEAD").orElseThrow();

        Doctor doctor=createDoctor(request.getHead());
        Set<Role> roles=new HashSet<>(doctor.getUser().getRoles());
        roles.add(head);
        doctor.getUser().setRoles(roles);

        Department department = Department.builder()
                .cost(request.getCost())
                .description(request.getDescription())
                .name(request.getName())
                .hospital(hospital)
                .head(doctor.getUser())
                .build();
        departmentRepository.save(department);

        doctor.setDepartment(department);
        doctorRepository.save(doctor);

        timeSlotService.generateInitialSlotsForDepartment(department);
    }

    public List<HospitalResponse> getAllHospital() {
        return hospitalRepository
                .findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public HospitalResponse getDetail(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("病院が見つかりません"));
        return mapper.toResponse(hospital);
    }

    public HospitalResponse getHospital() {
        HospitalResponse response = null;

        if (userUtil.hasRole("DIRECTOR")) {
            response = mapper.toResponse(
                    hospitalRepository.findByDirectorId(userUtil.getCurrentUser().getId())
                            .orElseThrow(() -> new RuntimeException("病院が見つかりません"))
            );
        } else if (userUtil.hasRole("DEPARTMENT_HEAD")) {
            Department department = departmentRepository.findByHeadId(userUtil.getCurrentUser().getId())
                    .orElseThrow(() -> new RuntimeException("科が見つかりません"));
            Hospital hospital = hospitalRepository.findByDepartmentId(department.getId())
                    .orElseThrow(() -> new RuntimeException("病院が見つかりません"));
            response = mapper.toResponse(hospital);
            response.setResponses(List.of(mapper.toDepartmentResponse(department)));
        }

        return response;
    }

    public List<HospitalResponse> search(String keyword) {
        List<Hospital> hospitals = hospitalRepository.searchHospitals(keyword);
        return hospitals
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public DepartmentResponse getDepartmentDetail(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("科が見つかりません"));
        List<TimeSlot> timeSlots = timeSlotRepository.findByAvailableIsTrueAndDepartmentId(id);
        department.setTimeSlots(timeSlots);
        return mapper.toDepartmentResponse(department);
    }

    public void updateDepartment(DpmUpdateRequest request, Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("科が見つかりません"));
        department.setName(request.getName());
        department.setCost(request.getCost());
        department.setDescription(request.getDescription());
        departmentRepository.save(department);
    }

    public List<DoctorResponse> getDoctors(Long id) {
        List<DoctorResponse> doctorResponses = new ArrayList<>();
        doctorRepository.findAllByHospitalId(id).forEach(e -> {
            DoctorResponse doctorResponse = DoctorResponse.builder()
                    .id(e.getId())
                    .name(e.getUser().getName())
                    .departmentName(e.getDepartment() != null ? e.getDepartment().getName() : "")
                    .build();
            doctorResponses.add(doctorResponse);
        });
        return doctorResponses;
    }

    public List<DepartmentResponse> getDepartments(Long id) {
        return departmentRepository
                .findAllByHospitalId(id)
                .stream()
                .map(mapper::toDepartmentResponse)
                .collect(Collectors.toList());
    }

    public void changeDpm(Long doctorId, Long dpmId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("医師が見つかりません"));
        Department department = departmentRepository.findById(dpmId)
                .orElseThrow(() -> new RuntimeException("科が見つかりません"));
        doctor.setDepartment(department);
        doctorRepository.save(doctor);
    }
}
