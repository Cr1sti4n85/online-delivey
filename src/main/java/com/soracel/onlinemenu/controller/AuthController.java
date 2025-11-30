package com.soracel.onlinemenu.controller;

import com.soracel.onlinemenu.io.AuthRequest;
import com.soracel.onlinemenu.io.AuthResponse;
import com.soracel.onlinemenu.service.AppUserDetailsService;
import com.soracel.onlinemenu.utils.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req){
        authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                req.getEmail(),
                req.getPassword()
        ));

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(req.getEmail());

        final String jwtToken = jwtUtil.generateToken(userDetails);
        AuthResponse authResponse = AuthResponse
                .builder()
                .email(req.getEmail())
                .token(jwtToken)
                .build();
        return ResponseEntity.ok(authResponse);
    }
}






















