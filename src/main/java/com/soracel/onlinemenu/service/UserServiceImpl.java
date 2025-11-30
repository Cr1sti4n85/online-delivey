package com.soracel.onlinemenu.service;

import com.soracel.onlinemenu.entity.UserEntity;
import com.soracel.onlinemenu.io.RegisterUserRequest;
import com.soracel.onlinemenu.io.UserDataResponse;
import com.soracel.onlinemenu.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService{

     private final UserRepository userRepository;
     private final PasswordEncoder passwordEncoder;
     private final AuthService authService;

    @Override
    public UserDataResponse registerUser(RegisterUserRequest request) {
        UserEntity newUser =  convertToEntity((request));
        newUser = userRepository.save(newUser);
        return convertToResponse(newUser);
    }

    @Override
    public String findByUserId() {
        String loggedInEmail = authService.getAuthentication().getName();
        var loggedInUser = userRepository
                        .findByEmail(loggedInEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return loggedInUser.getId();
    }

    private UserEntity convertToEntity(RegisterUserRequest req){
        return UserEntity.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
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
