package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.request.NewCommentRequestDto;
import ru.practicum.shareit.item.dto.request.NewItemRequestDto;
import ru.practicum.shareit.item.dto.request.UpdateItemRequestDto;
import ru.practicum.shareit.item.dto.response.CommentResponseDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemResponseDto createItem(Long userId, NewItemRequestDto newItemDto) {
        log.info("Создание новой вещи c name={} пользователем с userId={}", newItemDto.getName(), userId);

        User owner = userService.getExistingUser(userId);
        Item item = itemMapper.toNewItem(newItemDto);
        item.setOwner(owner);

        if (newItemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(newItemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(String.format("Запрос с id=%d не найден",
                            newItemDto.getRequestId())));
            item.setRequest(itemRequest);
        }

        Item savedItem = itemRepository.save(item);
        log.info("Вещь успешно создана: id={}, name={}, ownerId={}", savedItem.getId(), savedItem.getName(), userId);

        return itemMapper.toItemResponseDto(savedItem);
    }

    @Override
    public List<ItemResponseDto> getItemsByOwnerId(Long ownerId) {
        log.info("Запрошен список всех вещей владельца с ownerId={}", ownerId);
        userService.getExistingUser(ownerId);

        return itemRepository.findByOwnerIdOrderByIdAsc(ownerId).stream()
                .map(item -> {
                    ItemResponseDto responseDto = itemMapper.toItemResponseDto(item);
                    responseDto.setComments(getCommentsForItem(item.getId()));
                    return responseDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto getItemById(Long userId, Long itemId) {
        log.info("Запрошена вещь с id={}", itemId);
        Item item = getExistingItem(itemId);

        ItemResponseDto responseDto = itemMapper.toItemResponseDto(item);
        responseDto.setComments(getCommentsForItem(item.getId()));

        return responseDto;
    }

    @Override
    public List<ItemResponseDto> search(Long userId, String text) {
        userService.getExistingUser(userId);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        log.info("Запрошен список вещей по соответствию тексту: '{}'", text);
        return itemRepository.findByNameOrDescription(text).stream()
                .map(itemMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemResponseDto updateItem(Long userId, Long itemId, UpdateItemRequestDto updateItemDto) {
        log.info("Обновление существующей вещи c id={} пользователем с userId={}", itemId, userId);

        userService.getExistingUser(userId);
        Item existingItem = getExistingItem(itemId);

        if (!existingItem.getOwner().getId().equals(userId)) {
            log.warn("Попытка обновления вещи с id={}, которая не принадлежит userId={}", itemId, userId);
            throw new ValidationException(String.format("Вещь с id=%d не принадлежит данному пользователю",
                    existingItem.getId()));

        }

        itemMapper.updateItem(updateItemDto, existingItem);
        itemRepository.save(existingItem);
        log.info("Вещь с id={} успешно обновлена", existingItem.getId());

        return itemMapper.toItemResponseDto(existingItem);
    }

    @Override
    public Item getExistingItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id=%d не найдена", itemId)));
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(Long userId, Long itemId, NewCommentRequestDto newCommentDto) {
        log.info("Добавление комментария к вещи с id={} от пользователя с id={}", itemId, userId);

        User author = userService.getExistingUser(userId);
        Item item = getExistingItem(itemId);

        boolean hasBooked = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, itemId,
                LocalDateTime.now());

        if (!hasBooked) {
            log.warn("Пользователь с id={} не арендовал вещь с id={}, комментарий не может быть добавлен",
                    userId, itemId);
            throw new ValidationException("Нельзя оставить комментарий без завершённой аренды");
        }

        Comment comment = commentMapper.toNewComment(newCommentDto, item, author);
        Comment savedComment = commentRepository.save(comment);
        log.info("Комментарий успешно добавлен: id={}, itemId={}, authorId={}", savedComment.getId(), itemId, userId);

        return commentMapper.toCommentResponseDto(savedComment);
    }

    private List<CommentResponseDto> getCommentsForItem(Long itemId) {
        return commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::toCommentResponseDto)
                .toList();
    }
}