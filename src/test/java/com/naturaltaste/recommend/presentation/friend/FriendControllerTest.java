package com.naturaltaste.recommend.presentation.friend;

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
class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void searchRequestAcceptAndFindFriendRestaurants() throws Exception {
        AuthFixture user = signup("friend-user@example.com", "사용자");
        AuthFixture friend = signup("friend-target@example.com", "친구");

        mockMvc.perform(get("/users/search")
                        .header("Authorization", "Bearer " + user.accessToken())
                        .param("query", "친구"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(friend.userId()));

        String requestResponse = mockMvc.perform(post("/friends/requests")
                        .header("Authorization", "Bearer " + user.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "receiverId": %d
                                }
                                """.formatted(friend.userId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.requester.id").value(user.userId()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long requestId = objectMapper.readTree(requestResponse).get("id").longValue();

        mockMvc.perform(get("/friends/requests/received")
                        .header("Authorization", "Bearer " + friend.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestId));

        mockMvc.perform(post("/friends/requests/{requestId}/accept", requestId)
                        .header("Authorization", "Bearer " + friend.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.userId()));

        mockMvc.perform(get("/friends")
                        .header("Authorization", "Bearer " + user.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(friend.userId()));

        saveRestaurant(friend.accessToken());

        mockMvc.perform(get("/friends/{friendId}/restaurants/saved", friend.userId())
                        .header("Authorization", "Bearer " + user.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("친구 초밥집"));
    }

    @Test
    void rejectRequestRemovesReceivedRequest() throws Exception {
        AuthFixture user = signup("reject-user@example.com", "요청자");
        AuthFixture friend = signup("reject-target@example.com", "수신자");
        String requestResponse = mockMvc.perform(post("/friends/requests")
                        .header("Authorization", "Bearer " + user.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "receiverId": %d
                                }
                                """.formatted(friend.userId())))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long requestId = objectMapper.readTree(requestResponse).get("id").longValue();

        mockMvc.perform(delete("/friends/requests/{requestId}", requestId)
                        .header("Authorization", "Bearer " + friend.accessToken()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/friends/requests/received")
                        .header("Authorization", "Bearer " + friend.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    private AuthFixture signup(String email, String name) throws Exception {
        String response = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "password1234",
                                  "name": "%s"
                                }
                                """.formatted(email, name)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        return new AuthFixture(
                jsonNode.get("userId").longValue(),
                jsonNode.get("accessToken").asText()
        );
    }

    private void saveRestaurant(String accessToken) throws Exception {
        mockMvc.perform(post("/restaurants/saved")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "provider": "KAKAO",
                                  "providerPlaceId": "friend-place-1",
                                  "name": "친구 초밥집",
                                  "address": "서울시 강남구",
                                  "latitude": 37.1234567,
                                  "longitude": 127.1234567,
                                  "category": "음식점 > 일식",
                                  "phone": "02-000-0000",
                                  "placeUrl": "https://place.map.kakao.com/friend-place-1"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    private record AuthFixture(Long userId, String accessToken) {
    }
}
