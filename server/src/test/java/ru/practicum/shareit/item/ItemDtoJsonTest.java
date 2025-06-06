package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoJsonTest {

    private final JacksonTester<ItemResponseDto> json;

    @Test
    void shouldSerializeItemDtoCorrectly() throws Exception {
        ItemResponseDto itemDto = ItemResponseDto.builder()
                .id(1L)
                .name("Name")
                .description("Cool description")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .comments(Collections.emptyList())
                .requestId(2L)
                .build();

        JsonContent<ItemResponseDto> result = json.write(itemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.comments");
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(itemDto.getRequestId()
                .intValue());
        assertThat(result).extractingJsonPathArrayValue("$.comments").isEmpty();
        assertThat(result).extractingJsonPathValue("$.lastBooking").isNull();
        assertThat(result).extractingJsonPathValue("$.nextBooking").isNull();
    }

    @Test
    void shouldDeserializeJsonToItemDtoCorrectly() throws Exception {
        String jsonString = "{ " +
                "\"id\": 1, " +
                "\"name\": \"Name\", " +
                "\"description\": \"Cool description\", " +
                "\"available\": true, " +
                "\"lastBooking\": null, " +
                "\"nextBooking\": null, " +
                "\"comments\": [], " +
                "\"requestId\": 2 " +
                "}";

        ItemResponseDto itemDto = json.parse(jsonString).getObject();

        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Name");
        assertThat(itemDto.getDescription()).isEqualTo("Cool description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getLastBooking()).isNull();
        assertThat(itemDto.getNextBooking()).isNull();
        assertThat(itemDto.getComments()).isEmpty();
        assertThat(itemDto.getRequestId()).isEqualTo(2L);
    }

    @Test
    void shouldDeserializeWithNullFields() throws Exception {
        String jsonWithNulls = "{ " +
                "\"id\": 1, " +
                "\"name\": null, " +
                "\"description\": null, " +
                "\"available\": true, " +
                "\"lastBooking\": null, " +
                "\"nextBooking\": null, " +
                "\"comments\": null, " +
                "\"requestId\": null " +
                "}";

        ItemResponseDto itemDto = json.parse(jsonWithNulls).getObject();

        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isNull();
        assertThat(itemDto.getDescription()).isNull();
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getLastBooking()).isNull();
        assertThat(itemDto.getNextBooking()).isNull();
        assertThat(itemDto.getComments()).isNull();
        assertThat(itemDto.getRequestId()).isNull();
    }
}