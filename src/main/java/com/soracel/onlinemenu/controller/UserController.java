package com.soracel.onlinemenu.controller;

import com.soracel.onlinemenu.io.RegisterUserRequest;
import com.soracel.onlinemenu.io.UserDataResponse;
import com.soracel.onlinemenu.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping()
    public ResponseEntity<UserDataResponse> register(@RequestBody RegisterUserRequest request){
        UserDataResponse userDataResponse = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDataResponse);
    }
}
