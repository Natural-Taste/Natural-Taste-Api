package com.naturaltaste.recommend.presentation.restaurant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void searchSaveListAndCancelSavedRestaurant() throws Exception {
        String accessToken = signupAndReadToken("restaurant-user@example.com");

        mockMvc.perform(get("/restaurants/search")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("query", "초밥"))
                .andExpect(status().isOk());

        String saveResponse = mockMvc.perform(post("/restaurants/saved")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "provider": "KAKAO",
                                  "providerPlaceId": "1",
                                  "name": "초밥집",
                                  "address": "서울시 강남구",
                                  "latitude": 37.1234567,
                                  "longitude": 127.1234567,
                                  "category": "음식점 > 일식",
                                  "phone": "02-000-0000",
                                  "placeUrl": "https://place.map.kakao.com/1"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saved").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long restaurantId = objectMapper.readTree(saveResponse).get("id").longValue();

        mockMvc.perform(get("/restaurants/saved")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(restaurantId));

        mockMvc.perform(delete("/restaurants/saved/{restaurantId}", restaurantId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());
    }

    private String signupAndReadToken(String email) throws Exception {
        String response = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "password1234",
                                  "name": "사용자"
                                }
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.get("accessToken").asText();
    }
}
