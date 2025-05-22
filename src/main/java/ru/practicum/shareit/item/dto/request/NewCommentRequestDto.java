package ru.practicum.shareit.item.dto.request;

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
public class NewCommentRequestDto {

    @NotBlank(message = "Комментарий не должен быть пустым")
    @Size(max = 2000, message = "Комментарий не должен превышать 2000 символов")
    private String text;
}