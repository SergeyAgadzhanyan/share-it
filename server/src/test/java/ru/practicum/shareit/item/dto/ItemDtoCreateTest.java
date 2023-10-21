package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemDtoCreateTest {
    private final JacksonTester<ItemDtoCreate> json;
    private final JacksonTester<ItemDtoWithRequestId> jsonWithId;

    @Test
    void dtoTest() throws IOException {
        ItemDtoCreate itemDtoCreate = new ItemDtoCreate("n", "d", true, 1L);
        ItemDtoWithRequestId itemDtoWithRequestId = new ItemDtoWithRequestId(1L, itemDtoCreate.getName(),
                itemDtoCreate.getDescription(), itemDtoCreate.getRequestId(), true);

        JsonContent<ItemDtoCreate> resultDto = json.write(itemDtoCreate);
        JsonContent<ItemDtoWithRequestId> resultDtoWithId = jsonWithId.write(itemDtoWithRequestId);

        assertThat(resultDto).extractingJsonPathStringValue("$.name").isEqualTo(itemDtoCreate.getName());
        assertThat(resultDto).extractingJsonPathStringValue("$.description").isEqualTo(itemDtoCreate.getDescription());
        assertThat(resultDto).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDtoCreate.getAvailable());
        assertThat(resultDto).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);

        assertThat(resultDtoWithId).extractingJsonPathStringValue("$.name").isEqualTo(itemDtoCreate.getName());
        assertThat(resultDtoWithId).extractingJsonPathStringValue("$.description").isEqualTo(itemDtoCreate.getDescription());
        assertThat(resultDtoWithId).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDtoCreate.getAvailable());
        assertThat(resultDtoWithId).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

}
