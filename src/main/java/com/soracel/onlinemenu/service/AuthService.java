package com.soracel.onlinemenu.service;

import org.springframework.security.core.Authentication;

public interface AuthService {

    Authentication getAuthentication();
}
