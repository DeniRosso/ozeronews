package com.example.ozeronews.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
public class Contact {

    @Size(max=100, message = "Максимальная длина 100 символом")
    @Email(message = "Некорректный адресс электронной почты")
    private String email;
    @Size(max=100, message = "Максимальная длина 100 символом")
    private String subject;
    @Size(max=100, message = "Максимальная длина 100 символом")
    private String name;
    @Size(max=2000, message = "Максимальная длина 100 символом")
    private String text;

    public Contact() {
    }
}
