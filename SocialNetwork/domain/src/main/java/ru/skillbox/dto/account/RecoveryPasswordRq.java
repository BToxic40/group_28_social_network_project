package ru.skillbox.dto.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecoveryPasswordRq {

    private String temp;
    private String password;
}
