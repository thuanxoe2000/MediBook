package com.example.MediBook.mapper;

import com.example.MediBook.dto.request.PermissionRequest;
import com.example.MediBook.dto.response.PermissionResponse;
import com.example.MediBook.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
