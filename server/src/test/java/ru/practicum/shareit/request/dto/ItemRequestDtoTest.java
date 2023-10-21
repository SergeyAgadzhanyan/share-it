package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestDtoTest {

    private final JacksonTester<ItemRequestDto> jsonDto;
    private final JacksonTester<ItemRequestDtoGet> jsonDtoGet;

    @Test
    void dtoTest() throws IOException {
        LocalDateTime dateTime = LocalDateTime.now();
        ItemRequestDto itemRequestDto = new ItemRequestDto("testDesc");
        ItemRequestDtoGet itemRequestDtoGet = new ItemRequestDtoGet(1L, "d", dateTime);

        JsonContent<ItemRequestDto> resultDto = jsonDto.write(itemRequestDto);
        JsonContent<ItemRequestDtoGet> resultDtoGet = jsonDtoGet.write(itemRequestDtoGet);

        assertThat(resultDto).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());
        assertThat(resultDtoGet).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(resultDtoGet).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDtoGet.getDescription());
    }

}
