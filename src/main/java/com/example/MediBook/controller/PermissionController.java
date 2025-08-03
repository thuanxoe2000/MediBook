package com.example.MediBook.controller;

import com.example.MediBook.dto.request.PermissionRequest;
import com.example.MediBook.dto.response.PermissionResponse;
import com.example.MediBook.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    public ResponseEntity<PermissionResponse> create(@RequestBody PermissionRequest request){
        return ResponseEntity.ok(permissionService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<PermissionResponse>> getAll(){
        return ResponseEntity.ok(permissionService.getAll());
    }

    @DeleteMapping("/{permission}")
    public ResponseEntity<Void> delete(@PathVariable String permission){
        permissionService.delete(permission);
        return ResponseEntity.noContent().build();
    }
}
