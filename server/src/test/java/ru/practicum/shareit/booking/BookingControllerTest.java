package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.request.NewBookingRequestDto;
import ru.practicum.shareit.booking.dto.response.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.response.ItemShortResponseDto;
import ru.practicum.shareit.user.dto.response.UserResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    NewBookingRequestDto newBookingDto;
    BookingResponseDto waitingBooking;
    BookingResponseDto approvedBooking;

    @BeforeEach
    void beforeEach() {
        newBookingDto = NewBookingRequestDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(1L)
                .build();

        UserResponseDto booker = UserResponseDto.builder()
                .id(1L).name("User")
                .email("user@mail.com")
                .build();

        ItemShortResponseDto item = ItemShortResponseDto.builder()
                .id(1L).name("Item")
                .available(true)
                .build();

        waitingBooking = BookingResponseDto.builder()
                .id(1L)
                .start(newBookingDto.getStart())
                .end(newBookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        approvedBooking = BookingResponseDto.builder()
                .id(1L)
                .start(newBookingDto.getStart())
                .end(newBookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void shouldReturn201AndBookingDto_WhenBookingIsCreated() throws Exception {
        when(bookingService.createBooking(1L, newBookingDto)).thenReturn(waitingBooking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("Item"))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.booker.name").value("User"))
                .andExpect(jsonPath("$.booker.email").value("user@mail.com"))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void shouldReturnApprovedBooking_WhenStatusIsUpdated() throws Exception {
        when(bookingService.updateBookingStatus(1L, 1L, true)).thenReturn(approvedBooking);

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("Item"))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.booker.name").value("User"))
                .andExpect(jsonPath("$.booker.email").value("user@mail.com"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldReturnOkAndBookingDetails_WhenGetBookingById() throws Exception {
        when(bookingService.getBookingById(1L, 1L)).thenReturn(waitingBooking);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("Item"))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.booker.name").value("User"))
                .andExpect(jsonPath("$.booker.email").value("user@mail.com"))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }


    @Test
    void shouldReturnAllBookings_WhenBookerRequestsWithStateAll() throws Exception {
        List<BookingResponseDto> bookerBookings = List.of(waitingBooking);

        when(bookingService.getBookingsByBookerId(1L, BookingState.ALL)).thenReturn(bookerBookings);

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("Item"))
                .andExpect(jsonPath("$[0].booker.id").value(1L))
                .andExpect(jsonPath("$[0].booker.name").value("User"))
                .andExpect(jsonPath("$[0].booker.email").value("user@mail.com"))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void shouldReturnAllBookings_WhenOwnerRequestsWithStateAll() throws Exception {
        List<BookingResponseDto> ownerBookings = List.of(waitingBooking);

        when(bookingService.getBookingsByOwnerId(1L, BookingState.ALL)).thenReturn(ownerBookings);

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("Item"))
                .andExpect(jsonPath("$[0].booker.id").value(1L))
                .andExpect(jsonPath("$[0].booker.name").value("User"))
                .andExpect(jsonPath("$[0].booker.email").value("user@mail.com"))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }
}