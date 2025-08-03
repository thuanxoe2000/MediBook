package com.example.MediBook.controller;

import com.example.MediBook.dto.request.RoleRequest;
import com.example.MediBook.dto.response.RoleResponse;
import com.example.MediBook.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleResponse> create(@RequestBody RoleRequest request){
        return ResponseEntity.ok(roleService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAll(){
        return ResponseEntity.ok(roleService.getAll());
    }

    @DeleteMapping("/{role}")
    public ResponseEntity<Void> delete(@PathVariable String role){
        roleService.delete(role);
        return ResponseEntity.noContent().build();
    }
}
