package com.wellness.backend.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse
{
    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String role;
}
