package ru.practicum.shareit.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class NewUserRequestDto {

    @NotBlank(message = "Имя пользователя не должно быть пустым")
    private String name;

    @Email(message = "Email указан в некорректном формате")
    @NotBlank(message = "Email не должен быть пустым")
    private String email;
}