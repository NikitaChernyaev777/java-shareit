package ru.practicum.shareit.item.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class UpdateItemRequestDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;

    public boolean hasName() {
        return isNotBlank(name);
    }

    public boolean hasDescription() {
        return isNotBlank(description);
    }

    public boolean hasAvailable() {
        return !(available == null);
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isEmpty();
    }
}