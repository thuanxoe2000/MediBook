package com.example.MediBook.controller;

import com.example.MediBook.dto.request.CreateDoctorRequest;
import com.example.MediBook.dto.request.SignupRequest;
import com.example.MediBook.dto.request.UpdateRequest;
import com.example.MediBook.dto.response.DoctorResponse;
import com.example.MediBook.dto.response.UserResponse;
import com.example.MediBook.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok(Map.of("message", "登録が成功しました"));
    }

    @PostMapping("/createDoctor")
    public ResponseEntity<?> createDoctor(@Valid @RequestBody CreateDoctorRequest request) {
        userService.createDoctor(request);
        return ResponseEntity.ok(Map.of("message", "医師の作成に成功しました"));
    }

    @GetMapping("/list-doctor")
    public ResponseEntity<List<DoctorResponse>> getAllDoctor() {
        return ResponseEntity.ok(userService.getAllDoctor());
    }

    @PutMapping("/toggle-user/{id}")
    public ResponseEntity<?> toggleUser(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String message = request.get("reason");
        userService.toggleUser(id, message);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list-user")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/user-detail")
    public ResponseEntity<UserResponse> getUser() {
        return ResponseEntity.ok(userService.getUser());
    }

    @PutMapping("/update-user")
    public ResponseEntity<?> updateUser(@RequestBody UpdateRequest request) {
        userService.updateUser(request);
        return ResponseEntity.noContent().build();
    }

}
