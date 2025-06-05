package ru.practicum.shareit.booking.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(Long userId, NewBookingRequestDto newBookingDto) {
        return post("", userId, newBookingDto);
    }

    public ResponseEntity<Object> getBookingsByOwnerId(Long ownerId, BookingState state) {
        Map<String, Object> parameters = Map.of("state", state.name());
        return get("/owner", ownerId, parameters);
    }

    public ResponseEntity<Object> getBookingsByBookerId(Long bookerId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("state", state, "from", from, "size", size);
        return get("?state={state}&from={from}&size={size}", bookerId, parameters);
    }

    public ResponseEntity<Object> getBookingById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> updateBookingStatus(Long userId, Long bookingId, Boolean approved) {
        return patch(("/" + bookingId + "?approved=" + approved), userId, null);
    }
}