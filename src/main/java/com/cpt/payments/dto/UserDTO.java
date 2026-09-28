package com.cpt.payments.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String endUserID;
    private String firstname;
    private String lastname;
    private String email;
    private String mobilePhone;
}
