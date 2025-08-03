package com.example.MediBook.service;

import com.example.MediBook.dto.request.PermissionRequest;
import com.example.MediBook.dto.response.PermissionResponse;
import com.example.MediBook.entity.Permission;
import com.example.MediBook.mapper.PermissionMapper;
import com.example.MediBook.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request){
        Permission permission=permissionMapper.toPermission(request);
        permission=permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll(){
        return permissionRepository
                .findAll()
                .stream()
                .map(permissionMapper::toPermissionResponse)
                .collect(Collectors.toList());
    }

    public void delete(String permission){
        permissionRepository.deleteById(permission);
    }
}
