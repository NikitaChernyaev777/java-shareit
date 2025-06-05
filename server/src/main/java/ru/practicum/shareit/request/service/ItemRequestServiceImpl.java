package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.request.NewItemRequestRequestDto;
import ru.practicum.shareit.request.dto.response.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestResponseDto createItemRequest(Long userId, NewItemRequestRequestDto newItemRequestDto) {
        log.info("Создание нового запроса пользователем с userId={}", userId);

        User requestor = userService.getExistingUser(userId);
        ItemRequest itemRequest = itemRequestMapper.toNewItemRequest(newItemRequestDto);
        itemRequest.setRequestor(requestor);

        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        log.info("Запрос успешно создан: id={}, requestorId={}", savedItemRequest.getId(), userId);

        return itemRequestMapper.toItemRequestResponseDto(savedItemRequest);
    }

    @Override
    public List<ItemRequestResponseDto> getOwnItemRequests(Long requestorId) {
        log.info("Получение списка всех запросов пользователя с id={}", requestorId);

        userService.getExistingUser(requestorId);
        return itemRequestRepository.findByRequestorId(requestorId, Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .map(request -> {
                    ItemRequestResponseDto itemRequestDto = itemRequestMapper.toItemRequestResponseDto(request);
                    populateItems(itemRequestDto);
                    return itemRequestDto;
                })
                .toList();
    }

    @Override
    public List<ItemRequestResponseDto> getAllOtherUsersItemRequests(Long userId, Integer from, Integer size) {
        log.info("Получение списка запросов, созданных другими пользователями (инициатор запроса: userId={})", userId);

        userService.getExistingUser(userId);

        if (from < 0 || size <= 0) {
            throw new ValidationException("Параметры from и size должны быть положительными");
        }

        PageRequest pageRequest = PageRequest.of(from / size, size,
                Sort.by(Sort.Direction.DESC, "created"));

        return itemRequestRepository
                .findByRequestorIdNot(userId, pageRequest)
                .stream()
                .map(request -> {
                    ItemRequestResponseDto itemRequestDto = itemRequestMapper.toItemRequestResponseDto(request);
                    populateItems(itemRequestDto);
                    return itemRequestDto;
                })
                .toList();
    }

    @Override
    public ItemRequestResponseDto getItemRequestById(Long userId, Long requestId) {
        log.info("Пользователь с id={} запрашивает данные о запросе с id={}", userId, requestId);

        userService.getExistingUser(userId);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Запрос с id=%d не найден", requestId)));

        ItemRequestResponseDto itemRequestDto = itemRequestMapper.toItemRequestResponseDto(itemRequest);
        populateItems(itemRequestDto);

        return itemRequestDto;
    }

    private void populateItems(ItemRequestResponseDto itemRequestDto) {
        Long requestId = itemRequestDto.getId();
        List<Item> items = itemRepository.findByRequestId(requestId);
        List<ItemResponseDto> itemDtos = items.stream()
                .map(itemMapper::toItemResponseDto)
                .toList();
        itemRequestDto.setItems(itemDtos);
    }
}