package ru.practicum.shareit.item.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class NewItemRequestDto {

    @NotBlank(message = "Название вещи не должно быть пустым")
    @Size(max = 255, message = "Название вещи не должно превышать 255 символов")
    private String name;

    @NotBlank(message = "Описание вещи не должно быть пустым")
    @Size(max = 2000, message = "Описание вещи не должно превышать 2000 символов")
    private String description;

    @NotNull(message = "Статус аренды вещи должен быть указан")
    private Boolean available;
}