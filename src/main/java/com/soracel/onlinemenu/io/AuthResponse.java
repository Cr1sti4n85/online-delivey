package com.soracel.onlinemenu.io;

import lombok.*;

@AllArgsConstructor
@Getter
@Builder
public class AuthResponse {
    private String email;
    private String token;
}
