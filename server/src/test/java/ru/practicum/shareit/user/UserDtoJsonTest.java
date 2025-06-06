package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.dto.response.UserResponseDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoJsonTest {

    private final JacksonTester<UserResponseDto> json;

    @Test
    void shouldSerializeUserDtoCorrectly() throws Exception {
        UserResponseDto dto = UserResponseDto.builder()
                .id(1L)
                .name("User")
                .email("user@mail.com")
                .build();

        JsonContent<UserResponseDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(dto.getEmail());
    }

    @Test
    void shouldDeserializeJsonToUserDtoCorrectly() throws Exception {
        String jsonString = "{ \"id\": 1, \"name\": \"User\", \"email\": \"user@mail.com\" }";

        UserResponseDto dto = json.parse(jsonString).getObject();

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("User");
        assertThat(dto.getEmail()).isEqualTo("user@mail.com");
    }
}