package com.openfuture.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequest {

    private String password;
    private String confirmPassword;
}
