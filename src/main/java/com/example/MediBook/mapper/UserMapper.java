package com.example.MediBook.mapper;

import com.example.MediBook.dto.request.SignupRequest;
import com.example.MediBook.dto.request.UpdateRequest;
import com.example.MediBook.dto.response.UserResponse;
import com.example.MediBook.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(SignupRequest request);
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles",ignore = true)
    void updateUser(UpdateRequest request, @MappingTarget User user);
}
