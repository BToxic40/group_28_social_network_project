package ru.skillbox.dto;

import lombok.Getter;
import lombok.Setter;
import ru.skillbox.dto.enums.MessagePermission;

@Getter
@Setter
public class AccountDto {

    private Long id;
    private String email;
    private String phone;
    private String photo;
    private String about;
    private CityDto city;
    private CountryDto country;
    private String token;
    private String firstName;
    private String lastName;
    private Long regDate;
    private Long birthDate;
    private MessagePermission messagePermission;
    private Long lastOnlineTime;
    private Boolean isOnline;
    private Boolean isBlocked;
    private Boolean isDeleted;
}