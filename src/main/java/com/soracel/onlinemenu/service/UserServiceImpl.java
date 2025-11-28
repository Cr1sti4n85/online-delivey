package com.soracel.onlinemenu.service;

import com.soracel.onlinemenu.entity.UserEntity;
import com.soracel.onlinemenu.io.RegisterUserRequest;
import com.soracel.onlinemenu.io.UserDataResponse;
import com.soracel.onlinemenu.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService{

     private final UserRepository userRepository;

    @Override
    public UserDataResponse registerUser(RegisterUserRequest request) {
        UserEntity newUser =  convertToEntity((request));
        newUser = userRepository.save(newUser);
        return convertToResponse(newUser);
    }

    private UserEntity convertToEntity(RegisterUserRequest req){
        return UserEntity.builder()
                .email(req.getEmail())
                .password(req.getPassword())
                .name(req.getName())
                .build();
    }

    private UserDataResponse convertToResponse(UserEntity user){
        return UserDataResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
