package com.soracel.onlinemenu.service;

import com.soracel.onlinemenu.io.RegisterUserRequest;
import com.soracel.onlinemenu.io.UserDataResponse;

public interface UserService {

    UserDataResponse registerUser(RegisterUserRequest request);

    String findByUserId();
}
