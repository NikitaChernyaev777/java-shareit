package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.request.NewCommentRequestDto;
import ru.practicum.shareit.item.dto.request.NewItemRequestDto;
import ru.practicum.shareit.item.dto.request.UpdateItemRequestDto;
import ru.practicum.shareit.item.dto.response.CommentResponseDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    final NewItemRequestDto newItemDto = NewItemRequestDto.builder()
            .name("Item")
            .available(false)
            .description("description").requestId(2L).build();

    final UpdateItemRequestDto updateItemDto = UpdateItemRequestDto.builder()
            .id(1L)
            .available(true)
            .description("megaDescription").requestId(2L).build();

    final CommentResponseDto existingCommentDto = CommentResponseDto.builder()
            .id(1L)
            .text("MegaComment")
            .authorName("User")
            .created(LocalDateTime.of(2025, 5, 28, 21, 38, 0))
            .build();

    final ItemResponseDto createdItemDto = ItemResponseDto.builder()
            .id(1L)
            .name("Item")
            .available(false)
            .description("description")
            .comments(List.of(existingCommentDto)).requestId(2L).build();

    final ItemResponseDto anotherItemDto = ItemResponseDto.builder()
            .id(2L)
            .name("SuperName")
            .available(false)
            .description("SuperDescription").build();

    final ItemResponseDto itemDtoAfterUpdate = ItemResponseDto.builder()
            .id(1L)
            .name("MegaName")
            .available(true)
            .description("MegaDescription")
            .comments(List.of(existingCommentDto))
            .requestId(2L).build();

    final NewCommentRequestDto newCommentRequestDto = NewCommentRequestDto.builder()
            .text("NewMEgaComment")
            .build();

    final Long userId = 3L;
    final Long itemId = 1L;

    @Test
    void shouldCreateItemWhenRequestIsValid() throws Exception {
        when(itemService.createItem(userId, newItemDto))
                .thenReturn(createdItemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(newItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(createdItemDto.getId()))
                .andExpect(jsonPath("$.name").value(createdItemDto.getName()))
                .andExpect(jsonPath("$.available").value(createdItemDto.getAvailable()))
                .andExpect(jsonPath("$.description").value(createdItemDto.getDescription()))
                .andExpect(jsonPath("$.requestId").value(createdItemDto.getRequestId()));

    }

    @Test
    void shouldReturnAllItemsForGivenOwner() throws Exception {
        when(itemService.getItemsByOwnerId(userId)).thenReturn(List.of(createdItemDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(createdItemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(createdItemDto.getName()))
                .andExpect(jsonPath("$[0].available").value(createdItemDto.getAvailable()))
                .andExpect(jsonPath("$[0].description").value(createdItemDto.getDescription()))
                .andExpect(jsonPath("$[0].comments", hasSize(1)))
                .andExpect(jsonPath("$[0].comments[0].id").value(existingCommentDto.getId()))
                .andExpect(jsonPath("$[0].comments[0].text").value(existingCommentDto.getText()))
                .andExpect(jsonPath("$[0].comments[0].authorName").value(existingCommentDto.getAuthorName()))
                .andExpect(jsonPath("$[0].requestId").value(createdItemDto.getRequestId()));
    }

    @Test
    void shouldReturnItemByIdWhenExists() throws Exception {
        when(itemService.getItemById(userId, itemId))
                .thenReturn(createdItemDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdItemDto.getId()))
                .andExpect(jsonPath("$.name").value(createdItemDto.getName()))
                .andExpect(jsonPath("$.available").value(createdItemDto.getAvailable()))
                .andExpect(jsonPath("$.description").value(createdItemDto.getDescription()))
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].id").value(existingCommentDto.getId()))
                .andExpect(jsonPath("$.comments[0].text").value(existingCommentDto.getText()))
                .andExpect(jsonPath("$.comments[0].authorName").value(existingCommentDto.getAuthorName()))
                .andExpect(jsonPath("$.requestId").value(createdItemDto.getRequestId()));
    }

    @Test
    void shouldUpdateItemWhenDataIsValid() throws Exception {
        when(itemService.updateItem(userId, itemId, updateItemDto))
                .thenReturn(itemDtoAfterUpdate);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(updateItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoAfterUpdate.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoAfterUpdate.getName()))
                .andExpect(jsonPath("$.available").value(itemDtoAfterUpdate.getAvailable()))
                .andExpect(jsonPath("$.description").value(itemDtoAfterUpdate.getDescription()))
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].id").value(existingCommentDto.getId()))
                .andExpect(jsonPath("$.comments[0].text").value(existingCommentDto.getText()))
                .andExpect(jsonPath("$.comments[0].authorName").value(existingCommentDto.getAuthorName()))
                .andExpect(jsonPath("$.requestId").value(itemDtoAfterUpdate.getRequestId()));
    }

    @Test
    void shouldReturnItemsMatchingSearchText() throws Exception {
        String searchText = "2";
        when(itemService.search(userId, searchText))
                .thenReturn(List.of(anotherItemDto));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", searchText)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(anotherItemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(anotherItemDto.getName()))
                .andExpect(jsonPath("$[0].available").value(anotherItemDto.getAvailable()))
                .andExpect(jsonPath("$[0].description").value(anotherItemDto.getDescription()))
                .andExpect(jsonPath("$[0].requestId").value(anotherItemDto.getRequestId()));
    }

    @Test
    void shouldReturnEmptyListWhenNoItemsMatchSearchText() throws Exception {
        String searchText = "non-existent";
        when(itemService.search(userId, searchText))
                .thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", searchText)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldAddCommentToItemSuccessfully() throws Exception {
        when(itemService.createComment(userId, itemId, newCommentRequestDto))
                .thenReturn(existingCommentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(newCommentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(existingCommentDto.getId()))
                .andExpect(jsonPath("$.text").value(existingCommentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(existingCommentDto.getAuthorName()))
                .andExpect(jsonPath("$.created").value("2025-05-28T21:38:00"));
    }

    @Test
    void shouldReturnNotFoundWhenItemDoesNotExist() throws Exception {
        when(itemService.getItemById(userId, itemId)).thenThrow(new NotFoundException("Item not found"));

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Ресурс не найден"));
    }
}