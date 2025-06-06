package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.request.NewItemRequestRequestDto;
import ru.practicum.shareit.request.dto.response.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    final NewItemRequestRequestDto newItemRequest = NewItemRequestRequestDto.builder()
            .description("description")
            .build();

    final ItemResponseDto firstItemDto = ItemResponseDto.builder()
            .id(1L)
            .name("name1")
            .available(true)
            .description("description1")
            .build();

    final ItemResponseDto secondItemDto = ItemResponseDto.builder()
            .id(2L)
            .name("name2")
            .available(true)
            .description("description2")
            .build();

    final ItemRequestResponseDto expectedRequestDto = ItemRequestResponseDto.builder()
            .id(1L)
            .description("description")
            .created(LocalDateTime.of(2025, 5, 28, 21, 38, 0))
            .requestorName("requesterName")
            .items(List.of(firstItemDto, secondItemDto))
            .build();

    final ItemRequestResponseDto requestDto2 = ItemRequestResponseDto.builder()
            .id(2L)
            .description("description2")
            .created(LocalDateTime.of(2025, 4, 28, 21, 38, 5))
            .requestorName("requesterName2").items(List.of(firstItemDto))
            .build();

    final Long userId = 3L;

    @Test
    void shouldCreateItemRequestAndReturnCreatedDto() throws Exception {
        when(itemRequestService.createItemRequest(userId, newItemRequest))
                .thenReturn(expectedRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(newItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(expectedRequestDto.getDescription()))
                .andExpect(jsonPath("$.requestorName").value(expectedRequestDto.getRequestorName()))
                .andExpect(jsonPath("$.created").value("2025-05-28T21:38:00"))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id").value(firstItemDto.getId()))
                .andExpect(jsonPath("$.items[1].id").value(secondItemDto.getId()));
    }

    @Test
    void shouldReturnAllRequestsCreatedByUser() throws Exception {
        when(itemRequestService.getOwnItemRequests(userId))
                .thenReturn(List.of(expectedRequestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value(expectedRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].requestorName").value(expectedRequestDto.getRequestorName()))
                .andExpect(jsonPath("$[0].created").value("2025-05-28T21:38:00"))
                .andExpect(jsonPath("$[0].items", hasSize(2)))
                .andExpect(jsonPath("$[0].items[0].id").value(firstItemDto.getId()))
                .andExpect(jsonPath("$[0].items[1].id").value(secondItemDto.getId()));
    }

    @Test
    void shouldReturnAllItemRequestsFromOtherUsers() throws Exception {
        when(itemRequestService.getAllOtherUsersItemRequests(userId, 0, 10))
                .thenReturn(List.of(expectedRequestDto, requestDto2));
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(expectedRequestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(expectedRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].requestorName").value(expectedRequestDto.getRequestorName()))
                .andExpect(jsonPath("$[0].created").value("2025-05-28T21:38:00"))
                .andExpect(jsonPath("$[0].items", hasSize(2)))
                .andExpect(jsonPath("$[0].items[0].id").value(firstItemDto.getId()))
                .andExpect(jsonPath("$[0].items[1].id").value(secondItemDto.getId()))
                .andExpect(jsonPath("$[1].id").value(requestDto2.getId()))
                .andExpect(jsonPath("$[1].description").value(requestDto2.getDescription()))
                .andExpect(jsonPath("$[1].requestorName").value(requestDto2.getRequestorName()))
                .andExpect(jsonPath("$[1].created").value("2025-04-28T21:38:05"))
                .andExpect(jsonPath("$[1].items", hasSize(1)))
                .andExpect(jsonPath("$[1].items[0].id").value(firstItemDto.getId()));
    }

    @Test
    void shouldReturnItemRequestById() throws Exception {
        Long requestId = 1L;
        when(itemRequestService.getItemRequestById(userId, requestId))
                .thenReturn(expectedRequestDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(expectedRequestDto.getDescription()))
                .andExpect(jsonPath("$.requestorName").value(expectedRequestDto.getRequestorName()))
                .andExpect(jsonPath("$.created").value("2025-05-28T21:38:00"))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id").value(firstItemDto.getId()))
                .andExpect(jsonPath("$.items[1].id").value(secondItemDto.getId()));
    }
}