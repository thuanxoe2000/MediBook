package com.example.MediBook.service;

import com.example.MediBook.dto.request.RoleRequest;
import com.example.MediBook.dto.response.RoleResponse;
import com.example.MediBook.entity.Role;
import com.example.MediBook.mapper.RoleMapper;
import com.example.MediBook.repository.PermissionRepository;
import com.example.MediBook.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request){
        Role role= roleMapper.toRole(request);

        var permissions=permissionRepository.findAllById(request.getPermission());
        role.setPermissions( new HashSet<>(permissions));

        role=roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll(){
        return roleRepository
                .findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .collect(Collectors.toList());
    }

    public void delete(String role){
        roleRepository.deleteById(role);
    }
}
