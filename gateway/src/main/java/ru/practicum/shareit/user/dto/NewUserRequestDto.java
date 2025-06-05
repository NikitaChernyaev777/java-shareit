package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequestDto {

    @NotBlank(message = "Имя пользователя не должно быть пустым")
    @Size(max = 255, message = "Имя пользователя не должно превышать 255 символов")
    private String name;

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email указан в некорректном формате")
    @Size(max = 255, message = "Email не должен превышать 255 символов")
    private String email;
}