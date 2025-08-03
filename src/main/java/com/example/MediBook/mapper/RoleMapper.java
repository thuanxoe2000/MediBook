package com.example.MediBook.mapper;

import com.example.MediBook.dto.request.RoleRequest;
import com.example.MediBook.dto.response.RoleResponse;
import com.example.MediBook.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions",ignore = true)
    Role toRole(RoleRequest request);
    RoleResponse toRoleResponse(Role role);
}
