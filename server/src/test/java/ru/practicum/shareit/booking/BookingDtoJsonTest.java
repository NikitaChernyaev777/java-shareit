package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.response.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.response.ItemShortResponseDto;
import ru.practicum.shareit.user.dto.response.UserResponseDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {

    private final JacksonTester<BookingResponseDto> json;

    @Test
    void shouldSerializeBookingDtoCorrectly() throws Exception {
        UserResponseDto booker = UserResponseDto.builder()
                .id(1L)
                .name("Booker1")
                .email("booker1@mail.com")
                .build();

        ItemShortResponseDto item = ItemShortResponseDto.builder()
                .id(2L)
                .name("Item1")
                .available(true)
                .requestId(3L)
                .build();

        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 10, 20, 12, 0))
                .end(LocalDateTime.of(2025, 11, 20, 12, 0))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        JsonContent<BookingResponseDto> result = json.write(bookingResponseDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.status");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingResponseDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingResponseDto.getStatus().toString());

        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(booker.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.booker.name")
                .isEqualTo(booker.getName());
        assertThat(result).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo(booker.getEmail());

        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(item.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.item.name")
                .isEqualTo(item.getName());
        assertThat(result).extractingJsonPathBooleanValue("$.item.available")
                .isEqualTo(item.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId")
                .isEqualTo(item.getRequestId().intValue());
    }

    @Test
    void shouldDeserializeJsonToBookingDtoCorrectly() throws Exception {
        String jsonString = "{ " +
                "\"id\": 1, " +
                "\"start\": \"2025-10-20T12:00:00\", " +
                "\"end\": \"2025-11-20T12:00:00\", " +
                "\"item\": { \"id\": 2, \"name\": \"Item1\",  \"available\": true, \"requestId\": 3 }, " +
                "\"booker\": { \"id\": 1, \"name\": \"Booker1\", \"email\": \"booker1@mail.com\" }, " +
                "\"status\": \"APPROVED\" " +
                "}";

        BookingResponseDto bookingResponseDto = json.parse(jsonString).getObject();

        assertThat(bookingResponseDto).isNotNull();
        assertThat(bookingResponseDto.getId()).isEqualTo(1L);
        assertThat(bookingResponseDto.getStart()).isEqualTo(LocalDateTime.of(
                2025, 10, 20, 12, 0));
        assertThat(bookingResponseDto.getEnd()).isEqualTo(LocalDateTime.of(
                2025, 11, 20, 12, 0));
        assertThat(bookingResponseDto.getStatus()).isEqualTo(BookingStatus.APPROVED);

        assertThat(bookingResponseDto.getBooker()).isNotNull();
        assertThat(bookingResponseDto.getBooker().getId()).isEqualTo(1L);
        assertThat(bookingResponseDto.getBooker().getName()).isEqualTo("Booker1");
        assertThat(bookingResponseDto.getBooker().getEmail()).isEqualTo("booker1@mail.com");

        assertThat(bookingResponseDto.getItem()).isNotNull();
        assertThat(bookingResponseDto.getItem().getId()).isEqualTo(2L);
        assertThat(bookingResponseDto.getItem().getName()).isEqualTo("Item1");
        assertThat(bookingResponseDto.getItem().getAvailable()).isTrue();
        assertThat(bookingResponseDto.getItem().getRequestId()).isEqualTo(3L);
    }

    @Test
    void shouldDeserializeWithNullFields() throws Exception {
        String jsonWithNulls = "{ " +
                "\"id\": 1, " +
                "\"start\": null, " +
                "\"end\": null, " +
                "\"item\": null, " +
                "\"booker\": null, " +
                "\"status\": \"APPROVED\" " +
                "}";

        BookingResponseDto booking = json.parse(jsonWithNulls).getObject();

        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isEqualTo(1L);
        assertThat(booking.getStart()).isNull();
        assertThat(booking.getEnd()).isNull();
        assertThat(booking.getItem()).isNull();
        assertThat(booking.getBooker()).isNull();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }
}