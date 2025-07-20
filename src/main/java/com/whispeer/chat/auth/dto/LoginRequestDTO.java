package com.whispeer.chat.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Builder
public class LoginRequestDTO {

    private String userId;
    private String password;

    public LoginRequestDTO(String userId, String password) {
        this.userId = userId;
        this.password = password;
    } // constructor

    @Override
    public String toString() {
        return "LoginRequestDTO{" +
                "userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                '}';
    } // toString

} // end class
