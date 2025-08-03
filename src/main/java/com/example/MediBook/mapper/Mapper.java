package com.example.MediBook.mapper;

import com.example.MediBook.dto.request.HospitalUpdateRequest;
import com.example.MediBook.dto.request.SignupRequest;
import com.example.MediBook.dto.response.*;
import com.example.MediBook.entity.*;
import com.example.MediBook.repository.BookingRepository;
import com.example.MediBook.repository.TimeSlotRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class Mapper {
    TimeSlotRepository timeSlotRepository;
    BookingRepository bookingRepository;
    PasswordEncoder passwordEncoder;


    public Mapper(TimeSlotRepository timeSlotRepository, BookingRepository bookingRepository, PasswordEncoder passwordEncoder) {
        this.timeSlotRepository = timeSlotRepository;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User toUser(SignupRequest request){
        User user=new User();
        user.setName(request.getName());
//        user.setRole(Role.USER);
        user.setGender(request.getGender());
        user.setAddress(request.getAddress());
        user.setCreatedAt(LocalDateTime.now());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return user;
    }

    public UserResponse toUserResponse(User user){
        UserResponse userResponse=new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setGender(user.getGender());
        userResponse.setEmail(user.getEmail());
        userResponse.setEnabled(user.isEnabled());
        return userResponse;
    }

    public BookingResponse toBookingResponse(Booking booking){
        BookingResponse dto = new BookingResponse();
        dto.setId(booking.getId());
        dto.setUserName(booking.getUser().getName());
        dto.setEmail(booking.getUser().getEmail());
        dto.setDate(booking.getTimeSlot().getDate());
        dto.setTime(booking.getTimeSlot().getTime());
        dto.setStatus(booking.getStatus().name());
        dto.setStatusMessage(booking.getStatus().getMessage());
        dto.setNote(booking.getNote());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setCode(booking.getCode());
        return dto;
    }

    public HospitalResponse toResponse(Hospital hospital){
        HospitalResponse response=new HospitalResponse();
        response.setId(hospital.getId());
        response.setAddress(hospital.getAddress());
        response.setCost(hospital.getCost());
        response.setDescription(hospital.getDescription());
        response.setName(hospital.getName());
        response.setEmail(hospital.getEmail());
        response.setEndTime(hospital.getEndTime());
        response.setStartTime(hospital.getStartTime());
        response.setPhoneNumber(hospital.getPhoneNumber());
        response.setImageUrl(hospital.getImageUrl());
        response.setResponses(hospital
                .getDepartment()
                .stream()
                .map(this::toDepartmentResponse)
                .collect(Collectors.toList()));
        return response;
    }

    public void update(Hospital hospital,HospitalUpdateRequest request){
        hospital.setAddress(request.getAddress());
        hospital.setCost(request.getCost());
        hospital.setDescription(request.getDescription());
        hospital.setName(request.getName());
        hospital.setEmail(request.getEmail());
        hospital.setEndTime(request.getEndTime());
        hospital.setStartTime(request.getStartTime());
        hospital.setPhoneNumber(request.getPhoneNumber());
        hospital.setImageUrl(request.getImageUrl());
    }

    public DepartmentResponse toDepartmentResponse(Department department){
        DepartmentResponse departmentResponse=new DepartmentResponse();
        departmentResponse.setId(department.getId());
        departmentResponse.setCost(department.getCost());
        departmentResponse.setDescription(department.getDescription());
        departmentResponse.setHead(toUserResponse(department.getHead()));
        departmentResponse.setName(department.getName());
        departmentResponse.setBookingResponses(bookingRepository.findAllByDepartmentId(department.getId()).stream().map(this::toBookingResponse).collect(Collectors.toList()));
        List<TimeSlot> slots=timeSlotRepository.findByDepartmentId(department.getId());
        List<TimeSlotResponse> slotResponses=slots.stream().map(this::toTimeSlotResponse).collect(Collectors.toList());
        departmentResponse.setTimeSlots(slotResponses);
        return departmentResponse;
    }

    public TimeSlotResponse toTimeSlotResponse(TimeSlot timeSlot){
        return TimeSlotResponse.builder()
                .id(timeSlot.getId())
                .time(timeSlot.getTime())
                .date(timeSlot.getDate())
                .available(timeSlot.isAvailable())
                .build();
    }



}
